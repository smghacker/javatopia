package com.vmware.sofia.games.javatopia.server.rest;

import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.GARBAGE_CLEANER;
import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.LINKED_NODE;
import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.ROOT_NODE;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.sofia.games.javatopia.server.core.Graph;
import com.vmware.sofia.games.javatopia.server.rest.exceptions.IamTeapot;
import com.vmware.sofia.games.javatopia.server.rest.latency.UserLocks;
import com.vmware.sofia.games.javatopia.server.tests.tools.TestSuite;

@RestController
public class GarbageCarTrajectory {
   private static Logger logger = Logger.getLogger(GarbageCarTrajectory.class);

   @RequestMapping(value = "/api/sector/{sect}/company/{company}/trajectory", method = RequestMethod.POST)
   public String returnRoots(@PathVariable("sect") int sector,
         @PathVariable String company, @RequestParam String trajectory,
         HttpServletRequest request) {
      long sec = System.currentTimeMillis();
      try {
         String error = null;
         int hash = ("xx" + request.getRemoteHost()).hashCode();
         company = company + hash;
         UserLocks locks = UserLocks.getServiceForSector(sector);
         locks.enterWithUser(company);
         try {
            if (sector < 1 || sector > TestSuite.SECTOR_COUNT) {
               throw new IllegalArgumentException("Sector is not within 1.."
                     + TestSuite.SECTOR_COUNT + " range.");
            }
            Graph gr = TestSuite.getInstance().sector(sector);
            error = changeTrajectory(gr, company, trajectory);
            if (error != null) {
               System.err.println("----- " + error);
               throw new IamTeapot();
            }
         } finally {
            locks.leaveWithUser(company, error != null);
         }

      } catch (RuntimeException t) {
         logger.info(t.getMessage());
         logger.trace("Error:", t);
      }
      return "Done for " + ((System.currentTimeMillis() - sec) / 1000)
            + " seconds. ";
   }

   String changeTrajectory(Graph gr, String company, String trajectory) {
      if (company.length() > 7) {
         company = company.substring(0, 7);
      }
      try {
         gr.enterSuspendedState();
         String[] st = trajectory.split(" ");

         for (int i = 0; i < st.length; i++) {
            Long t = Long.parseLong(st[i]);
            if (gr.getNodeAttribute(t, LINKED_NODE) != null) {
               return "Cannot collect linked node " + t;
            }
            if (gr.getNodeAttribute(t, ROOT_NODE) != null) {
               return "Cannot collect root node " + t;
            }
            if (i > 0) {
               Long f = Long.parseLong(st[i - 1]);
               Set<Long> s = gr.getLinks(f);
               if (!s.contains(t)) {
                  return "Invalid link from " + f + " to " + t;
               }
            }
         }
         // path validated, let's update the scores
         for (int i = 0; i < st.length; i++) {
            Long t = Long.parseLong(st[i]);
            String hero = (String) gr.getNodeAttribute(t, GARBAGE_CLEANER);
            if (hero == null) {
               gr.putNodeAttribute(t, GARBAGE_CLEANER, company);
            }
         }
         return null;
      } finally {
         gr.leaveSuspendedState();
      }
   }
}