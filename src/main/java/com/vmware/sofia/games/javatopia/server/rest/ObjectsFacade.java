package com.vmware.sofia.games.javatopia.server.rest;

import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.GARBAGE_CLEANER;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.sofia.games.javatopia.server.core.Graph;
import com.vmware.sofia.games.javatopia.server.rest.exceptions.ResourceNotFoundException;
import com.vmware.sofia.games.javatopia.server.rest.latency.UserLocks;
import com.vmware.sofia.games.javatopia.server.tests.tools.TestSuite;

@RestController
public class ObjectsFacade {
   @RequestMapping("/api/sector/{sect}/objects")
   public String objects(@PathVariable("sect") int sector,
         HttpServletRequest request) {
      if (sector < 1 || sector > TestSuite.SECTOR_COUNT) {
         throw new ResourceNotFoundException();
      }
      StringBuffer results = new StringBuffer();

      String user = request.getRemoteHost();
      UserLocks locks = UserLocks.getServiceForSector(sector);
      locks.getLockLatencyService().startContestLatency(
            "objects: " + user + "@" + sector);
      Graph gr = TestSuite.getInstance().sector(sector);
      try {
         gr.enterSuspendedState();
         Iterator<Long> it = gr.getNodeIterator();
         nodes: while (it.hasNext()) {
            Long node = it.next();
            String hero = (String) gr.getNodeAttribute(node, GARBAGE_CLEANER);
            if (hero != null)
               continue nodes;
            java.util.Set<Long> to = gr.getLinks(node);
            links: for (Long toCurrent : to) {
               hero = (String) gr.getNodeAttribute(toCurrent, GARBAGE_CLEANER);
               if (hero != null)
                  continue links;
               results.append(node);
               results.append(' ');
               results.append(toCurrent);
               results.append('\n');
            }
         }
      } finally {
         gr.leaveSuspendedState();
      }
      return results.toString();
   }
}