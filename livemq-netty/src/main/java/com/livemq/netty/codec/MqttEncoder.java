package com.livemq.netty.codec;

import com.livemq.core.wire.MqttWireMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 16:55
 */
public class MqttEncoder extends MessageToByteEncoder<MqttWireMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, MqttWireMessage msg, ByteBuf out) throws Exception {
		// log start
		byte[] bytes = new byte[msg.getHeader().length + msg.getPayload().length];
		System.arraycopy(msg.getHeader(), 0, bytes, 0, msg.getHeader().length);
		System.arraycopy(msg.getPayload(), 0, bytes, msg.getHeader().length, msg.getPayload().length);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			buffer.append(bytes[i] + " ");
		}
		System.out.println("MqttEncoder ... " + "header len: " + msg.getHeader().length + ", payload len:" + msg.getPayload().length + ", content: " + buffer.toString());
		// log end
		
		out.writeBytes(msg.getHeader());
		out.writeBytes(msg.getPayload());
	}

}
