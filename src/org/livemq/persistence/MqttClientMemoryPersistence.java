package org.livemq.persistence;

import java.util.Enumeration;
import java.util.Hashtable;

import org.livemq.MqttClientPersistence;

/**
 * 客户端默认的内存持久化实现
 * @author w.x
 * @date 2018年2月8日 下午2:31:53
 */
public class MqttClientMemoryPersistence implements MqttClientPersistence {

	private Hashtable<String, Object> data;

	public void open(String clientId, String serverURI) {
		data = new Hashtable<String, Object>();
	}

	public void close() {
		data.clear();
	}

	public void put(String key, Object object) {
		data.put(key, object);
	}

	public Object get(String key) {
		return data.get(key);
	}

	public void remove(String key) {
		data.remove(key);
	}

	public Enumeration<String> keys() {
		return data.keys();
	}

	public void clear() {
		data.clear();
	}

	public boolean containsKey(String key) {
		return data.containsKey(key);
	}
}
