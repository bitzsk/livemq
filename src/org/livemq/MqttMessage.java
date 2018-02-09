package org.livemq;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;

/**
 * 对外的消息实体类
 * 
 * @author w.x
 * @date 2018年2月8日 下午1:46:04
 */
public class MqttMessage {

	private byte[] payload;
	private int qos;
	/** 保留标志*/
	private boolean retained = false;
	/** 重发标志*/
	private boolean duplicate = false;

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	public boolean isRetained() {
		return retained;
	}

	public void setRetained(boolean retained) {
		this.retained = retained;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}
	
	public static void validateQos(int qos) throws MqttException {
		if(qos < 0 || qos > 2){
			throw ExceptionHelper.createMqttException(MqttException.CODE_MESSAGE_QOS_EXCEPTION);
		}
	}
	
}
