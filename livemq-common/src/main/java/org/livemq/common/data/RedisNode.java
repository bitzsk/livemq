package org.livemq.common.data;

/**
 * 
 * @Title RedisNode
 * @Package org.livemq.common.data
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-21 17:39
 * @version 1.0.0
 */
public class RedisNode {
	
	private String host;
	private int port;

	public RedisNode() {
	}
	
	public RedisNode(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return host + ":" + port;
	}

}
