package org.livemq.server.components;

import org.livemq.api.service.Listener;
import org.livemq.api.spi.common.ServiceRegistryFactory;

/**
 * 
 * @Title ServiceRegistryComponent
 * @Package org.livemq.server.components
 * @Description 服务注册组件
 * @author xinxisimple@163.com
 * @date 2018-07-30 11:48
 * @version 1.0.0
 */
public class ServiceRegistryComponent extends Component {

	@Override
	protected void start() {
		//TODO: DO SOMETHING
		ServiceRegistryFactory.create().start(new Listener() {
			
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
		ServiceRegistryFactory.create().stop(new Listener() {
			
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
