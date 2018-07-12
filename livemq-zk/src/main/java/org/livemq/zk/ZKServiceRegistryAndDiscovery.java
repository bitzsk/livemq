package org.livemq.zk;

import java.util.List;

import org.livemq.api.spi.common.ServiceDiscovery;
import org.livemq.api.spi.common.ServiceRegistry;
import org.livemq.common.log.Logs;

public class ZKServiceRegistryAndDiscovery implements ServiceRegistry, ServiceDiscovery {

	private static volatile ZKServiceRegistryAndDiscovery instance;
	
	public static ZKServiceRegistryAndDiscovery getInstance() {
		if(instance == null) {
			synchronized (ZKServiceRegistryAndDiscovery.class) {
				if(instance == null) {
					instance = new ZKServiceRegistryAndDiscovery();
				}
			}
		}
		return instance;
	}
	
	private ZKServiceRegistryAndDiscovery() {}
	
	@Override
	public List<String> lookup(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deregister() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		Logs.ZK.info("begin init zookeeper...");
		
		
		
		Logs.ZK.info("init zookeeper success.");
	}

	@Override
	public void start() {
		if(!isRunning()) {
			init();
			
			Logs.ZK.info("startting zookeeper...");
			
			
			Logs.ZK.info("started zookeeper.");
		}else {
			Logs.ZK.info("zookeeper is not run.");
		}
	}

	@Override
	public void stop() {
		if(isRunning()) {
			Logs.ZK.info("stopping zookeeper...");
			
			
			Logs.ZK.info("stopped zookeeper.");
		}else {
			Logs.ZK.info("zookeeper is not run.");
		}
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

}
