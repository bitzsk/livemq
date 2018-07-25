package org.livemq.test.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Title 自定义链表
 * @Package org.livemq.test.node
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-25 17:17
 * @version 1.0.0
 */
public class List {
	private static final Logger logger = LoggerFactory.getLogger(List.class);
	
	// 根节点，也就是头节点
	private Node root = new Node("root") {
		
		@Override
		protected void stop() {
			stopNext();
			
			logger.info("node [{}] begin stop", getName());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				logger.error("node [{}] stop ex", getName(), e);
			}
			logger.info("node [{}] stop success", getName());
			
			
			logger.info("=========================SERVER STOP=========================");
		}
		
		@Override
		protected void start() {
			logger.info("=========================SERVER START=========================");
			
			
			logger.info("node [{}] begin start", getName());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				logger.error("node [{}] start ex", getName(), e);
			}
			logger.info("node [{}] start success", getName());
			
			startNext();
		}
	};
	
	public void end() {
		add(new Node("end") {
			
			@Override
			protected void stop() {
				stopNext();
				
				logger.info("node [{}] begin stop", getName());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error("node [{}] stop ex", getName(), e);
				}
				logger.info("node [{}] stop success", getName());
			}
			
			@Override
			protected void start() {
				logger.info("node [{}] begin start", getName());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error("node [{}] start ex", getName(), e);
				}
				logger.info("node [{}] start success", getName());
				
				startNext();
				
				System.err.println();
			}
		});
	}
	
	public void start() {
		root.start();
	}

	public void stop() {
		root.stop();
	}
	
	public void add(Node node) {
		if(root == null) {
			root = node;
		}else {
			root.add(node);
		}
	}
	
	public boolean contains(String name) {
		if(root.getName().equals(name)) {
			return true;
		}else {
			return root.contains(name);
		}
	}
	
	public void remove(String name) {
		if(contains(name)) {
			if(root.getName().equals(name)) {
				root = root.getNext();
			}else {
				root.remove(name);
			}
		}
	}
	
}
