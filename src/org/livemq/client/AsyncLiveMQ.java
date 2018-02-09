package org.livemq.client;

import org.livemq.MqttMessage;
import org.livemq.exception.MqttException;

/**
 * 客户端基础接口
 * @author w.x
 * @date 2018年2月8日 下午1:51:22
 */
public interface AsyncLiveMQ {

	/**
	 * <h1>连接</h1>
	 * 发送  <strong>MqttConnect</strong> 报文<br>
	 * @author w.x
	 * @date 2018年2月8日 下午2:19:39
	 */
	public void connect() throws MqttException;
	
	/**
	 * <h1>断开连接</h1>
	 * 发送  <strong>MqttDisconnect</strong> 报文<br>
	 * @author w.x
	 * @date 2018年2月8日 下午2:19:39
	 */
	public void disconnect();
	
	/**
	 * <h1>订阅</h1>
	 * 发送  <strong>MqttSubscribe</strong> 报文<br>
	 * @author w.x
	 * @date 2018年2月8日 下午2:19:39
	 */
	public void subscribe(String topic) throws MqttException;

	/**
	 * <h1>订阅</h1>
	 * 发送  <strong>MqttSubscribe</strong> 报文<br>
	 * @author w.x
	 * @date 2018年2月8日 下午2:19:39
	 */
	public void subscribe(String topic, int qos) throws MqttException;

	/**
	 * <h1>订阅</h1>
	 * 发送  <strong>MqttSubscribe</strong> 报文<br>
	 * @author w.x
	 * @date 2018年2月8日 下午2:19:39
	 */
	public void subscribe(String[] topics) throws MqttException;

	/**
	 * <h1>订阅</h1>
	 * 发送  <strong>MqttSubscribe</strong> 报文<br>
	 * @author w.x
	 * @throws MqttException 
	 * @date 2018年2月8日 下午2:19:39
	 */
	public void subscribe(String[] topics, int[] qoss) throws MqttException;
	
	/**
	 * <h1>取消订阅</h1>
	 * 发送  <strong>MqttUnsubscribe</strong> 报文<br>
	 * @author w.x
	 * @date 2018年2月8日 下午2:19:39
	 */
	public void unsubscribe(String topic) throws MqttException;

	/**
	 * <h1>取消订阅</h1>
	 * 发送  <strong>MqttUnsubscribe</strong> 报文<br>
	 * @author w.x
	 * @date 2018年2月8日 下午2:19:39
	 */
	public void unsubscribe(String[] topics) throws MqttException;
	
	/**
	 * <h1>消息发布</h1>
	 * 发送  <strong>MqttPublish</strong> 报文<br>
	 * @author w.x
	 * @date 2018年2月8日 下午2:19:39
	 */
	public void publish(String topic, MqttMessage message);

	/**
	 * <h1>消息发布</h1>
	 * 发送  <strong>MqttPublish</strong> 报文<br>
	 * @author w.x
	 * @date 2018年2月8日 下午2:19:39
	 */
	public void publish(String[] topics, MqttMessage message);
	
	/**
	 * 获取客户端标识符
	 * @author w.x
	 * @date 2018年2月8日 下午2:23:40
	 */
	public String getClientId();
	
	/**
	 * 获取服务端里连接地址
	 * @author w.x
	 * @date 2018年2月8日 下午2:36:56
	 */
	public String getServerURI();
}
