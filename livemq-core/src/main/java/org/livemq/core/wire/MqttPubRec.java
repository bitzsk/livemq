package org.livemq.core.wire;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.livemq.common.exception.MqttException;

/**
 * 
 * @Title PUBREC - 发布收到 （QoS 2，第一步）
 * @Package org.livemq.core.wire
 * @Description PUBREC 报文是对 QoS 等级 2 的 PUBLISH 报文的响应。它是 QoS 2 等级协议交换的第二个报文。
 * @author xinxisimple@163.com
 * @date 2018-07-18 15:39
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0305-PUBREC.md
 */
public class MqttPubRec extends MqttAck {

	public MqttPubRec(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBREC);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
	}

	public MqttPubRec(MqttPublish publish) {
		this(publish.getMessageId());
	}
	
	public MqttPubRec(int messageId) {
		super(MqttWireMessage.MESSAGE_TYPE_PUBREC);
		msgId = messageId;
	}

	@Override
	public byte[] getVariableHeader() throws MqttException {
		return encodeMessageId();
	}

}
