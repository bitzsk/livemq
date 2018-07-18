package org.livemq.core.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.common.exception.ExceptionHelper;
import org.livemq.common.exception.MqttException;

/**
 * 
 * @Title UNSUBSCRIBE - 取消订阅
 * @Package org.livemq.core.wire
 * @Description 客户端发送 UNSUBSCRIBE 报文给服务端，用于取消订阅主题。
 * @author xinxisimple@163.com
 * @date 2018-07-18 16:35
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0310-UNSUBSCRIBE.md
 */
public class MqttUnsubscribe extends MqttWireMessage {

	/** 报文 订阅/取消订阅 的最大主题数量*/
	public static final int MAX_TOPIC_LENGTH = 10;
	
	private String[] topics;
	private int count;
	
	public MqttUnsubscribe(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBSCRIBE);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		
		msgId = dis.readUnsignedShort();
		
		count = 0;
		topics = new String[MAX_TOPIC_LENGTH];
		boolean end = false;
		while(!end) {
			try {
				topics[count ++] = decodeUTF8(dis);
			} catch (Exception e) {
				end = true;
			}
		}
	}

	public MqttUnsubscribe(String topic) throws MqttException {
		this(new String[] { topic});
	}

	public MqttUnsubscribe(String[] topics) throws MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBSCRIBE);
		
		if(topics == null || topics.length == 0) 
			throw ExceptionHelper.createMqttException("Topic is Null");
		if(topics.length > MAX_TOPIC_LENGTH) 
			throw ExceptionHelper.createMqttException("订阅主题数量超过最大限制 " + MAX_TOPIC_LENGTH);
		
		this.topics = topics;
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
			
			for (String topic : topics) {
				encodeUTF8(dos, topic);
			}
			
			dos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
	}

}
