package com.vmware.sofia.games.javatopia.server.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.sofia.games.javatopia.server.rest.latency.LatencyService;
import com.vmware.sofia.games.javatopia.server.tests.tools.TestSuite;

/**
 * Created by kosio on 7/23/14.
 */

@RestController
public class GoodbyeWorld {

   @Autowired
   ApplicationContext ctx;

   @RequestMapping("/api/secured/restart")
   public String restart(HttpServletResponse response) {
      TestSuite.newTestSuite();
      LatencyService.restart();
      try {
         response.sendRedirect("//");
      } catch (IOException e) {
         e.printStackTrace();
      }
      return "Server restarted.";

   }

   @RequestMapping("/api/secured/stop")
   public void stop() throws IOException {
      SpringApplication.exit(ctx);
   }
}