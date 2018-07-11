package com.livemq.core.wire;

/**
 * <h1>控制报文 - CONNECT</h1>
 * 
 * <p>连接服务端报文</p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 13:34
 */
public class MqttConnect extends MqttWireMessage {
	
	public MqttConnect() {
		super(MqttWireMessage.MESSAGE_TYPE_CONNECT);
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
