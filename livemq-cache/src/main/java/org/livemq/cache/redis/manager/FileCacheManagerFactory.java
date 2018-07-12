package org.livemq.cache.redis.manager;

import org.livemq.api.spi.Spi;
import org.livemq.api.spi.common.CacheManager;
import org.livemq.api.spi.common.CacheManagerFactory;

@Spi(order = 1)
public class FileCacheManagerFactory implements CacheManagerFactory {

	@Override
	public CacheManager get() {
		return FileManager.getInstance();
	}

}
