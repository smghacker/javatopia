package com.vmware.sofia.games.javatopia.server.tests.tools;

import java.util.Date;

import org.apache.log4j.Logger;

import com.vmware.sofia.games.javatopia.server.rest.latency.LatencyService;

public class Log {
	private static Logger logger = Logger.getLogger(LatencyService.class);
	
	public static void log(String log) {
		System.err.println("["+new Date()+"] "+log);
		logger.info(log);
	}
}
