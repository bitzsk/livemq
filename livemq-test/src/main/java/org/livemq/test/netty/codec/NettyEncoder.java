package org.livemq.test.netty.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoder extends MessageToByteEncoder<Message> {
	private static final Logger logger = LoggerFactory.getLogger(NettyEncoder.class);
	
	private static int count = 0;
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
		// 编码时将消息的总长度和有效荷载一起传输过去
		out.writeInt(msg.getLength());
		out.writeBytes(msg.getPayload());
		logger.info("encoder count:{}", count);
	}

}
