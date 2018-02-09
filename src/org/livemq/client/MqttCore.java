package org.livemq.client;

import java.io.IOException;

import org.livemq.Configuration;
import org.livemq.LiveMQ;
import org.livemq.MqttClientCallback;
import org.livemq.MqttClientPersistence;
import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;
import org.livemq.internal.net.Network;
import org.livemq.internal.wire.MqttConnect;
import org.livemq.internal.wire.MqttWireMessage;
import org.livemq.log.Logger;
import org.livemq.log.LoggerFactory;

/**
 * 客户端内部核心类
 * @author w.x
 * @date 2018年2月8日 下午2:43:34
 */
public class MqttCore {

	private static final Logger logger = LoggerFactory.getLogger(MqttCore.class);
	
	private LiveMQ mq;
	private Configuration config;
	private MqttHandler handler;
	private MqttReceiver receiver;
	private MqttSender sender;
	private MqttClientPersistence persistence;
	private MqttClientCallback callback;
	
	private Network network;
	
	/** 已连接*/
	final static byte CONNECTED	= 0;
	/** 连接中*/
	final static byte CONNECTING = 1;
	/** 断开中*/
	final static byte DISCONNECTING	= 2;
	/** 已断开*/
	final static byte DISCONNECTED = 3;
	/** 已关闭*/
	final static byte CLOSED = 4;
	
	byte conState = CLOSED;
	Object conLock = new Object();
	
	public MqttCore(LiveMQ mq, Configuration config, MqttClientPersistence persistence){
		this.mq = mq;
		this.config = config;
		this.persistence = persistence;
		this.handler = new MqttHandler(this, config, this.persistence);
	}
	
	public void setNetwork(Network network) {
		this.network = network;
	}

	public void setCallback(MqttClientCallback callback) {
		this.callback = callback;
		this.handler.setCallback(callback);
	}
	
	public void connect() {
		synchronized (conLock) {
			conState = CONNECTING;
			
			MqttConnect connect = new MqttConnect(mq.getClientId(), 
					config.isCleanSession(), 
					config.getKeepAliveInterval(), 
					config.getWillMessage(), 
					config.getWillTopic(), 
					config.getUsername(), 
					config.getPassword());
			ConnectThread thread = new ConnectThread(this, connect);
			thread.start("连接线程");
		}
	}
	
	public void shutdownConnection(Throwable e){
		synchronized (conLock) {
			conState = DISCONNECTING;
		}
		
		if(receiver != null){
			receiver.stop();
		}
		
		if(sender != null){
			sender.stop();
		}
		
		if(callback != null){
			callback.connectionLost(e);
		}
		
		synchronized (conLock) {
			try {
				close();
			} catch (Exception ex) {
				logger.warn("客户端关闭失败");
			}
		}
		
		synchronized (conLock) {
			conState = CLOSED;
		}
	}
	
	/**
	 * 关闭连接
	 * @author w.x
	 * @date 2018年2月8日 下午4:24:33
	 */
	private void close() throws MqttException {
		synchronized (conLock) {
			if(!isClosed()){
				if(!isDisconnected()){
					if (isConnecting()) {
						throw ExceptionHelper.createMqttException(MqttException.CODE_CLIENT_STATE_CONNECTING_EXCEPTION);
					} else if (isConnected()) {
						throw ExceptionHelper.createMqttException(MqttException.CODE_CLIENT_STATE_CONNECTED_EXCEPTION);
					} else if (isDisconnecting()) {
						throw ExceptionHelper.createMqttException(MqttException.CODE_CLIENT_STATE_DISCONNECTING_EXCEPTION);
					}
				}
				
				conState = DISCONNECTING;
				
				handler = null;
				receiver = null;
				sender = null;
				callback = null;
				config = null;
				persistence = null;
				network = null;
				
				conState = CLOSED;
			}
		}
	}
	
	/**
	 * 发送消息
	 * @author w.x
	 * @date 2018年2月8日 下午4:16:17
	 */
	public void send(MqttWireMessage message) {
		try {
			if(isConnected() || (!isConnected() && message instanceof MqttConnect)){
				handler.send(message);
			}else{
				Thread.sleep(5);
				send(message);
			}
		} catch (Exception e) {
			handler.undo(message);
		}
	}
	
	private class ConnectThread implements Runnable{

		MqttCore core;
		Thread thread;
		MqttConnect connect;
		
		public ConnectThread(MqttCore core, MqttConnect connect){
			this.core = core;
			this.connect = connect;
		}
		
		public void start(String threadName){
			thread = new Thread(this, threadName);
			thread.start();
		}
		
		public void run() {
			try {
				network.start();
				receiver = new MqttReceiver(core, handler, network.getInputStream());
				receiver.start();
				sender = new MqttSender(core, handler, network.getOutputStream());
				sender.start();
				
				send(connect);
			} catch (IOException e) {
				core.shutdownConnection(e);
			}
		}

	}
	
	
	/**
	 * 是否是已连接状态
	 * @return
	 */
	public boolean isConnected() {
		return conState == CONNECTED;
	}

	/**
	 * 是否是连接中状态
	 * @return
	 */
	public boolean isConnecting() {
		return conState == CONNECTING;
	}
	
	/**
	 * 是否是断开状态
	 * @return
	 */
	public boolean isDisconnected() {
		return conState == DISCONNECTED;
	}
	
	/**
	 * 是否是断开中状态
	 * @return
	 */
	public boolean isDisconnecting() {
		return conState == DISCONNECTING;
	}
	
	/**
	 * 是否是关闭状态
	 * @return
	 */
	public boolean isClosed() {
		return conState == CLOSED;
	}
	
	public void setConState(byte conState) {
		this.conState = conState;
	}

}
