package com.livemq.core.wire;

/**
 * <h1>控制报文 - CONNACK</h1>
 * 
 * <p>连接服务端确认报文</p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 13:37
 */
public class MqttConnAck extends MqttAck {

	public MqttConnAck() {
		super(MqttWireMessage.MESSAGE_TYPE_CONNACK);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] getVariableHeader() {
		// TODO Auto-generated method stub
		return null;
	}

}
