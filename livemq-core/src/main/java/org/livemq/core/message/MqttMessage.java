package org.livemq.core.message;

import org.livemq.common.exception.ExceptionHelper;
import org.livemq.common.exception.MqttException;

/**
 * 
 * @Title MqttMessage
 * @Package org.livemq.common.message
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-18 14:18
 * @version 1.0.0
 */
public class MqttMessage {

	private byte[] payload;
	private int qos = 1;
	private boolean retained = false;
	private boolean duplicate = false;
	
	/**
	 * 校验 QoS
	 * @param qos
	 * @throws MqttException 
	 */
	public static void validateQos(int qos) throws MqttException {
		if(qos < 0 || qos > 2) throw ExceptionHelper.createMqttException("QoS [" + qos + "] is error");
	}
	
	/**
	 * 返回消息内容的 byte 数组
	 * @return
	 */
	public byte[] getPayload() {
		return payload;
	}
	
	/**
	 * 返回消息的 QoS
	 * @return
	 */
	public int getQos() {
		return qos;
	}
	
	/**
	 * 返回消息是否应该保留在服务器
	 * @return
	 */
	public boolean isRetained() {
		return retained;
	}
	
	/**
	 * 返回该消息是否为重发消息
	 * @return
	 */
	public boolean isDuplicate() {
		return duplicate;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	public void setQos(int qos) {
		this.qos = qos;
	}
	
	public void setRetained(boolean retained) {
		this.retained = retained;
	}
	
	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}
}
