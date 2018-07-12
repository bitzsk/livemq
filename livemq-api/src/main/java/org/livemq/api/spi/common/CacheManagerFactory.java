package org.livemq.api.spi.common;

import org.livemq.api.spi.Factory;
import org.livemq.api.spi.SpiLoader;

public interface CacheManagerFactory extends Factory<CacheManager> {

	static CacheManager create() {
		return SpiLoader.load(CacheManagerFactory.class).get();
	}
}
