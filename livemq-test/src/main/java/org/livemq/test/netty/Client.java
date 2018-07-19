package org.livemq.test.netty;

import org.livemq.core.message.MqttMessage;
import org.livemq.core.wire.MqttConnAck;
import org.livemq.core.wire.MqttConnect;
import org.livemq.core.wire.MqttDisconnect;
import org.livemq.core.wire.MqttPingReq;
import org.livemq.core.wire.MqttPingResp;
import org.livemq.core.wire.MqttPubAck;
import org.livemq.core.wire.MqttPubComp;
import org.livemq.core.wire.MqttPubRec;
import org.livemq.core.wire.MqttPubRel;
import org.livemq.core.wire.MqttPublish;
import org.livemq.core.wire.MqttSubAck;
import org.livemq.core.wire.MqttSubscribe;
import org.livemq.core.wire.MqttUnsubAck;
import org.livemq.core.wire.MqttUnsubscribe;
import org.livemq.core.wire.MqttWireMessage;
import org.livemq.netty.codec.MqttDecoder;
import org.livemq.netty.codec.MqttEncoder;
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

	public static void main(String[] args) {
		MqttWireMessage message = null;
		try {
//			// CONNECT
//			String clientId = "admin";
//			String username = "xinxisimple";
//			char[] password = "123456".toCharArray();
//			boolean clearSession = true;
//			int keepAlive = 120;
//			String willTopic = "ying";
//			MqttMessage willMessage = new MqttMessage();
//			willMessage.setPayload("This is will message payload.".getBytes());
//			willMessage.setRetained(true);
//			willMessage.setQos(2);
//			message = new MqttConnect(clientId, username, password, clearSession, keepAlive, willTopic, willMessage);
//			
//			// CONNACK
//			message = new MqttConnAck(1, MqttConnAck.FAIL_TOKEN);
//			
//			// PUBLISH
//			String topic = "ying";
//			MqttMessage msg = new MqttMessage("Hi LiveMQ ...".getBytes());
//			message = new MqttPublish(topic, msg);
//			
//			// PUBACK
//			message = new MqttPubAck(2);
//			
//			// PUBREC
//			message = new MqttPubRec(2);
//			
//			// PUBREL
//			message = new MqttPubRel(2);
//			
//			// PUBCOMP
//			message = new MqttPubComp(2);
//			
//			// SUBSCRIBE
//			String subTopic = "ying";
//			int qos = 1;
//			message = new MqttSubscribe(subTopic, qos);
//			
//			// SUBACK
//			message = new MqttSubAck(2, 1);
//			
//			// UNSUBSCRIBE
//			String unsubTopic = "lucy";
//			message = new MqttUnsubscribe(unsubTopic);
//			
//			// UNSUBACK
//			message = new MqttUnsubAck(2);
//			
//			// PINGREQ
//			message = new MqttPingReq();
//			
//			// PINGRESP
//			message = new MqttPingResp();
//			
//			// DISCONNECT
//			message = new MqttDisconnect();
//			
//			sendMessage(message);
			
//			// 测试一次发多条消息
//			sendMessage(new MqttPingReq());
//			sendMessage(new MqttDisconnect());
//			sendMessage(new MqttPingResp());
			
			// 测试编解码器的问题
			sendMessage("M");
			sendMessage("Q");
			sendMessage("T");
			sendMessage("T");
			
		} catch (Exception e) {
			e.printStackTrace();
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
//				pipeline.addLast(new MqttEncoder());
//				pipeline.addLast(new MqttDecoder());
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
