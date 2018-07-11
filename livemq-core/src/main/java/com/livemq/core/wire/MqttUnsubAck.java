package com.livemq.core.wire;

/**
 * <h1>控制报文 - UNSUBACK</h1>
 * 
 * <p>取消订阅主题确认报文</p>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 14:09
 */
public class MqttUnsubAck extends MqttAck {

	public MqttUnsubAck() {
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBACK);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] getVariableHeader() {
		// TODO Auto-generated method stub
		return null;
	}

}
