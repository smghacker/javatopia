package com.vmware.sofia.games.javatopia.server.rest.latency;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import com.vmware.sofia.games.javatopia.server.tests.tools.Log;

public class UserLocks {
	private static ConcurrentHashMap<Integer, UserLocks> userLocks = new ConcurrentHashMap<Integer, UserLocks>();
	private HashSet<String> locked = new HashSet<String>();
	public LatencyService latencyService = new LatencyService();
	
	public LatencyService getLockLatencyService() {
		return latencyService;
	}



	public static UserLocks getServiceForSector(int sector) {
		Log.log("entering SECTOR: "+sector);
		UserLocks lock = userLocks.get(sector);
		if (lock==null) {
			lock = new UserLocks();
			Log.log("locked, SECTOR: "+sector);
			userLocks.putIfAbsent(sector, lock);
		}
		lock = userLocks.get(sector);
		return lock;
	}
	
	public synchronized boolean enterWithUser(String user) {
		if (locked.contains(user)) return false;
		locked.add(user);
		return true;
	}
	
	public synchronized void leaveWithUser(String user, Boolean longDelay) {
		if (!locked.contains(user)) throw new IllegalStateException();
		locked.remove(user);
		if (longDelay==null) {
			return;
		}
		if (longDelay) {
			latencyService.endTrajectoryLatencyFailure();
		} else {
			latencyService.endTrajectoryLatency();
		}
	}
	
}
