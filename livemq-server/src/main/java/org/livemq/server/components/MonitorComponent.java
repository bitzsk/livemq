package org.livemq.server.components;

/**
 * 
 * @Title MonitorComponent
 * @Package org.livemq.server.components
 * @Description 监控组件
 * @author xinxisimple@163.com
 * @date 2018-07-30 11:50
 * @version 1.0.0
 */
public class MonitorComponent extends Component {

	@Override
	protected void start() {
		//TODO: DO SOMETHING
		startNext();
	}

	@Override
	protected void stop() {
		stopNext();
		//TODO: DO SOMETHING
	}

}
