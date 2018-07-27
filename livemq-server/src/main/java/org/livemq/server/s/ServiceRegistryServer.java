package org.livemq.server.s;

import org.livemq.api.spi.common.ServiceRegistryFactory;

public class ServiceRegistryServer extends Server {

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
