package org.livemq.api.spi.common;

import org.livemq.api.service.Service;

public interface ServiceRegistry extends Service {

	void register();

    void deregister();
}
