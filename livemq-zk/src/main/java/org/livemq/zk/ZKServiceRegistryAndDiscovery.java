package org.livemq.zk;

import java.util.List;

import org.livemq.api.spi.common.ServiceDiscovery;
import org.livemq.api.spi.common.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKServiceRegistryAndDiscovery implements ServiceRegistry, ServiceDiscovery {
	private static final Logger logger = LoggerFactory.getLogger(ZKServiceRegistryAndDiscovery.class);
	
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
	public void start() {
		if(!isRunning()) {
			
			RUNNING.set(true);
		}else {
			logger.info("zookeeper is running.");
		}
	}

	@Override
	public void stop() {
		if(isRunning()) {
			
			RUNNING.set(false);
		}else {
			logger.info("zookeeper is not run.");
		}
	}
	
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

}
