package com.vmware.sofia.games.javatopia.server.rest;

import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.GARBAGE_CLEANER;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.vmware.sofia.games.javatopia.server.core.Graph;
import com.vmware.sofia.games.javatopia.server.rest.latency.UserLocks;
import com.vmware.sofia.games.javatopia.server.tests.tools.GraphGenerator;
import com.vmware.sofia.games.javatopia.server.tests.tools.TestSuite;

//  The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/sector/{sect}/objects")
public class ObjectsFacade {

	// This method is called if TEXT_PLAIN is request
	@GET	
	@Produces("text/plain")
	public String objects(
			@PathParam("sect") int sector,
			@Context HttpServletRequest request) {
		if (sector<1 || sector>TestSuite.SECTOR_COUNT) {
			throw new WebApplicationException(404);
		}
		StringBuffer results = new StringBuffer();

		String user = request.getRemoteHost();
		UserLocks locks = UserLocks.getServiceForSector(sector);
		locks.getLockLatencyService().startContestLatency("objects: "+user+"@"+sector);
		try {
			Graph gr = TestSuite.getInstance().sector(sector);
			try {
				gr.enterSuspendedState();
				Iterator<Long> it = gr.getNodeIterator();
				nodes:
				while (it.hasNext()) {
					Long node = it.next();
					String hero = (String)gr.getNodeAttribute(node, GARBAGE_CLEANER);
					if (hero != null) continue nodes;
					java.util.Set<Long> to = gr.getLinks(node);
					links:
					for (Long toCurrent: to) {
						hero = (String)gr.getNodeAttribute(toCurrent, GARBAGE_CLEANER);
						if (hero != null) continue links;
						results.append(node);
						results.append(' ');
						results.append(toCurrent);
						results.append('\n');
					}					
				}
			} finally {
				gr.leaveSuspendedState();
			}
		} finally {
		}
		return results.toString();
	}


}