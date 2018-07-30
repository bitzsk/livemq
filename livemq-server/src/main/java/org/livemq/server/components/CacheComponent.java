package org.livemq.server.components;

import org.livemq.api.spi.common.CacheManagerFactory;

/**
 * 
 * @Title CacheComponent
 * @Package org.livemq.server.components
 * @Description 缓存组件
 * @author xinxisimple@163.com
 * @date 2018-07-30 11:48
 * @version 1.0.0
 */
public class CacheComponent extends Component {
	
	@Override
	protected void start() {
		CacheManagerFactory.create().start();
		startNext();
	}

	@Override
	protected void stop() {
		stopNext();
		CacheManagerFactory.create().stop();
	}

}
