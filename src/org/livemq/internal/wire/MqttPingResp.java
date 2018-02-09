package org.livemq.internal.wire;

/**
 * <h1>MQTT 心跳检测确认报文</h1>
 * <p>
 * 
 * 服务端发送 PINGRESP 报文响应客户端的 PINGREQ 报文。表示服务端还活着。
 * 
 * 保持连接（Keep Alive）处理中用到这个报文。
 * 
 * <h2>可变报头</h2>
 * PINGREQ 报文没有可变报头。
 * 
 * <h2>有效荷载</h2>
 * PINGREQ 报文没有有效荷载。
 * 
 * </p>
 */
public class MqttPingResp extends MqttAck {

	public MqttPingResp(byte info, byte[] variableHeader) {
		super(MqttWireMessage.MESSAGE_TYPE_PINGRESP);
	}
	
	public MqttPingResp() {
		super(MqttWireMessage.MESSAGE_TYPE_PINGRESP);
	}

	@Override
	public byte getMessageInfo() {
		return 0;
	}

	@Override
	public byte[] getVariableHeader() {
		return new byte[0];
	}
	
	@Override
	public boolean isMessageIdRequired() {
		return false;
	}

}
