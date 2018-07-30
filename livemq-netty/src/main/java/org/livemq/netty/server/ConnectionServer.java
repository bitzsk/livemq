package org.livemq.netty.server;

import org.livemq.api.service.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Title ConnectionServer
 * @Package org.livemq.core.server
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-30 11:56
 * @version 1.0.0
 */
public final class ConnectionServer extends NettyTCPServer {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionServer.class);
	
	public ConnectionServer(int port) {
		super(port);
	}
	
	@Override
	public void init() {
		logger.info("do init");
	}
	
	@Override
	public void start(Listener listener) {
		//TODO: to start
		logger.info("do start");
		listener.onSuccess();
	}
	
	@Override
	public void stop(Listener listener) {
		//TODO: to stop
		logger.info("do stop");
		listener.onFailure(new Exception("is error"));
	}

}
