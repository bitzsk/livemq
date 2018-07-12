package org.livemq.server.server;

import org.livemq.api.spi.common.ServiceRegistryFactory;

public class ServiceDiscoveryServer extends Server {

	@Override
	protected void start() {
		ServiceRegistryFactory.create().start();
		startNext();
	}

	@Override
	protected void stop() {
		stopNext();
		ServiceRegistryFactory.create().stop();
	}

}
