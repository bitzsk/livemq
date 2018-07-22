package org.livemq.api.spi.common;

public interface CacheManager {

	void init();
	
	void destroy();
	
	void set(String key, String value);
	
	void set(String key, String value, int expireTime);
	
	String get(String key);
}
