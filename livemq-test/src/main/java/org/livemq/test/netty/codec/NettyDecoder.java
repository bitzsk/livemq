package org.livemq.test.netty.codec;

import java.util.Arrays;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class NettyDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if(in.readableBytes() > 0) {
			byte[] bytes = new byte[in.readableBytes()];
			in.readBytes(bytes);
			
			if(bytes.length > 0) {
				System.out.println(Arrays.toString(bytes));
				
			}
		}
	}

}
