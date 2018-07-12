package org.livemq.api.service;

public interface Service {

	void init();
	
	void start();
	
	void stop();
	
	boolean isRunning();
}
