package org.livemq.internal.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;

/**
 * <h1>MQTT 取消订阅确认报文</h1>
 * <p>服务端发送 UNSUBACK 报文给客户端用于确认收到 UNSUBSCRIBE 报文。</p>
 */
public class MqttUnsubAck extends MqttAck {

	public MqttUnsubAck(MqttUnsubscribe message){
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBACK);
		msgId = message.getMessageId();
	}
	
	public MqttUnsubAck(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBACK);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
		dis.close();
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
