package com.livemq.core.wire;

/**
 * <h1>控制报文 - PUBREC</h1>
 * 
 * <p>
 * 是对 PUBREL 报文的响应。它是 QoS 2 等级协议交换的第四个也就是最后一个报文。
 * </p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 14:04
 */
public class MqttPubComp extends MqttAck {

	public MqttPubComp() {
		super(MqttWireMessage.MESSAGE_TYPE_PUBCOMP);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] getVariableHeader() {
		// TODO Auto-generated method stub
		return null;
	}

}
