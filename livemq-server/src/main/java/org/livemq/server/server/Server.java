package org.livemq.server.server;

import org.livemq.common.log.Logs;

public abstract class Server {

	protected Server next;
	
	protected abstract void start();
	
	protected abstract void stop();
	
	public void startNext() {
		if(next != null) {
			Logs.CONSOLE.info("start server [{}]", getNextName());
			next.start();
		}
	}

	public void stopNext() {
		if(next != null) {
			next.stop();
			Logs.CONSOLE.info("stopped server [{}]", getNextName());
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
