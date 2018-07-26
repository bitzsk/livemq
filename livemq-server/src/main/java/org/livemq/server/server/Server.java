package org.livemq.server.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Server {
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	protected Server next;
	
	protected abstract void start();
	
	protected abstract void stop();
	
	public void startNext() {
		if(next != null) {
			logger.info("start server [{}]", getNextName());
			next.start();
		}
	}

	public void stopNext() {
		if(next != null) {
			next.stop();
			logger.info("stopped server [{}]", getNextName());
		}
	}
	
	public Server setNext(Server server) {
		this.next = server;
		return server;
	}
	
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	public String getNextName() {
		return next.getName();
	}
	
}
