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
public final class MqttDecoder extends ByteToMessageDecoder {
	
	/**
	 * Encoder 传输字节流时，首先会传输该流的总长度
	 */
	private static final int PACKET_LENGTH = 4;
	
	// TODO: 用作测试日志，可删除所有打印
	private static int count = 0;
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if(in.readableBytes() > 0) {
			/**
			 * 如果缓冲区中读到的字节流长度小于报文的最小长度，则直接return
			 */
			if(in.readableBytes() < PACKET_LENGTH) {
				System.err.println("decoder 读取到的字节流小于数据的最小长度 ["+ PACKET_LENGTH +"]");
				return;
			}
			
			// 记录开始读取缓冲区的 index
			int beginIndex = in.readerIndex();
			// 读取到的报文消息总长度
			int len = in.readInt();
			
			System.err.println("decoder count:" + ++count);
			System.err.println("decoder in.readableBytes():" + in.readableBytes());
			System.err.println("decoder beginIndex:" + beginIndex + "\n");
			
			/**
			 * 拆包场景:
			 * 由于还未读取到完整的消息，in.readableBytes() 会小于 len，并重置 ByteBuf 的 readerIndex 为 0，
			 * 然后 return，ByteToMessageDecoder 会等待下个包的到来。
			 * 
			 * 由于第一次调用中 readerIndex 被重置为 0，那么 decoder 方法被调用第二次的时候，beginIndex 还是为 0
			 * 
			 * 
			 * 粘包场景:
			 * decode 方法会接收到一条聚合了多条业务的 <strong>大消息</strong>，
			 * 因此 ByteBuf.readableBytes() 肯定大于 len，ByteBuf 的 readerIndex 不会被重置。
			 * 只是 decode 方法每执行一次，beginIndex 将会递增，递增的值为 beginIndex + (PACKET_LENGTH + len)
			 */
			if(in.readableBytes() < len) {
				System.err.println("decoder 读取到的字节流长度为 ["+ in.readableBytes() +"], 而消息的总长度为 ["+ len +"], 等待继续读取，同时将 readerIndex 设置为 ["+ beginIndex +"]\n");
				in.readerIndex(beginIndex);
				return;
			}
			
			/**
			 * 拆包场景:
			 * 将缓冲区的 readerIndex 设置为最大。
			 * 首先代码执行到这里，针对拆包这种场景而言，已经是读取到一条有效完整的消息了。
			 * 这个时候需要通知 ByteToMessageDecoder 类，ByteBuf 中的数据已经读取完毕了，不要再调用 decode 方法了。
			 * ByteToMessageDecoder 类的底层会根据 ByteBuf.isReadable() 方法来判断是否读取完毕。
			 * 只有将 readerIndex 设置为最大，ByteBuf.isReadable() 方法才会返回 false。
			 * 
			 * 
			 * 粘包场景:
			 * 对于粘包来说，这行代码就不是表示将 readerIndex 升到最高，而是将 readerIndex 后移 (PACKET_LENGTH + len) 位，
			 * 让 beginIndex 递增 (PACKET_LENGTH + len)
			 */
			in.readerIndex(beginIndex + PACKET_LENGTH + len);
			
			/**
			 * 拆包场景:
			 * 当 decode 方法执行完后，回释放 ByteBuf 这个缓冲区，如果将执行完释放操作的 ByteBuf 传递给下一个处理器的话，
			 * 一旦下个处理器调用 ByteBuf 的读或者写的方法时，会立即报 IllegalReferenceCountException 异常的。
			 * 
			 * 因此 slice 操作后，必须加一个 retain 操作，让 ByteBuf 的引用计数器加 1，这样 ByteToMessageDecoder 会刀下留人，先不释放 ByteBuf
			 * 
			 * 
			 * 粘包场景:
			 * slice操作，目的是从 <strong>大消息</strong> 中截取一条完整有效的业务消息。
			 */
			ByteBuf sb = in.slice(beginIndex, PACKET_LENGTH + len);
			sb.retain();
			sb.readInt(); // 因为我们发送的报文包含了一个 int (也就是 4 个 byte) 的报文总长度，所以这里先 readInt 掉
			out.add(sb);
		}
	}

}
