package org.livemq.zk;

import org.livemq.api.spi.Spi;
import org.livemq.api.spi.common.ServiceRegistry;
import org.livemq.api.spi.common.ServiceRegistryFactory;

@Spi
public class ZKRegistryFactory implements ServiceRegistryFactory {

	@Override
	public ServiceRegistry get() {
		return ZKServiceRegistryAndDiscovery.getInstance();
	}

}
