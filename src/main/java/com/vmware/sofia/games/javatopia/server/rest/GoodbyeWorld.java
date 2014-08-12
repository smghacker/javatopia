package com.vmware.sofia.games.javatopia.server.rest;


import com.vmware.sofia.games.javatopia.server.rest.latency.LatencyService;
import com.vmware.sofia.games.javatopia.server.tests.tools.GraphGenerator;
import com.vmware.sofia.games.javatopia.server.tests.tools.TestSuite;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**h
 * Created by kosio on 7/23/14.
 */

@Path("/secured")
@Component
public class GoodbyeWorld {
    @Context
    HttpServletResponse _currentResponse;

    @GET
    @Path("/restart")
    public String restart() {
        TestSuite.newTestSuite();
        LatencyService.restart();
        try {
            _currentResponse.sendRedirect("//");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Server restarted.";

    }

    @GET
    @Path("/stop")
    public String stop() {
        System.exit(0);
        return null;
    }
}