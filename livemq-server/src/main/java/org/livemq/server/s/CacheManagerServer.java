package org.livemq.server.s;

import org.livemq.api.spi.common.CacheManagerFactory;

public class CacheManagerServer extends Server {

	@Override
	protected void start() {
		CacheManagerFactory.create().start();
		startNext();
	}

	@Override
	protected void stop() {
		stopNext();
		CacheManagerFactory.create().stop();
	}

}
