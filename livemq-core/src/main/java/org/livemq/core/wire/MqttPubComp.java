package org.livemq.core.wire;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.livemq.common.exception.MqttException;

/**
 * 
 * @Title PUBCOMP - 发布完成（QoS 2，第三步）
 * @Package org.livemq.core.wire
 * @Description PUBCOMP 报文是对 PUBREL 报文的响应。它是 QoS 2 等级协议交换的第四个也是最后一个报文。
 * @author xinxisimple@163.com
 * @date 2018-07-18 15:52
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0307-PUBCOMP.md
 */
public class MqttPubComp extends MqttAck {

	public MqttPubComp(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBCOMP);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
	}

	public MqttPubComp(MqttPubRel pubRel) {
		this(pubRel.getMessageId());
	}
	
	public MqttPubComp(int messageId) {
		super(MqttWireMessage.MESSAGE_TYPE_PUBCOMP);
		msgId = messageId;
	}

	@Override
	public byte[] getVariableHeader() throws MqttException {
		return encodeMessageId();
	}

}
