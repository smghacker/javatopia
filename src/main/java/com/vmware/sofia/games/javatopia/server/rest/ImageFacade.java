package com.vmware.sofia.games.javatopia.server.rest;

import java.io.*;
import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.*;

import java.util.Iterator;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import com.vmware.sofia.games.javatopia.main.Bootstrap;
import com.vmware.sofia.games.javatopia.server.core.Graph;
import com.vmware.sofia.games.javatopia.server.tests.tools.GraphGenerator;
import com.vmware.sofia.games.javatopia.server.tests.tools.TestSuite;

@Path("/sector/{sect}/image.png")
public class ImageFacade {

	private static final Object SINGLE_EXECUTION_LOCK = new Object();
	private static final int MAX_NODE_CROP_NUMBER = 100;
	private static final int MAX_RELATION = 250;

	// This method is called if TEXT_PLAIN is request
	@GET
	@Produces("image/png")
	public Response getImage(@PathParam("sect") int sector) {
		synchronized (SINGLE_EXECUTION_LOCK) {
			try {
				if (sector < 1 || sector > TestSuite.SECTOR_COUNT) {
					throw new WebApplicationException(404);
				}

				final Graph gr = TestSuite.getInstance().sector(sector);
				Process proc = Runtime.getRuntime().exec(
						Bootstrap.BINARY_ROOT_FOLDER
								+ "/release/bin/dot.exe -Tpng");
				// any error message?
				StreamGobbler errorGobbler = new StreamGobbler(
						proc.getErrorStream(), "ERROR");

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// any output?
				StreamGobbler outputGobbler = new StreamGobbler(
						proc.getInputStream(), "OUTPUT", baos);
				final BufferedOutputStream bufferout = new BufferedOutputStream(
						proc.getOutputStream());
				Runnable run = new Runnable() {
					public void run() {
						PrintWriter commandInput = new PrintWriter(
								(new OutputStreamWriter(bufferout)), true);
						commandInput.print("digraph \"unix\" {");
						commandInput.print("node [shape=circle; color=red];");
						Iterator<Long> it2 = gr.getRootsIterator();
						while (it2.hasNext()) {
							commandInput.print("" + it2.next());
							commandInput.print(";");
						}
						commandInput.print("node[shape=circle; color=black];");
						gr.enterSuspendedState();
						int maxNode = 0;
						try {
							it2 = gr.getNodeIterator();
							while (it2.hasNext()) {
								if (gr.size()>MAX_NODE_CROP_NUMBER) break;
								Long l = it2.next();
								maxNode++;
								boolean linked = gr.getNodeAttribute(l,
										LINKED_NODE) != null;
								if (linked) {
									commandInput.print("" + l);
									commandInput.print(";");
								}
							}
						} finally {
							gr.leaveSuspendedState();
						}
						commandInput.print("node[shape=box; color=gray];\n");
						int maxElement = MAX_RELATION;
						gr.enterSuspendedState();
						try {
							Iterator<Long> it = gr.getNodeIterator();
							label:
							while (it.hasNext()) {
								Long node = it.next();
								String nodeDump = getNodeName(gr, node);
								java.util.Set<Long> to = gr.getLinks(node);
								for (Long toCurrent : to) {
									if (maxElement--<-1) break label;
									commandInput.print("" + nodeDump);
									commandInput.print("-> ");
									commandInput.print(getNodeName(gr,
											toCurrent));
									commandInput.print(";\n");
								}
							}
						} finally {
							gr.leaveSuspendedState();
						}
						commandInput.print("}");
						commandInput.print("\032");
						commandInput.flush();
					}
				};
				new Thread(run).start();
				outputGobbler.start();
				errorGobbler.start();
				proc.waitFor();
				baos.close();

				return Response.ok().entity(baos.toByteArray()).build();
			} catch (Exception e) {
				throw new WebApplicationException(e);
			}
		}
	}

	public String getNodeName(Graph gr, Long t) {
		String hero = (String) gr.getNodeAttribute(t, GARBAGE_CLEANER);
		if (hero != null) {
			return hero.toUpperCase() + "_" + t;
		} else {
			return "" + t;
		}
	}

	class StreamGobbler extends Thread {
		InputStream is;
		String type;
		OutputStream os;

		StreamGobbler(InputStream is, String type) {
			this(is, type, null);
		}

		StreamGobbler(InputStream is, String type, OutputStream redirect) {
			this.is = is;
			this.type = type;
			this.os = redirect;
		}

		public void run() {
			try {
				PrintWriter pw = null;
				if (os != null) {
					int i;
					byte[] buf = new byte[1024];
					int len;
					while ((len = is.read(buf)) > 0) {
						os.write(buf, 0, len);
					}
					return;
				}

				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;

				while ((line = br.readLine()) != null) {
					if (pw != null)
						pw.println(line);
					System.out.println(type + ">" + line);
				}
				if (pw != null)
					pw.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
