package org.livemq;

import javax.net.SocketFactory;

import org.livemq.client.AsyncLiveMQ;
import org.livemq.client.MqttCore;
import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;
import org.livemq.internal.net.Network;
import org.livemq.internal.net.TCPNetwork;
import org.livemq.internal.wire.MqttDisconnect;
import org.livemq.internal.wire.MqttPublish;
import org.livemq.internal.wire.MqttSubscribe;
import org.livemq.internal.wire.MqttUnsubscribe;
import org.livemq.persistence.MqttClientFilePersistence;
import org.livemq.persistence.MqttClientMemoryPersistence;

/**
 * 客户端
 * @author w.x
 * @date 2018年2月8日 下午1:43:55
 */
public class LiveMQ implements AsyncLiveMQ {

	private Configuration config;
	private MqttClientPersistence persistence;
	private MqttCore core;
	
	public LiveMQ() throws MqttException{
		this(null, new MqttClientFilePersistence());
	}
	
	public LiveMQ(Configuration config) throws MqttException{
		this(config, new MqttClientFilePersistence());
	}
	
	public LiveMQ(Configuration config, MqttClientPersistence persistence) throws MqttException{
		if(config == null){
			throw ExceptionHelper.createMqttException(MqttException.CODE_CONFIGURATION_EXCEPTION);
		}
		
		this.config = config;
		this.persistence = persistence;
		if(this.persistence == null){
			this.persistence = new MqttClientMemoryPersistence();
		}
		this.persistence.open(getClientId(), getServerURI());
		core = new MqttCore(this, this.config, this.persistence);
	}

	public void connect() throws MqttException {
		core.setNetwork(createNetwork(config));
		core.connect();
	}

	public void disconnect() {
		MqttDisconnect message = new MqttDisconnect();
		core.send(message);
	}

	public void subscribe(String topic) throws MqttException {
		this.subscribe(new String[]{topic }, new int[]{1 });
	}

	public void subscribe(String topic, int qos) throws MqttException {
		this.subscribe(new String[]{topic }, new int[]{qos });
	}

	public void subscribe(String[] topics) throws MqttException {
		int[] qos = new int[topics.length];
		for(int i = 0;i < qos.length;i++){
			qos[i] = 1;
		}
		this.subscribe(topics, qos);
	}

	public void subscribe(String[] topics, int[] qoss) throws MqttException {
		MqttSubscribe message = new MqttSubscribe(topics, qoss);
		core.send(message);
	}

	public void unsubscribe(String topic) throws MqttException {
		this.unsubscribe(new String[]{topic});
	}

	public void unsubscribe(String[] topics) throws MqttException {
		MqttUnsubscribe message = new MqttUnsubscribe(topics);
		core.send(message);
	}

	public void publish(String topic, MqttMessage message) {
		MqttPublish msg = new MqttPublish(topic, message);
		core.send(msg);
	}

	public void publish(String[] topics, MqttMessage message) {
		MqttPublish msg = null;
		for(String topic : topics){
			msg = new MqttPublish(topic, message);
			core.send(msg);
		}
	}

	public String getClientId() {
		return config.getClientId();
	}

	public String getServerURI() {
		return config.getServerURI();
	}
	
	public void setCallback(MqttClientCallback callback){
		core.setCallback(callback);
	}
	
	private Network createNetwork(Configuration config) throws MqttException {
		Network network = null;
		String address = config.getServerURI();
		String host;
		int port;
		SocketFactory factory = config.getSocketFactory();
		
		int serverUriType = Configuration.validateURI(config.getServerURI());
		switch (serverUriType) {
		case Configuration.URI_TYPE_TCP:
			address = address.substring(6);
			host = getHost(address);
			port = getPort(address, Network.DEFAULT_PORT_TCP);
			
			if (factory == null) {
				factory = SocketFactory.getDefault();
			}
			network = new TCPNetwork(factory, host, port);
			((TCPNetwork) network).setConTimeout(config.getConnectionTimeout());
			break;
		case Configuration.URI_TYPE_SSL:
			break;
		}
		return network;
	}
	
	private int getPort(String uri, int defaultPort) {
		int port;
		int portIndex = uri.lastIndexOf(':');
		if (portIndex == -1) {
			port = defaultPort;
		} else {
			port = Integer.valueOf(uri.substring(portIndex + 1)).intValue();
		}
		return port;
	}

	private String getHost(String uri) {
		int schemeIndex = uri.lastIndexOf('/');
		int portIndex = uri.lastIndexOf(':');
		if (portIndex == -1) {
			portIndex = uri.length();
		}
		return uri.substring(schemeIndex + 1, portIndex);
	}
	
}
