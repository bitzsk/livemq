package org.livemq.internal.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;

/**
 * <h1>PubRel 报文是对 PubRec 报文的响应。它是 QoS 2 等级协议交换的第三个报文</h1>
 * <p>PubRel 报文没有有效荷载</p>
 */
public class MqttPubRel extends MqttWireMessage {

	public MqttPubRel(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBREL);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
		dis.close();
	}
	
	public MqttPubRel(MqttPubRec message){
		super(MqttWireMessage.MESSAGE_TYPE_PUBREL);
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

	/**
	 * PubRel 控制报文固定报头的第 3，2，1，0 位是保留位，必须被设置为 0，0，1，0。
	 * 服务端必须将其它的任何值都当做是不合法的并关闭网络连接
	 */
	@Override
	public byte getMessageInfo() {
		return 2;
	}
	
}
