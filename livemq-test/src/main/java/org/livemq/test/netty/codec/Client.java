package org.livemq.test.netty.codec;

import org.livemq.common.exception.MqttException;
import org.livemq.test.netty.codec.NettyDecoder;
import org.livemq.test.netty.codec.NettyEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

	public static void main(String[] args) throws InterruptedException, MqttException {
		
		/**
		 * 1.测试普通消息
		 */
//		Message m1 = new Message("netty".getBytes());
//		sendMessage(m1);
		
		/**
		 * 2.测试拆包
		 * 
		 * 说明: 将该消息的有效荷载增大至 tcp 传输时需要 2 个以上的请求才可以处理完毕时
		 */
//		String payload = "";
//		for (int i = 0; i < 200; i++) {
//			payload += ("payload" + i);
//		}
//		System.out.println(payload);
//		Message m2 = new Message(payload.getBytes());
//		sendMessage(m2);
		
		/**
		 * 3.测试粘包
		 * 
		 * 说明: 发送多个报文到服务端
		 */
		Message m3 = null;
		for (int i = 0; i < 10; i++) {
			m3 = new Message(("hello"+i).getBytes());
			sendMessage(m3);
		}
		
	}
	
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8088;

	public static Bootstrap bootstrap = initBootstrap();
	public static Channel channel = getChannel(HOST, PORT);

	private static Bootstrap initBootstrap() {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new NettyEncoder());
				pipeline.addLast(new NettyDecoder());
				pipeline.addLast(new ClientHandler());
			}
		});
		return bootstrap;
	}

	/**
	 * 发起异步连接并返回 Channel
	 * 
	 * @param host
	 * @param port
	 * @return
	 */
	private static Channel getChannel(String host, int port) {
		Channel channel = null;
		try {
			channel = bootstrap.connect(host, port).sync().channel();
		} catch (Exception e) {
			System.out.println(String.format("连接Server(IP[%s],PORT[%s])失败", host, port));
			return null;
		}
		return channel;
	}
	
	/**
	 * 发送消息
	 * @param message
	 * @return
	 * @throws InterruptedException
	 */
	private static void sendMessage(Message message) throws InterruptedException {
		if (channel != null) {
			ChannelFuture future = channel.writeAndFlush(message).sync();
			future.addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isDone()) {
						if(future.isSuccess()) {
							System.out.println("消息发送成功: " + future.isSuccess());
						}else if(future.isCancelled()) {
							System.out.println("消息取消发送.");
						}else if(future.cause() !=null) {
							System.out.println("消息发送失败~~");
						}
						System.out.println();
					}
				}
			});
			
		} else {
			System.out.println("消息发失败?,连接尚未建立!");
		}
	}
}
