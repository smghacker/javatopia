package com.vmware.sofia.games.javatopia.server.rest.latency;

import static com.vmware.sofia.games.javatopia.server.tests.tools.Log.log;

import java.util.Timer;
import java.util.TimerTask;

import com.vmware.sofia.games.javatopia.server.tests.tools.Log;

public class LatencyService {
	
	private static final String INITIAL_PAUSE_PROPERTY = "com.vmware.sofia.games.javatopia.pause";
	
   private static Object WAIT_LOCK = new Object();
	
	private volatile static long startContestTime = 0;
	
	private int DEFAULT_PAUSE = 10000;
	
	
	public void startContestLatency(final String id) {
		log(">>>"+id);
	 	synchronized (WAIT_LOCK) {
			if (startContestTime==0) {
				int pause = DEFAULT_PAUSE;
				String initial = System.getProperty(INITIAL_PAUSE_PROPERTY);
				if (initial != null) {
					pause = Integer.parseInt(initial);
				}
				startContestTime = System.currentTimeMillis()+pause;
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						synchronized(WAIT_LOCK) {
							WAIT_LOCK.notifyAll();
							Log.log("!!! STARTING THE GAME ! "+id);
						}
					}
				};
				Timer timer = new Timer();
				timer.schedule(task, pause+1000);
			}
			while (System.currentTimeMillis()<startContestTime) {
				try {
					WAIT_LOCK.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		log("<<<"+id);
	}
	
	public void endTrajectoryLatency() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void endTrajectoryLatencyFailure() {
		try {
			Thread.sleep(DEFAULT_PAUSE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

    public static void restart() {
        startContestTime = 0;
    }

}
