package org.livemq.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		logger.info("launch livemq server...");
		ServerLauncher launcher = new ServerLauncher();
		launcher.init();
		launcher.start();
		addHook(launcher);
	}

	private static void addHook(ServerLauncher launcher) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
                    launcher.stop();
                } catch (Exception e) {
                	logger.error("mpush server stop ex", e);
                }
				logger.info("jvm exit, all service stopped.");
			}
		}, "livemq-shutdown-hook-thread"));
	}
}
