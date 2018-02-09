package org.livemq.internal.wire;

/**
 * <h1>MQTT 顶级抽象报文确认类</h1>
 * <p></p>
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
