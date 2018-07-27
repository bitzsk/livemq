package org.livemq.api.service;

import java.util.concurrent.atomic.AtomicBoolean;

public interface Service {

	AtomicBoolean RUNNING = new AtomicBoolean(false);
	
	void start();
	
	void stop();
	
	/**
	 * 返回当前服务是否运行中
	 * @return
	 */
	default boolean isRunning() {
		return RUNNING.get();
	};
}
