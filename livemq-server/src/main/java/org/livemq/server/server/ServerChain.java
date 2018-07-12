package org.livemq.server.server;

import org.livemq.common.log.Logs;

public class ServerChain {

	private final Server server = new Server() {
		
		@Override
		protected void start() {
			Logs.CONSOLE.info("server chain startting...");
			startNext();
		}
		
		@Override
		protected void stop() {
			stopNext();
			Logs.CONSOLE.info("server chain stopped.");
			Logs.CONSOLE.info("=======================================================================");
			Logs.CONSOLE.info("======================LIVEMQ SERVER STOPPED SUCCESS====================");
			Logs.CONSOLE.info("=======================================================================");
		}
		
		@Override
		public String getName() {
			return "FirstServer";
		}
		
	};
	
	private Server last = server;
	
	public void start() {
		server.start();
	}

	public void stop() {
		server.stop();
	}
	
	public void end() {
		setNext(new Server() {

			@Override
			protected void start() {
				Logs.CONSOLE.info("server chain started.");
				Logs.CONSOLE.info("=======================================================================");
				Logs.CONSOLE.info("======================LIVEMQ SERVER START SUCCESS======================");
				Logs.CONSOLE.info("=======================================================================");
			}
			
			@Override
			protected void stop() {
				Logs.CONSOLE.info("server chain stopping...");
			}
			
			@Override
			public String getName() {
				return "LastServer";
			}
			
		});
	}
	
	public ServerChain setNext(Server server) {
		this.last = last.setNext(server);
		return this;
	}
	
}
