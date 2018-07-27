package org.livemq.server.s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Title Server
 * @Package org.livemq.server.s
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-27 16:59
 * @version 1.0.0
 */
public abstract class Server {
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	protected Server next;
	
	/** First do something and then do start next*/
	protected abstract void start();
	
	/** First do stop next and then do something*/
	protected abstract void stop();
	
	public void startNext() {
		if(next != null) {
			logger.info("server [{}] startting...", getNextName());
			next.start();
		}
	}

	public void stopNext() {
		if(next != null) {
			next.stop();
			logger.info("server [{}] stopped.", getNextName());
		}
	}
	
	/**
	 * 新增一个服务
	 * @param server
	 */
	public void add(Server server) {
		if(next == null) {
			next = server;
		}else {
			next.add(server);
		}
	}
	
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	public String getNextName() {
		return next == null ? null : next.getName();
	}
	
}
