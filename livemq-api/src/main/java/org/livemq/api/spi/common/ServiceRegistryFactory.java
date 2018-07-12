package org.livemq.api.spi.common;

import org.livemq.api.spi.Factory;
import org.livemq.api.spi.SpiLoader;

public interface ServiceRegistryFactory extends Factory<ServiceRegistry> {

	static ServiceRegistry create() {
		return SpiLoader.load(ServiceRegistryFactory.class).get();
	}
}
