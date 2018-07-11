package com.livemq.core.wire;

/**
 * <h1>控制报文 - SUBACK</h1>
 * 
 * <p>订阅主题确认报文</p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 14:07
 */
public class MqttSubAck extends MqttAck {

	public MqttSubAck() {
		super(MqttWireMessage.MESSAGE_TYPE_SUBACK);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] getVariableHeader() {
		// TODO Auto-generated method stub
		return null;
	}

}
