package org.livemq.internal.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;

/**
 * <h1>MQTT 发布消息确认报文</h1>
 * <p>
 * QoS 1 消息的预期响应<br>
 * PUBACK 报文没有有效荷载<br>
 * </p>
 */
public class MqttPubAck extends MqttAck {

	public MqttPubAck(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBACK);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
		dis.close();
	}
	
	public MqttPubAck(MqttPublish publish) {
		super(MqttWireMessage.MESSAGE_TYPE_PUBACK);
		msgId = publish.getMessageId();
	}

	/**
	 * PUBACK 报文的剩余长度等于2
	 * 
	 * 可变报头 包含等待确认的 PUBLISH 报文的报文标识符
	 * @throws MqttException 
	 */
	@Override
	public byte[] getVariableHeader() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeShort(msgId);
			dos.flush();
		}
		catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return baos.toByteArray();
	}

}
