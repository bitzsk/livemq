package org.livemq.server.server;

import org.livemq.api.spi.common.CacheManagerFactory;

public class CacheManagerServer extends Server {

	@Override
	protected void start() {
		CacheManagerFactory.create().init();
		startNext();
	}

	@Override
	protected void stop() {
		stopNext();
		CacheManagerFactory.create().destroy();
	}

}
