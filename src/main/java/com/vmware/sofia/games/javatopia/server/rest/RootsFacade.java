package com.vmware.sofia.games.javatopia.server.rest;

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
@Path("/sector/{sect}/roots")
public class RootsFacade {

	
	// This method is called if TEXT_PLAIN is request
	@GET	
	@Produces("text/plain")
	public String returnRoots(
			@PathParam("sect") int sector,
			@Context HttpServletRequest request) {
		if (sector<1 || sector>TestSuite.SECTOR_COUNT) {
			throw new WebApplicationException(404);
		}
		String user = request.getRemoteHost();
		UserLocks locks = UserLocks.getServiceForSector(sector);
		locks.getLockLatencyService().startContestLatency("roots: "+user+"@"+sector);
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