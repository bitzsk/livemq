package org.livemq.internal.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;

/**
 * <h1>MQTT 取消订阅报文</h1>
 * <p>
 * 客户端发送 UNSUBSCRIBE 报文给服务端，用于取消订阅主题<br>
 * 
 * <h2>响应</h2><br>
 * UNSUBSCRIBE 报文提供的主题过滤器（无论是否包含通配符）<strong> 必须 </strong>与服务端持有的这个客户端的当前主题过滤器集合逐个字符比较。<br>
 * 如果有任何过滤器完全匹配，那么它（服务端）自己的订阅将被删除，否则不能有进一步的处理。<br><br>
 * 
 * 如果福利端删除了一个订阅：<br>
 * <ul>
 * 		<li>它<strong> 必须 </strong>停止分发任何新消息给这个客户端。</li>
 * 		<li>它<strong> 必须 </strong>完成分发任何已经开始往客户端发送的 QoS 1 和 QoS 2 的消息。</li>
 * 		<li>它<strong> 可以 </strong>继续发送任何现存的准备分发给客户端的缓存消息。</li>
 * </ul>
 * 
 * 服务端<strong> 必须 </strong>发送 UNSUBSCRIBE 报文响应客户端的 UNSUBSCRIBE 请求。
 * UNSUBSCRIBE 报文必须包含和 UNSUBSCRIBE 报文相同的报文标识符。
 * 即使没有删除任何主题订阅，服务端页必须发送一个 SUBACK 响应。<br><br>
 * 
 * 如果服务端收到包含多个主题过滤器的 UNSUBSCRIBE 报文，
 * 它<strong> 必须 </strong>如同收到了一系列的多个 UNSUBSCRIBE 报文一样处理那个报文，
 * 除了将它们的响应合并到一个单独的 UNSUBACK 报文外。<br><br>
 * </p>
 */
public class MqttUnsubscribe extends MqttWireMessage {

	/**
	 * 要取消订阅的topic集
	 */
	private String topics[];
	private int count;
	
	public MqttUnsubscribe(String[] topics) throws MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBSCRIBE);
		if(topics == null || topics.length == 0){
			throw ExceptionHelper.createMqttException(MqttException.CODE_MESSAGE_TOPIC_IS_EMPTY_EXCEPTION);
		}
		this.topics = topics;
	}
	
	public MqttUnsubscribe(byte info, byte[] data) throws MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBSCRIBE);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		try {
			msgId = dis.readUnsignedShort();

			count = 0;
			topics = new String[10];
			boolean end = false;
			while (!end) {
				try {
					topics[count] = decodeUTF8(dis);
					count++;
				} catch (Exception e) {
					end = true;
				}
			}
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}finally{
			try {
				if(dis != null){
					dis.close();
				}
			} catch (Exception ex) {}
		}
		
	}
	
	/**
	 * UNSUBSCRIBE 控制报文固定报头的第 3，2，1，0 位是保留位，必须被设置为 0，0，1，0。
	 * 服务端必须将其它的任何值都当做是不合法的并关闭网络连接
	 */
	@Override
	public byte getMessageInfo() {
		return (byte) (2 | (duplicate ? 8 : 0));
	}
	
	/**
	 * UNSUBSCRIBE 报文的有效荷载包含客户端想要取消订阅的主题过滤器列表。
	 * UNSUBSCRIBE 报文中的主题过滤器必须是连续打包的、按照 1.5.3 节定义的 UTF-8 编码字符串
	 * UNSUBSCRIBE 报文的有效荷载必须至少包含一个消息过滤器。没有有效荷载的 UNSUBSCRIBE 报文是违反协议的
	 * @author Xinxi
	 * @date 2018年1月6日下午3:58:08
	 * @return
	 * @throws MqttException 
	 */
	@Override
	public byte[] getPayload() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		for (int i = 0;i < topics.length;i++){
			encodeUTF8(dos, topics[i]);
		}
		return baos.toByteArray();
	}

	@Override
	public byte[] getVariableHeader() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeShort(msgId);
			dos.flush();
		}catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return baos.toByteArray();
	}
	
	public String[] getTopics() {
		return topics;
	}

}
