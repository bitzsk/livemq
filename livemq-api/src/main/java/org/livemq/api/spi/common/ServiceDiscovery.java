package org.livemq.api.spi.common;

import java.util.List;

import org.livemq.api.service.Service;

public interface ServiceDiscovery extends Service {

	List<String> lookup(String path);
}
