package org.livemq.cache.redis.manager;

import org.livemq.api.spi.common.CacheManager;
import org.livemq.common.log.Logs;

public class RedisManager implements CacheManager {

	private static volatile RedisManager instance;
	
	public static RedisManager getInstance() {
		if(instance == null) {
			synchronized (RedisManager.class) {
				if(instance == null) {
					instance = new RedisManager();
				}
			}
		}
		return instance;
	}
	
	private RedisManager() {}
	
	@Override
	public void init() {
		Logs.CACHE.info("begin init redis...");
		
		
		Logs.CACHE.info("init redis success.");
	}

	@Override
	public void destroy() {
		Logs.CACHE.info("redis destroy.");
	}

}
