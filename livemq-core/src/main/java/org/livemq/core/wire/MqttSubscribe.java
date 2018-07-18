package org.livemq.core.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.common.exception.ExceptionHelper;
import org.livemq.common.exception.MqttException;
import org.livemq.core.message.MqttMessage;

/**
 * 
 * @Title SUBSCRIBE - 订阅主题
 * @Package org.livemq.core.wire
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-18 15:55
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0308-SUBSCRIBE.md
 */
public class MqttSubscribe extends MqttWireMessage {

	/** 报文 订阅/取消订阅 的最大主题数量*/
	public static final int MAX_TOPIC_LENGTH = 10;
	
	private String[] topics;
	private int[] qos;
	private int count;
	
	public MqttSubscribe(byte info, byte[] data) throws MqttException, IOException {
		super(MqttWireMessage.MESSAGE_TYPE_SUBSCRIBE);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		
		msgId = dis.readUnsignedShort();
		
		count = 0;
		topics = new String[MAX_TOPIC_LENGTH];
		qos = new int[MAX_TOPIC_LENGTH];
		boolean end = false;
		while(!end) {
			try {
				topics[count] = decodeUTF8(dis);
				qos[count ++] = dis.readByte();
			} catch (Exception e) {
				end = true;
			}
		}
	}
	
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
			MqttMessage.validateQos(qos[i]);
		
		this.topics = topics;
		this.qos = qos;
		this.count = topics.length;
	}

	@Override
	public byte[] getVariableHeader() throws MqttException {
		return encodeMessageId();
	}

	@Override
	public byte getMessageInfo() {
		return (byte) 2;
	}
	
	@Override
	public byte[] getPayload() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			
			for (int i = 0; i < topics.length; i++) {
				encodeUTF8(dos, topics[i]);
				dos.writeByte(qos[i]);
			}
			
			dos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
	}
	
}
