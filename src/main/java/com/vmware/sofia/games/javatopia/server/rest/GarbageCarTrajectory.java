package com.vmware.sofia.games.javatopia.server.rest;

import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.vmware.sofia.games.javatopia.server.core.Graph;
import com.vmware.sofia.games.javatopia.server.rest.latency.LatencyService;
import com.vmware.sofia.games.javatopia.server.rest.latency.UserLocks;
import com.vmware.sofia.games.javatopia.server.tests.tools.GraphGenerator;
import com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes;
import com.vmware.sofia.games.javatopia.server.tests.tools.TestSuite;
import static com.vmware.sofia.games.javatopia.server.rest.latency.UserLocks.*;
import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.*;

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/sector/{sect}/company/{company}/trajectory")
public class GarbageCarTrajectory {

	// This method is called if TEXT_PLAIN is request
	@POST
	@Produces("text/plain")
	public String returnRoots(@PathParam("sect") int sector,
			@PathParam("company") String company,
			@FormParam("trajectory") String trajectory,
			@Context HttpServletRequest request) {
		try {
			String error = null;
			int hash = ("xx"+request.getRemoteHost()).hashCode();
			String res = ""+(hash%99);
			company = company + hash;
			long sec = System.currentTimeMillis();
			UserLocks locks = UserLocks.getServiceForSector(sector);
			try {
				if (!locks.enterWithUser(company)) {
					throw new WebApplicationException(429);
				}
				if (sector < 1 || sector > TestSuite.SECTOR_COUNT) {
					throw new WebApplicationException(404);
				}
				Graph gr = TestSuite.getInstance().sector(sector);
				error = changeTrajectory(gr, company, trajectory);
				if (error != null) {
					System.err.println("----- "+error);
					throw new WebApplicationException(418);
				}
			} finally {
				locks.leaveWithUser(company, error != null);
			}
			return "Done for "+((System.currentTimeMillis()-sec)/1000)+" seconds. ";
		} catch (RuntimeException t) {
			t.printStackTrace();
			throw t;
		}
	}

	String changeTrajectory(Graph gr, String company, String trajectory) {
		if (company.length()>7) company = company.substring(0, 7);
		try {
			gr.enterSuspendedState();
			String[] st = trajectory.split(" ");
	
			for (int i = 0; i < st.length; i++) {
				Long t = Long.parseLong(st[i]);
				if (gr.getNodeAttribute(t, LINKED_NODE) != null) {
					return "Cannot collect linked node "+t;
				}
				if (gr.getNodeAttribute(t, ROOT_NODE) != null) {
					return "Cannot collect root node "+t;
				}
				if (i > 0) {
					Long f = Long.parseLong(st[i - 1]);
					Set<Long> s = gr.getLinks(f);
					if (!s.contains(t)) {
				   		return "Invalid link from "+f+" to "+t;
					}
				}
			}
			// path validated, let's update the scores
			for (int i = 0; i < st.length; i++) {
				Long t = Long.parseLong(st[i]);
				String hero = (String)gr.getNodeAttribute(t, GARBAGE_CLEANER);
				if (hero==null) {
					gr.putNodeAttribute(t, GARBAGE_CLEANER, company);
				}
			}
			return null;
		} finally {
			gr.leaveSuspendedState();
		}
	}

}