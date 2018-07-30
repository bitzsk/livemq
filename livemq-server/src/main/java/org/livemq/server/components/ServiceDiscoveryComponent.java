package org.livemq.server.components;

import org.livemq.api.service.Listener;
import org.livemq.api.spi.common.ServiceDiscoveryFactory;

/**
 * 服务发现组件
 * @Title ServiceDiscoveryComponent
 * @Package org.livemq.server.components
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-30 11:49
 * @version 1.0.0
 */
public class ServiceDiscoveryComponent extends Component {

	@Override
	protected void start() {
		ServiceDiscoveryFactory.create().start(new Listener() {
			
			@Override
			public void onSuccess(Object... args) {
				// TODO Auto-generated method stub
				startNext();
			}
			
			@Override
			public void onFailure(Throwable cause) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	protected void stop() {
		stopNext();
		ServiceDiscoveryFactory.create().stop(new Listener() {
			
			@Override
			public void onSuccess(Object... args) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(Throwable cause) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
