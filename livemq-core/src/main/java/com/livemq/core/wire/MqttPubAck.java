package com.livemq.core.wire;

/**
 * <h1>控制报文 - PUBACK</h1>
 * 
 * <p>
 * 发布消息确认报文
 * </p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 13:46
 */
public class MqttPubAck extends MqttAck {

	public MqttPubAck() {
		super(MqttWireMessage.MESSAGE_TYPE_PUBACK);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] getVariableHeader() {
		// TODO Auto-generated method stub
		return null;
	}

}
