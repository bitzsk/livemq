package org.livemq.core.wire;

/**
 * <h1>控制报文 - DISCONNECT</h1>
 * 
 * <p>断开连接服务端报文</p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 14:13
 */
public class MqttDisconnect extends MqttWireMessage {

	public MqttDisconnect() {
		super(MqttWireMessage.MESSAGE_TYPE_DISCONNECT);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] getVariableHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte getMessageInfo() {
		// TODO Auto-generated method stub
		return 0;
	}

}
