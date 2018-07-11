package com.livemq.core.wire;

/**
 * <h1>控制报文 - PUBLISH</h1>
 * 
 * <p>发布消息报文</p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 13:45
 */
public class MqttPublish extends MqttWireMessage {

	public MqttPublish() {
		super(MqttWireMessage.MESSAGE_TYPE_PUBLISH);
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
