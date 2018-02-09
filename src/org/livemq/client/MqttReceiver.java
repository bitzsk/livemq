package org.livemq.client;

import java.io.InputStream;

import org.livemq.internal.stream.MqttInputStream;
import org.livemq.internal.wire.MqttWireMessage;

/**
 * 客户端核心接收消息线程
 * @author w.x
 * @date 2018年2月8日 下午4:08:37
 */
public class MqttReceiver implements Runnable {

	private boolean running = false;
	private Object lifecycle = new Object();
	private MqttCore core;
	private MqttHandler handler;
	private MqttInputStream in;
	private Thread recThread;
	
	public MqttReceiver(MqttCore core, MqttHandler handler, InputStream is){
		this.core = core;
		this.handler = handler;
		this.in = new MqttInputStream(is);
	}
	
	public void start(){
		synchronized (lifecycle) {
			if(!running){
				running = true;
				recThread = new Thread(this, "MqttReceiver");
				recThread.start();
			}
		}
	}
	
	public void stop(){
		synchronized (lifecycle) {
			if(running){
				running = false;
				recThread = null;
			}
		}
	}
	
	public void run() {
		while(running && (in != null)){
			try {
				MqttWireMessage message = in.readMqttMessage();
				handler.handleReceivedMessage(message);
			} catch (Exception e) {
				core.shutdownConnection(e);
			}
		}
	}
	
	public boolean isRunning() {
		return running;
	}
}
