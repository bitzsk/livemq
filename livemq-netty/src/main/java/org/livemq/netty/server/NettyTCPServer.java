package org.livemq.netty.server;

import org.livemq.api.service.Listener;
import org.livemq.api.service.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.EventLoopGroup;

public abstract class NettyTCPServer implements Server {
	private static final Logger logger = LoggerFactory.getLogger(NettyTCPServer.class);
	
	private static final String LOCAL = "127.0.0.1";
	private final String host;
	private final int port;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	public NettyTCPServer(int port) {
		this(LOCAL, port);
	}

	public NettyTCPServer(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void start(Listener listener) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void stop(Listener listener) {
		// TODO Auto-generated method stub
		
	}
}
