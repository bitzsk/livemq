package org.livemq.test.netty.codec;

import java.util.Arrays;

import org.livemq.core.wire.MqttWireMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoder extends MessageToByteEncoder<MqttWireMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, MqttWireMessage msg, ByteBuf out) throws Exception {
		int len = msg.getHeader().length + msg.getPayload().length;
		
		System.out.println("NettyEncoder " + len);
		System.out.println(Arrays.toString(msg.getHeader()) + " - " + Arrays.toString(msg.getPayload()));
		
		out.writeBytes(msg.getHeader());
		out.writeBytes(msg.getPayload());
	}

}
