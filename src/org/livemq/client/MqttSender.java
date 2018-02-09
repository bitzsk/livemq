package org.livemq.client;

import java.io.OutputStream;

import org.livemq.internal.stream.MqttOutputStream;
import org.livemq.internal.wire.MqttWireMessage;

/**
 * 客户端核心发送报文线程
 * @author w.x
 * @date 2018年2月8日 下午4:11:33
 */
public class MqttSender implements Runnable {
	
	private boolean running = false;
	private Object lifecycle = new Object();
	private MqttCore core;
	private MqttHandler handler;
	private MqttOutputStream out;
	private Thread recThread;
	
	public MqttSender(MqttCore core, MqttHandler handler, OutputStream os){
		this.core = core;
		this.handler = handler;
		this.out = new MqttOutputStream(os);
	}
	
	public void start(){
		synchronized (lifecycle) {
			if(!running){
				running = true;
				recThread = new Thread(this, "MqttSender");
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
		while(running && (out != null)){
			try {
				MqttWireMessage message = handler.get();
				if(message != null){
					out.write(message);
					out.flush();
					handler.handleSentMessage(message);
				}
			} catch (Exception e) {
				core.shutdownConnection(e);
			}
		}
	}
	
	public boolean isRunning() {
		return running;
	}

}
