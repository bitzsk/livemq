package org.livemq.test.netty.codec.test;

import org.livemq.common.exception.MqttException;
import org.livemq.core.wire.MqttDisconnect;
import org.livemq.core.wire.MqttPingResp;
import org.livemq.core.wire.MqttSubscribe;
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
		sendMessage(new MqttSubscribe("ying", 1));
		
		sendMessage(new MqttDisconnect());
		sendMessage(new MqttPingResp());
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
	private static void sendMessage(Object message) throws InterruptedException {
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
					}
				}
			});
			
		} else {
			System.out.println("消息发失败?,连接尚未建立!");
		}
	}
}
