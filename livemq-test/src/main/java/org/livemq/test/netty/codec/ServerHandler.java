package org.livemq.test.netty.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	private static int count = 0;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		Message message = new Message(bytes);
		logger.info("Received Msg Count:{}", ++count);
		logger.info("Received Msg:{}", message);
		System.out.println();
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
