package org.livemq.core.wire;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.livemq.common.exception.MqttException;

/**
 * 
 * @Title UNSUBACK - 取消订阅确认
 * @Package org.livemq.core.wire
 * @Description 服务端发送 UNSUBACK 报文给客户端用于确认收到 UNSUBSCRIBE 报文。
 * @author xinxisimple@163.com
 * @date 2018-07-18 16:45
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0311-UNSUBACK.md
 */
public class MqttUnsubAck extends MqttAck {

	public MqttUnsubAck(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBACK);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
	}
	
	public MqttUnsubAck(MqttUnsubscribe unsubscribe) {
		this(unsubscribe.getMessageId());
	}
	
	public MqttUnsubAck(int msgId) {
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBACK);
		this.msgId = msgId;
	}

	@Override
	public byte[] getVariableHeader() throws MqttException {
		return encodeMessageId();
	}

}
