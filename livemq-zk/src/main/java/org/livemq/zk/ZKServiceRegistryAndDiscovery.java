package org.livemq.zk;

import java.util.List;

import org.livemq.api.service.Listener;
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
	public void start(Listener listener) {
		if(isRunning()) {
			listener.onSuccess();
		}else {
			//TODO: do start
		}
	}

	@Override
	public void stop(Listener listener) {
		if(isRunning()) {
			//TODO: do stop
		}else {
			listener.onSuccess();
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

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

}
