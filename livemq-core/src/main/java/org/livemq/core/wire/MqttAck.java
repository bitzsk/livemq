package org.livemq.core.wire;

/**
 * 
 * @Title MqttAck
 * @Package org.livemq.core.wire
 * @Description 控制报文确认报文顶级父类
 * @author xinxisimple@163.com
 * @date 2018-07-18 16:57
 * @version 1.0.0
 */
public abstract class MqttAck extends MqttWireMessage {

	public MqttAck(byte type) {
		super(type);
	}

	@Override
	public byte getMessageInfo() {
		return (byte) 0;
	}

}
