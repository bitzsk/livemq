package org.livemq;

import java.util.Enumeration;

/**
 * 客户端消息持久化接口
 * @author w.x
 * @date 2018年2月8日 下午1:46:23
 */
public interface MqttClientPersistence {

	public void open(String clientId, String serverURI);
	
	public void close();
	
	public void put(String key, Object obj);
	
	public Object get(String key);
	
	public void remove(String key);
	
	public Enumeration<String> keys();
	
	public void clear();
	
	public boolean containsKey(String key);
}
