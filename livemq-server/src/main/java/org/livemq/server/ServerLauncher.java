package org.livemq.server;

import org.livemq.netty.server.ConnectionServer;
import org.livemq.netty.server.WebsocketServer;
import org.livemq.server.components.CacheComponent;
import org.livemq.server.components.Components;
import org.livemq.server.components.MonitorComponent;
import org.livemq.server.components.ServerComponent;
import org.livemq.server.components.ServiceDiscoveryComponent;
import org.livemq.server.components.ServiceRegistryComponent;

public class ServerLauncher {

	private Components components;
	
	public void init() {
		// 1.初始化配置文件
		
		// 2.初始化组件中心
		if(components == null) components = new Components();
		
		// 3.添加组件
		components.add(new CacheComponent());
		components.add(new ServiceRegistryComponent());
		components.add(new ServiceDiscoveryComponent());
		components.add(new ServerComponent(new ConnectionServer(8088)));
		components.add(new ServerComponent(new WebsocketServer(8089)));
		components.add(new MonitorComponent());
		components.end();
	}
	
	public void start() {
		components.start();
	}

	public void stop() {
		components.stop();
	}
}
