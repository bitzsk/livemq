package org.livemq.core.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.common.exception.ExceptionHelper;
import org.livemq.common.exception.MqttException;
import org.livemq.core.message.MqttMessage;
import org.livemq.core.stream.CountingInputStream;

/**
 * 
 * @Title PUBLISH - 发布消息
 * @Package org.livemq.core.wire
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-18 14:17
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0303-PUBLISH.md
 */
public class MqttPublish extends MqttWireMessage {

	private String topic;
	private MqttMessage message;
	
	public MqttPublish(byte info, byte[] data) throws MqttException, IOException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBLISH);
		message = new MqttMessage();
		message.setRetained((info & 0x01) == 0x01);
		message.setQos((info >> 1) & 0x03);
		message.setDuplicate((info & 0x08) == 0x08);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		CountingInputStream counter = new CountingInputStream(bais);
		DataInputStream dis = new DataInputStream(counter);
		topic = decodeUTF8(dis);
		if(message.getQos() > 0) {
			msgId = dis.readUnsignedShort();
		}
		
		byte[] payload = new byte[data.length - counter.getCounter()];
		dis.readFully(payload, 0, payload.length);
		message.setPayload(payload);
	}

	public MqttPublish(String topic, MqttMessage message) {
		super(MqttWireMessage.MESSAGE_TYPE_PUBLISH);
		this.topic = topic;
		this.message = message;
	}

	@Override
	public byte[] getVariableHeader() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			
			// 1.主题名 
			encodeUTF8(dos, topic);
			
			// 2.报文标识符
			// 只有当 QoS 等级是 1 或 2 时，报文标识符字段才能出现在 PUBLISH 报文中
			if(message.getQos() > 0) {
				dos.writeShort(msgId);
			}
			
			dos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
	}

	@Override
	public byte getMessageInfo() {
		byte info = 0;
		
		// 0: 保留标志 RETAIN
		if(message.isRetained()) {
			info |= 0x01;
		}
		
		// 1 2: QoS 等级
		info |= (message.getQos() << 1);
		
		// 3: 重发标志 DUP
		if(message.isDuplicate()) {
			info |= 0x08;
		}
		
		return info;
	}
	
	@Override
	public byte[] getPayload() throws MqttException {
		return message.getPayload();
	}
	
	@Override
	public boolean isMessageIdRequired() {
		return message.getQos() > 0;
	}
	
}
