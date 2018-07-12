package org.livemq.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Logs {

	boolean logInit = init();
	
	static boolean init() {
		if(logInit) return true;
		
		// TODO: 初始化日志
		
		return true;
	}
	
	Logger CONSOLE = LoggerFactory.getLogger("console"),
			
	CACHE = LoggerFactory.getLogger("cache"),

	ZK = LoggerFactory.getLogger("zookeeper");
	
}
