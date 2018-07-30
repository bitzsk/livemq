package org.livemq.server.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Title Component
 * @Package org.livemq.server.components
 * @Description 超级组件
 * @author xinxisimple@163.com
 * @date 2018-07-30 11:41
 * @version 1.0.0
 */
public abstract class Component {
	private static final Logger logger = LoggerFactory.getLogger(Component.class);
	
	protected Component next;
	
	/**
	 * 1. do something <br>
	 * 2. do start next
	 */
	protected abstract void start();
	
	/**
	 * 1. do stop next <br>
	 * 2. do something
	 */
	protected abstract void stop();
	
	public void startNext() {
		if(next != null) {
			logger.info("start component [{}]", getNextName());
			next.start();
		}
	}
	
	public void stopNext() {
		if(next != null) {
			next.stop();
			logger.info("stopped component [{}]", getNextName());
		}
	}
	
	public void add(Component component) {
		if(next == null) {
			next = component;
		}else {
			next.add(component);
		}
	}
	
	protected String getName() {
		return this.getClass().getSimpleName();
	}
	
	protected String getNextName() {
		return next == null ? "NULL" : next.getName();
	}
}
