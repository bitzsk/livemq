package org.livemq.test.netty.codec.test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.livemq.core.wire.MqttWireMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		byte[] bytes = (byte[]) msg;
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);
		MqttWireMessage message = MqttWireMessage.createWireMessage(dis);
		System.out.println(message == null ? "MESSAGE IS NULL" : message);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		try {
			System.out.println("有客户端断开连接.");
			ctx.close();
			ctx.fireExceptionCaught(cause);
		} catch (Exception e) {}
	}
}
