package org.livemq.netty.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 解码器
 * @author xinxisimple@163.com
 * @date 2018-07-04 16:56
 */
public class MqttDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		byte[] bytes = new byte[in.readableBytes()];
		in.readBytes(bytes);
		
		// log start
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			buffer.append(bytes[i] + " ");
		}
		System.out.println("MqttDecoder ... " + "bytes len: " + bytes.length + ", content: " + buffer.toString());
		// log end
		
		out.add(bytes);
	}

}
