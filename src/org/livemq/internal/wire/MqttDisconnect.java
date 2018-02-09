package org.livemq.internal.wire;

/**
 * <h1>MQTT 断开连接报文</h1>
 * <p>
 * 
 * DISCONNECT 报文是客户端发给服务端的最后一个控制报文。表示客户端正常断开连接。
 * 
 * <h2>固定报头</h2>
 * 服务端必须验证所有的保留位都被设置为 0 ，日过它们不为0必须断开连接。
 * 
 * <h2>可变报头</h2>
 * PINGREQ 报文没有可变报头。
 * 
 * <h2>有效荷载</h2>
 * PINGREQ 报文没有有效荷载。
 * 
 * <h2>响应</h2>
 * 
 * 客户端发送 DISCONNECT 报文之后：
 * <ul>
 * 		<li>必须关闭网络连接。</li>
 * 		<li>不能通过那个网络连接再发送任何控制报文。</li>
 * </ul>
 * 
 * 服务端在收到 DISCONNECT 报文时：
 * <ul>
 * 		<li>必须丢弃任何与当前连接关联的未发布的遗嘱消息，具体描述见3.1.2.5节。</li>
 * 		<li>应该关闭网络连接，如果客户端 还没有这么做。</li>
 * </ul>
 * 
 * </p>
 */
public class MqttDisconnect extends MqttWireMessage {

	public MqttDisconnect() {
		super(MqttWireMessage.MESSAGE_TYPE_DISCONNECT);
	}

	public MqttDisconnect(byte info, byte[] variableHeader) {
		super(MqttWireMessage.MESSAGE_TYPE_DISCONNECT);
	}

	@Override
	public byte getMessageInfo() {
		return 0;
	}

	@Override
	public byte[] getVariableHeader() {
		return new byte[0];
	}

}
