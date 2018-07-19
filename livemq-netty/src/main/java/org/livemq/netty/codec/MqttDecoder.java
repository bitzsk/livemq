package org.livemq.netty.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 
 * @Title MqttDecoder
 * @Package org.livemq.netty.codec
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-19 15:46
 * @version 1.0.0
 */
public class MqttDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		byte[] bytes = new byte[in.readableBytes()];
		in.readBytes(bytes);
		
		// log start
		System.out.println("MqttDecoder " + bytes.length);
		// log end
		
		out.add(bytes);
	}

}
