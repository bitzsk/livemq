package org.livemq.server;

import org.livemq.server.server.CacheManagerServer;
import org.livemq.server.server.ServerChain;
import org.livemq.server.server.ServiceDiscoveryServer;
import org.livemq.server.server.ServiceRegistryServer;

public class ServerLauncher {

	private ServerChain chain;
	
	public void init() {
		
		if(chain == null) {
			chain = new ServerChain();
		}
		
		chain
			.setNext(new CacheManagerServer()) // 1.初始化缓存服务
			.setNext(new ServiceRegistryServer()) // 2.启动服务注册
			.setNext(new ServiceDiscoveryServer()) // 3.启动服务发现
			// 4.启动 netty 服务
			// 5.启动 websocket 服务
			// 6.启动监控服务
			.end();
	}
	
	public void start() {
		chain.start();
	}

	public void stop() {
		chain.stop();
	}
}
