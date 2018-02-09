package org.livemq.internal.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;

/**
 * <h1>PubComp 报文是对 PubRel 报文的响应。它是 QoS 2 等级协议交换的第四个也是最后一个报文</h1>
 * <p>PubComp 报文没有有效荷载</p>
 */
public class MqttPubComp extends MqttAck {

	public MqttPubComp(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBCOMP);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
		dis.close();
	}
	
	public MqttPubComp(MqttPubRel message) {
		super(MqttWireMessage.MESSAGE_TYPE_PUBCOMP);
		msgId = message.getMessageId();
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

}
