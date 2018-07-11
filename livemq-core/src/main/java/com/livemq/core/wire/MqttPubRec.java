package com.livemq.core.wire;

/**
 * <h1>控制报文 - PUBREC</h1>
 * 
 * <p>
 * 发布消息报文 QoS 2 的预期响应
 * </p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 13:59
 */
public class MqttPubRec extends MqttAck {

	public MqttPubRec() {
		super(MqttWireMessage.MESSAGE_TYPE_PUBREC);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] getVariableHeader() {
		// TODO Auto-generated method stub
		return null;
	}

}
