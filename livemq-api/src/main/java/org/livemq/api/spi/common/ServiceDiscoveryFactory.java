package org.livemq.api.spi.common;

import org.livemq.api.spi.Factory;
import org.livemq.api.spi.SpiLoader;

public interface ServiceDiscoveryFactory extends Factory<ServiceDiscovery> {

	static ServiceDiscovery create() {
		return SpiLoader.load(ServiceDiscoveryFactory.class).get();
	}
}
