package org.livemq.server.s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerList {
	private static final Logger logger = LoggerFactory.getLogger(ServerList.class);
	
	private final Server root = new Server() {
		
		@Override
		protected void stop() {
			stopNext();
			logger.info("server list stopped.");
			logger.info("===========================================================================");
			logger.info("=========================SERVER LIST STOP SUCCESS==========================");
			logger.info("===========================================================================");
		}
		
		@Override
		protected void start() {
			logger.info("server list starting...");
			startNext();
		}
		
		public String getName() {
			return "Root";
		};
	};
	
	public void start() {
		root.start();
	}

	public void stop() {
		root.stop();
	}
	
	public void add(Server server) {
		root.add(server);
	}
	
	public void end() {
		add(new Server() {
			
			@Override
			protected void stop() {
				logger.info("server list stopping...");
			}
			
			@Override
			protected void start() {
				logger.info("server list started.");
				logger.info("===========================================================================");
				logger.info("=========================SERVER LIST START SUCCESS=========================");
				logger.info("===========================================================================");
			}
			
			@Override
			public String getName() {
				return "End";
			}
		});
	}
}
