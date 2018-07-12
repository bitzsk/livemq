package org.livemq.cache.redis.manager;

import org.livemq.api.spi.common.CacheManager;
import org.livemq.common.log.Logs;

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
	public void init() {
		Logs.CACHE.info("begin init file...");
		
		
		Logs.CACHE.info("init file success.");
	}

	@Override
	public void destroy() {
		Logs.CACHE.info("file destroy.");
	}

}
