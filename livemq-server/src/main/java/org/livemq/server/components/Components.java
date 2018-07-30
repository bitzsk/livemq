package org.livemq.server.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Title Components
 * @Package org.livemq.server.components
 * @Description 组件集
 * @author xinxisimple@163.com
 * @date 2018-07-30 11:46
 * @version 1.0.0
 */
public class Components {
	private static final Logger logger = LoggerFactory.getLogger(Components.class);
	
	/**
	 * 组件根节点
	 */
	private final Component root = new Component() {
		
		@Override
		protected void stop() {
			stopNext();
			logger.info("Components stopped.");
			logger.info("/");
			logger.info("/");
			logger.info("/");
		}
		
		@Override
		protected void start() {
			logger.info("Components starting...");
			startNext();
		}
		
		protected String getName() {
			return "ROOT";
		};
	};
	
	/**
	 * 组件添加完成必须调用的方法
	 */
	public void end() {
		add(new Component() {
			
			@Override
			protected void stop() {
				logger.info("Components stopping...");
			}
			
			@Override
			protected void start() {
				logger.info("Components started.");
				logger.info("/");
				logger.info("/");
				logger.info("/");
			}
			
			@Override
			protected String getName() {
				return "END";
			}
		});
	}
	
	/**
	 * 启动组件集
	 */
	public void start() {
		root.start();
	}
	
	/**
	 * 停止组件集
	 */
	public void stop() {
		root.stop();
	}
	
	/**
	 * 向组件集中添加新的组件
	 * @param component
	 */
	public void add(Component component) {
		root.add(component);
	}
	
}
