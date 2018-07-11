package com.livemq.core.wire;

/**
 * <h1>控制报文 - UNSUBSCRIBE</h1>
 * 
 * <p>取消订阅主题报文</p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 14:08
 */
public class MqttUnsubscribe extends MqttWireMessage {

	public MqttUnsubscribe() {
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBSCRIBE);
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
