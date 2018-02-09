package org.livemq.internal.wire;

import java.io.IOException;

/**
 * <h1>MQTT 心跳检测报文</h1>
 * <p>
 * 
 * 客户端发送 PINGREQ 报文给服务端。用于：
 * 
 * 1.在没有任何其它控制报文从客户端发送给服务端时，告知服务端客户端还活着。
 * 2.请求服务端发送 响应确认它还活着。
 * 3.使用网络以确认网络连接没有断开。
 * 
 * 保持连接（Keep Alive）处理中用到这个报文，详情信息请查看 3.1.2.10 节。
 * 
 * <h2>可变报头</h2>
 * PINGREQ 报文没有可变报头。
 * 
 * <h2>有效荷载</h2>
 * PINGREQ 报文没有有效荷载。
 * 
 * <h2>响应</h2>
 * 服务端必须发送 PINGRESP 报文响应客户端的 PINGREQ 报文。
 * 
 * </p>
 */
public class MqttPingReq extends MqttWireMessage {

	public MqttPingReq(byte info, byte[] variableHeader) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_PINGREQ);
	}
	
	public MqttPingReq() {
		super(MqttWireMessage.MESSAGE_TYPE_PINGREQ);
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
