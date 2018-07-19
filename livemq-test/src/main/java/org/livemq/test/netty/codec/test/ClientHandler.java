package org.livemq.test.netty.codec.test;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Netty client connected");
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端连接断开");
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		try {
			ctx.close();
			ctx.fireExceptionCaught(cause);
		} catch (Exception e) {}
	}
}
