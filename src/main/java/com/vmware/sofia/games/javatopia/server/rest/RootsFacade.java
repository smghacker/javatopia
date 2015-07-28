package com.vmware.sofia.games.javatopia.server.rest;

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
public class RootsFacade {

   @RequestMapping("/api/sector/{sect}/roots")
   public String returnRoots(@PathVariable("sect") int sector,
         HttpServletRequest request) {
      if (sector < 1 || sector > TestSuite.SECTOR_COUNT) {
         throw new ResourceNotFoundException();
      }
      String user = request.getRemoteHost();
      UserLocks locks = UserLocks.getServiceForSector(sector);
      locks.getLockLatencyService().startContestLatency(
            "roots: " + user + "@" + sector);
      Graph gr = TestSuite.getInstance().sector(sector);
      StringBuffer results = new StringBuffer();
      Iterator<Long> it = gr.getRootsIterator();
      while (it.hasNext()) {
         results.append(it.next());
         results.append('\n');
      }
      return results.toString();
   }
}