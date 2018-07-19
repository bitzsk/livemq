package org.livemq.test.netty.codec;

import org.livemq.core.wire.MqttWireMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoder extends MessageToByteEncoder<MqttWireMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, MqttWireMessage msg, ByteBuf out) throws Exception {
		int len = msg.getHeader().length + msg.getPayload().length;
		
		System.out.println("NettyEncoder " + len);
		
		out.writeBytes(msg.getHeader());
		out.writeBytes(msg.getPayload());
	}

}
