package org.livemq.server.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerChain {
	private static final Logger logger = LoggerFactory.getLogger(ServerChain.class);
	
	private final Server server = new Server() {
		
		@Override
		protected void start() {
			logger.info("server chain startting...");
			startNext();
		}
		
		@Override
		protected void stop() {
			stopNext();
			logger.info("server chain stopped.");
			logger.info("=======================================================================");
			logger.info("======================LIVEMQ SERVER STOPPED SUCCESS====================");
			logger.info("=======================================================================");
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
				logger.info("server chain started.");
				logger.info("=======================================================================");
				logger.info("======================LIVEMQ SERVER START SUCCESS======================");
				logger.info("=======================================================================");
			}
			
			@Override
			protected void stop() {
				logger.info("server chain stopping...");
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
