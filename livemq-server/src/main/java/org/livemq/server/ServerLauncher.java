package org.livemq.server;

import org.livemq.server.s.CacheManagerServer;
import org.livemq.server.s.ServerList;
import org.livemq.server.s.ServiceDiscoveryServer;
import org.livemq.server.s.ServiceRegistryServer;

public class ServerLauncher {

	private ServerList sl;
	
	public void init() {
		
		// TODO:初始化配置文件
		
		if(sl == null) sl = new ServerList();
		
		// 1.初始化缓存服务
		sl.add(new CacheManagerServer());
		// 2.启动服务注册
		sl.add(new ServiceRegistryServer());
		// 3.启动服务发现
		sl.add(new ServiceDiscoveryServer());
		// 4.启动 netty 服务
		// 5.启动 websocket 服务
		// 6.启动监控服务
		sl.end();
	}
	
	public void start() {
		sl.start();
	}

	public void stop() {
		sl.stop();
	}
}
