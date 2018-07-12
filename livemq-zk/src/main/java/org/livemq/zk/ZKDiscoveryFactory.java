package org.livemq.zk;

import org.livemq.api.spi.Spi;
import org.livemq.api.spi.common.ServiceDiscovery;
import org.livemq.api.spi.common.ServiceDiscoveryFactory;

@Spi
public class ZKDiscoveryFactory implements ServiceDiscoveryFactory {

	@Override
	public ServiceDiscovery get() {
		return ZKServiceRegistryAndDiscovery.getInstance();
	}

}
