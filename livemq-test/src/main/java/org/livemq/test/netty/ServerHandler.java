package org.livemq.test.netty;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.livemq.core.wire.MqttWireMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		byte[] bytes = (byte[]) msg;
		
//		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//		DataInputStream dis = new DataInputStream(bais);
//		MqttWireMessage message = MqttWireMessage.createWireMessage(dis);
//		
//		System.out.println(message == null ? "Server Received Message is Null" : message.toString());
		
		String str = new String(bytes);
		System.out.println(str);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		ctx.fireExceptionCaught(cause);
	}
}
