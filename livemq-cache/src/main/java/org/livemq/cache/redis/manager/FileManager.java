package org.livemq.cache.redis.manager;

import java.util.List;

import org.livemq.api.spi.common.CacheManager;

/**
 * 
 * @Title FileManager
 * @Package org.livemq.cache.redis.manager
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-23 17:52
 * @version 1.0.0
 */
public class FileManager implements CacheManager {

	private static volatile FileManager instance;
	
	public static FileManager getInstance() {
		if(instance == null) {
			synchronized (FileManager.class) {
				if(instance == null) {
					instance = new FileManager();
				}
			}
		}
		return instance;
	}
	
	private FileManager() {}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String set(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean exists(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long expire(String key, int seconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long ttl(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long incr(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hset(String key, String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String hget(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hdel(String key, String... field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean hexists(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> hvals(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long del(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
