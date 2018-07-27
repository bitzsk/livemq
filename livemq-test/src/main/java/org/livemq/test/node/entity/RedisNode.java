package org.livemq.test.node.entity;

import org.livemq.test.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisNode extends Node {
	private static final Logger logger = LoggerFactory.getLogger(RedisNode.class);
	
	public RedisNode(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void start() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			logger.error("node [{}] start ex", getName(), e);
		}
		logger.info("server [{}] started.", getName());
		
		startNext();
	}

	@Override
	protected void stop() {
		stopNext();
		
		logger.info("server [{}] stopping...", getName());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			logger.error("node [{}] stop ex", getName(), e);
		}
	}

}
