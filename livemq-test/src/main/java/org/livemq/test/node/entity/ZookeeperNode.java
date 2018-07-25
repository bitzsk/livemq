package org.livemq.test.node.entity;

import org.livemq.test.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperNode extends Node {
	private static final Logger logger = LoggerFactory.getLogger(ZookeeperNode.class);
	
	public ZookeeperNode(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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
	}

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

}
