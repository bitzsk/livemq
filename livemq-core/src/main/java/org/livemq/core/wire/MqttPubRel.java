package org.livemq.core.wire;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.livemq.common.exception.MqttException;

/**
 * 
 * @Title PUBREL - 发布释放（QoS 2，第二步）
 * @Package org.livemq.core.wire
 * @Description PUBREL 报文是对 PUBREC 报文的响应。它是 QoS 2 等级协议交换的第三个报文。
 * @author xinxisimple@163.com
 * @date 2018-07-18 15:45
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0306-PUBREL.md
 */
public class MqttPubRel extends MqttWireMessage {

	public MqttPubRel(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBREL);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
	}

	public MqttPubRel(MqttPubRec pubRec) {
		this(pubRec.getMessageId());
	}
	
	public MqttPubRel(int messageId) {
		super(MqttWireMessage.MESSAGE_TYPE_PUBREL);
		msgId = messageId;
	}

	@Override
	public byte[] getVariableHeader() throws MqttException {
		return encodeMessageId();
	}

	@Override
	public byte getMessageInfo() {
		return (byte) 2;
	}

}
