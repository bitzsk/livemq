package org.livemq.api.spi.common;

import java.util.List;

import org.livemq.api.service.Service;

/**
 * 
 * @Title CacheManager
 * @Package org.livemq.api.spi.common
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-23 17:52
 * @version 1.0.0
 */
public interface CacheManager {

	void start();
	
	void stop();
	
	String set(String key, String value);
	
	String get(String key);
	
	Boolean exists(String key);
	
	Long expire(String key, int seconds);
	
	Long ttl(String key);

	Long incr(String key);

	Long hset(String key, String field, String value);
	
	String hget(String key, String field);
	
	Long hdel(String key, String ... field);
	
	Boolean hexists(String key, String field);
	
	List<String> hvals(String key);
	
	Long del(String key);
	
}
