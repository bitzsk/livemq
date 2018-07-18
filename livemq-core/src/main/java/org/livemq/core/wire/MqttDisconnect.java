package org.livemq.core.wire;

/**
 * DISCONNECT - 断开连接
 * @Title MqttDisconnect
 * @Package org.livemq.core.wire
 * @Description DISCONNECT 报文是客户端发给服务端的最后一个控制报文。表示客户端正常断开连接。
 * @author xinxisimple@163.com
 * @date 2018-07-18 17:01
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0314-DISCONNECT.md
 */
public class MqttDisconnect extends MqttWireMessage {

	public MqttDisconnect(byte info, byte[] data) {
		super(MqttWireMessage.MESSAGE_TYPE_DISCONNECT);
	}
	
	public MqttDisconnect() {
		super(MqttWireMessage.MESSAGE_TYPE_DISCONNECT);
	}

	@Override
	public byte[] getVariableHeader() {
		return new byte[0];
	}

	@Override
	public byte getMessageInfo() {
		return (byte) 0;
	}
	
	@Override
	public boolean isMessageIdRequired() {
		return false;
	}

}
