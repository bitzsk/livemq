package org.livemq.core.wire;

/**
 * <h1>控制报文确认报文顶级父类</h1>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 13:40
 */
public abstract class MqttAck extends MqttWireMessage {

	public MqttAck(byte type) {
		super(type);
	}

	@Override
	public byte getMessageInfo() {
		return 0;
	}

}
