package org.livemq.cache.redis.manager;

import org.livemq.api.spi.Spi;
import org.livemq.api.spi.common.CacheManager;
import org.livemq.api.spi.common.CacheManagerFactory;

@Spi
public class RedisCacheManagerFactory implements CacheManagerFactory {

	@Override
	public CacheManager get() {
		return RedisManager.getInstance();
	}

}
