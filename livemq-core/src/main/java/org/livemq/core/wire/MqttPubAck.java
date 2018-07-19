package org.livemq.core.wire;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.livemq.common.exception.MqttException;

/**
 * 
 * @Title PUBACK - 发布确认 (QoS 1 确认报文)
 * @Package org.livemq.core.wire
 * @Description PUBACK 报文是对 QoS 1 等级的 PUBLISH 报文的响应。
 * @author xinxisimple@163.com
 * @date 2018-07-18 15:26
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0304-PUBACK.md
 */
public class MqttPubAck extends MqttAck {

	public MqttPubAck(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBACK);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
	}
	
	public MqttPubAck(MqttPublish publish) {
		this(publish.getMessageId());
	}
	
	public MqttPubAck(int messageId) {
		super(MqttWireMessage.MESSAGE_TYPE_PUBACK);
		msgId = messageId;
	}

	@Override
	public byte[] getVariableHeader() throws MqttException {
		return encodeMessageId();
	}

}
