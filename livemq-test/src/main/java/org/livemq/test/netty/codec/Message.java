package org.livemq.test.netty.codec;

/**
 * 
 * @Title 自定义协议消息
 * @Package org.livemq.test.netty.wire
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-21 14:43
 * @version 1.0.0
 */
public class Message {

	// 报文的总长度
	// 每个报文最前面总会有 4 byte 的消息长度
	private int length;
	
	// 有效荷载
	private byte[] payload;

	public Message(byte[] payload) {
		this.payload = payload;
		this.length = payload.length;
	}

	@Override
	public String toString() {
		return new String(payload);
	}
	
	/**
	 * 返回消息的总长度
	 * @return
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * 返回消息的有效荷载
	 * @return
	 */
	public byte[] getPayload() {
		return payload;
	}
}
