package org.livemq.core.wire;

/**
 * <h1>控制报文 - PINGRESP</h1>
 * 
 * <p>心跳检测确认报文</p>
 * 
 * 服务端发送 PINGRESP 报文响应客户端的 PINGREQ 报文。表示服务端还活着。 <br>
 * 
 * 保持连接（Keep Alive）处理中用到这个报文。 <br>
 * 
 * <h2>可变报头</h2>
 * PINGREQ 报文没有可变报头。 <br>
 * 
 * <h2>有效荷载</h2>
 * PINGREQ 报文没有有效荷载。 <br>
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 14:11
 */

/**
 * 
 * @Title PINGRESP - 心跳响应
 * @Package org.livemq.core.wire
 * @Description 服务端发送 PINGRESP 报文响应客户端的 PINGREQ 报文。表示服务端还活着。<br><br>
 * 		保持连接（Keep Alive）处理中用到这个报文。
 * @author xinxisimple@163.com
 * @date 2018-07-18 16:55
 * @version 1.0.0
 */
public class MqttPingResp extends MqttAck {

	public MqttPingResp(byte info, byte[] data) {
		super(MqttWireMessage.MESSAGE_TYPE_PINGRESP);
	}

	public MqttPingResp() {
		super(MqttWireMessage.MESSAGE_TYPE_PINGRESP);
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
