package org.livemq.server;

import org.livemq.common.log.Logs;

public class Main {

	public static void main(String[] args) {
		Logs.init();
		Logs.CONSOLE.info("launch livemq server...");
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
                	Logs.CONSOLE.error("mpush server stop ex", e);
                }
                Logs.CONSOLE.info("jvm exit, all service stopped.");
			}
		}, "livemq-shutdown-hook-thread"));
	}
}
