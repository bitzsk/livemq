package org.livemq.core.wire;

/**
 * <h1>控制报文 - PUBREC</h1>
 * 
 * <p>
 * 是对 PUBREC 报文的响应。它是 QoS 2 等级协议交换的第三个报文。
 * </p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 14:02
 */
public class MqttPubRel extends MqttWireMessage {

	public MqttPubRel() {
		super(MqttWireMessage.MESSAGE_TYPE_PUBREL);
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
