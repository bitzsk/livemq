package com.livemq.core.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.livemq.common.exception.ExceptionHelper;
import com.livemq.common.exception.MqttException;

/**
 * <h1>控制报文 - SUBSCRIBE</h1>
 * 
 * <p>订阅主题报文</p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 14:06
 */
public class MqttSubscribe extends MqttWireMessage {

	/**
	 * 每次订阅的最大主题数量
	 */
	public static final int MAX_TOPIC_LENGTH = 10;
	
	private String[] topics;
	private int[] qos;
	
	/**
	 * 服务端收到该报文时通过 byte 数组构造方法
	 * @param info
	 * @param data
	 * @throws MqttException 
	 */
	public MqttSubscribe(byte info, byte[] data) throws MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_SUBSCRIBE);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		try {
			msgId = dis.readUnsignedShort();
			topics = new String[MAX_TOPIC_LENGTH];
			qos = new int[MAX_TOPIC_LENGTH];
			boolean end = false;
			int count = 0;
			while(!end) {
				try {
					topics[count] = decodeUTF8(dis);
					qos[count ++] = dis.readByte();
				} catch (Exception e) {
					end = true;
				}
			}
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
	}
	
	/**
	 * 客户端订阅主题构造方法
	 * @param topics
	 * @param qos
	 * @throws MqttException
	 */
	public MqttSubscribe(String[] topics, int[] qos) throws MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_SUBSCRIBE);
		
		if(topics == null || topics.length == 0) 
			throw ExceptionHelper.createMqttException("Topic is Null");
		if(qos == null || qos.length == 0) 
			throw ExceptionHelper.createMqttException("QoS is Null");
		if(topics.length != qos.length) 
			throw ExceptionHelper.createMqttException("Topics 和 QoS 长度不一致");
		if(topics.length > MAX_TOPIC_LENGTH || qos.length > MAX_TOPIC_LENGTH) 
			throw ExceptionHelper.createMqttException("订阅主题数量超过最大限制 " + MAX_TOPIC_LENGTH);
		
		for (int i = 0; i < qos.length; i++)
			validateQos(qos[i]);
		
		this.topics = topics;
		this.qos = qos;
	}

	// TODO
	/**
	 * 注: [3.8.2] <br>
	 * 可变报头包含客户端标识符(2 字节) <br>
	 */
	@Override
	public byte[] getVariableHeader() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeShort(msgId);
			dos.flush();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return baos.toByteArray();
	}

	/**
	 * 注: [3.8.1] <br>
	 * SUBSCRIBE 控制报文固定报头的第 3,2,1,0 位是保留位，<strong>必须</strong> 分别设置为 0,0,1,0。 <br>
	 * 服务端 <strong>必须</strong> 将其它的任何值都当做是不合法的并关闭网络。 <br>
	 */
	@Override
	public byte getMessageInfo() {
		return (byte) (2 | (duplicate ? 8 : 0));
	}
	
	/**
	 * SUBSCRIBE 报文的有效荷载包含了一个主题过滤器列表，它们表示客户端想要订阅的主题。 <br>
	 * 1.SUBSCRIBE 报文有效荷载中的主题过滤器必须是 1.5.3 节定义的 UTF-8 字符串。 <br>
	 * 2.服务端应该支持包含通配符的主题过滤器。 <br>
	 * 3.如果服务器选择不支持包含通配符的主题过滤器，必须拒绝任何包含统配符过滤器的订阅要求。 <br>
	 * 4.每一个过滤器后面跟着一个字节，这个字节被叫做 服务质量要求（Requested Qos）。它给出了服务端向客户端发送应用消息所允许的最大QoS 等级 <br>
	 * @throws MqttException 
	 */
	@Override
	public byte[] getPayload() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		for (int i = 0; i < topics.length; i++) {
			try {
				encodeUTF8(dos, topics[i]);
				dos.writeByte(qos[i]);
			} catch (IOException e) {
				throw ExceptionHelper.createMqttException(e);
			}
		}
		return baos.toByteArray();
	}
	
	public String[] getTopics() {
		return topics;
	}
	
	public void setTopics(String[] topics) {
		this.topics = topics;
	}

}
