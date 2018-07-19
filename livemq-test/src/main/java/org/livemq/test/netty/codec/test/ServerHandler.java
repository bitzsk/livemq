package org.livemq.test.netty.codec.test;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		byte[] bytes = (byte[]) msg;
		String str = new String(bytes);
		System.out.println(str);
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
