package org.livemq.cache.redis.manager;

import org.livemq.api.spi.common.CacheManager;
import org.livemq.cache.redis.connection.RedisConnectionFactory;
import org.livemq.common.data.RedisNode;
import org.livemq.common.log.Logs;

/**
 * 
 * @Title RedisManager
 * @Package org.livemq.cache.redis.manager
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-21 19:08
 * @version 1.0.0
 */
public class RedisManager implements CacheManager {

	private static volatile RedisManager instance;
	
	private final RedisConnectionFactory factory = new RedisConnectionFactory();
	
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
		factory.setPassword("123456");
		factory.setNode(new RedisNode("192.168.1.10", 6379));
		factory.init();
		Logs.CACHE.info("init redis success.");
	}

	@Override
	public void destroy() {
		if(factory != null) factory.destroy();
	}

	@Override
	public void set(String key, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void set(String key, String value, int expireTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
