package org.livemq.server.components;

import org.livemq.api.service.Listener;
import org.livemq.api.service.Server;

/**
 * 
 * @Title ServerComponent
 * @Package org.livemq.server.components
 * @Description 服务组件
 * @author xinxisimple@163.com
 * @date 2018-07-30 11:50
 * @version 1.0.0
 */
public class ServerComponent extends Component {

	private Server server;
	
	public ServerComponent(Server server) {
		this.server = server;
	}
	
	@Override
	protected void start() {
		server.init();
		server.start(new Listener() {
			
			@Override
			public void onSuccess(Object ... args) {
				// TODO: DO SOMETHING
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
		// TODO: DO SOMETHING
	}
	
	@Override
	protected String getName() {
		return server == null ? this.getClass().getSimpleName() : server.getClass().getSimpleName();
	}

}
