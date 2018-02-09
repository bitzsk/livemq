package org.livemq;

import org.livemq.internal.wire.MqttWireMessage;

/**
 * 客户端消息回调接口
 * @author w.x
 * @date 2018年2月8日 下午1:45:09
 */
public interface MqttClientCallback {

	/**
	 * 连接成功时
	 */
	public void connected();
	
	/**
	 * 连接断开时
	 * @param throwable
	 */
	public void connectionLost(Throwable cause);
	
	/**
	 * 收到消息时
	 * @param topic
	 * @param message
	 */
	public void messageArrived(String topic, MqttWireMessage message);
	
	/**
	 * 消息发送完成时
	 */
	public void deliveryComplete(MqttWireMessage message);
}
