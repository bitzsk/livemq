package org.livemq.core.wire;

/**
 * 
 * @Title PINGREQ - 心跳请求
 * @Package org.livemq.core.wire
 * @Description 客户端发送 PINGREQ 报文给服务端。用于：
 * 		<ol>
 * 			<li>在没有任何其它控制报文从客户端发给服务端时，告知服务端客户端还活着。</li>
 * 			<li>请求服务端发送，响应确认它还活着。</li>
 * 			<li>使用网络以确认网络连接没有断开。</li>
 * 		</ol>
 * 		保持连接（Kepp Alive）处理中用到这个报文。
 * @author xinxisimple@163.com
 * @date 2018-07-18 16:50
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0312-PINGREQ.md
 */
public class MqttPingReq extends MqttWireMessage {

	public MqttPingReq(byte info, byte[] data) {
		super(MqttWireMessage.MESSAGE_TYPE_PINGREQ);
	}
	
	public MqttPingReq() {
		super(MqttWireMessage.MESSAGE_TYPE_PINGREQ);
	}

	@Override
	public byte[] getVariableHeader() {
		return new byte[0];
	}

	@Override
	public byte getMessageInfo() {
		return (byte) 0;
	}
	
	@Override
	public boolean isMessageIdRequired() {
		return false;
	}
	
}
