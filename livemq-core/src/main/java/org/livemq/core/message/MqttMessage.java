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

	/** 默认消息 QoS*/
	private static final int QOS_DEFAULT = 1;
	/** 默认不保留消息在服务器*/
	private static final boolean RETAINED_DEFAULT = false;
	/** 默认不是重发消息*/
	private static final boolean DUPLICATE_DEFAULT = false;
	
	private byte[] payload;
	private int qos = QOS_DEFAULT;
	private boolean retained = RETAINED_DEFAULT;
	private boolean duplicate = DUPLICATE_DEFAULT;
	
	public MqttMessage() {
		this(new byte[0]);
	}
	
	public MqttMessage(byte[] payload) {
		this(QOS_DEFAULT, payload);
	}

	public MqttMessage(int qos, byte[] payload) {
		this(qos, payload, RETAINED_DEFAULT);
	}
	
	public MqttMessage(int qos, byte[] payload, boolean retained) {
		this(qos, payload, retained, DUPLICATE_DEFAULT);
	}
	
	public MqttMessage(int qos, byte[] payload, boolean retained, boolean duplicate) {
		this.qos = qos;
		this.payload = payload;
		this.retained = retained;
		this.duplicate = duplicate;
	}
	
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

	/**
	 * 设置消息有效荷载
	 * @param payload
	 */
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	/**
	 * 设置消息的 qos
	 * @param qos
	 */
	public void setQos(int qos) {
		this.qos = qos;
	}
	
	/**
	 * 设置消息是否应该保留在服务器
	 * @param retained
	 */
	public void setRetained(boolean retained) {
		this.retained = retained;
	}
	
	/**
	 * 设置该消息是否为重发消息
	 * @param duplicate
	 */
	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	@Override
	public String toString() {
		return "MqttMessage [payload=" + new String(payload) + ", qos=" + qos + ", retained=" + retained
				+ ", duplicate=" + duplicate + "]";
	}
}
