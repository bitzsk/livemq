package org.livemq.netty.codec;

import org.livemq.core.wire.MqttWireMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * @Title MqttEncoder
 * @Package org.livemq.netty.codec
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-19 15:46
 * @version 1.0.0
 */
public class MqttEncoder extends MessageToByteEncoder<MqttWireMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, MqttWireMessage msg, ByteBuf out) throws Exception {
		// log start
		int len = msg.getHeader().length + msg.getPayload().length;
		System.out.println("MqttEncoder " + len);
		// log end
		
		out.writeBytes(msg.getHeader());
		out.writeBytes(msg.getPayload());
	}

}
