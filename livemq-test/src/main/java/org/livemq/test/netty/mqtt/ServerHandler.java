package org.livemq.test.netty.mqtt;

import org.livemq.core.wire.MqttWireMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		
		MqttWireMessage message = MqttWireMessage.createWireMessage(bytes);
		System.out.println("Received Message: " + message + "\n");
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		ctx.fireExceptionCaught(cause);
	}
}
