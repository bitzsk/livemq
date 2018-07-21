package org.livemq.test.netty.mqtt;

import org.livemq.common.exception.MqttException;
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
		// 测试 tcp 拆包/粘包 现象
		// 1.拆包 (success)
		// 说明:发送一条有效荷载非常大的消息
		String str = "";
		for (int i = 0; i < 200; i++) {
			str += ("mqtt超大消息" + i);
		}
		System.out.println(str);
		MqttMessage m1 = new MqttMessage(str.getBytes());
		MqttPublish publish = new MqttPublish("ying", m1);
		sendMessage(publish);
		
		// 2.粘包 (success)
		// 说明:发送多条较小的消息，让 tcp 将多条较小的消息汇聚成一个大消息一次发送
		sendMessage(new MqttPingReq());
		sendMessage(new MqttPingResp());
		sendMessage(new MqttSubAck(1, 1));
		
		// 3.拆包 / 粘包 一起测试 (success)
		// 说明:将上面的测试 1 和 测试 2 全部打开发送
		
		
//		// 以下为测试发送 MQTT 消息
//		MqttWireMessage message = null;
//		// CONNECT (success)
//		String clientId = "admin";
//		String username = "xinxisimple";
//		char[] password = "123456".toCharArray();
//		boolean clearSession = true;
//		int keepAlive = 120;
//		String willTopic = "ying";
//		MqttMessage willMessage = new MqttMessage();
//		willMessage.setPayload("This is will message payload.".getBytes());
//		willMessage.setRetained(true);
//		willMessage.setQos(2);
//		message = new MqttConnect(clientId, username, password, clearSession, keepAlive, willTopic, willMessage);
//		
//		// CONNACK (success)
//		message = new MqttConnAck(1, MqttConnAck.FAIL_TOKEN);
//		
//		// PUBLISH (success)
//		String topic = "ying";
//		MqttMessage msg = new MqttMessage("Hi LiveMQ ...".getBytes());
//		message = new MqttPublish(topic, msg);
//		
//		// PUBACK (success)
//		message = new MqttPubAck(2);
//		
//		// PUBREC (success)
//		message = new MqttPubRec(2);
//		
//		// PUBREL (success)
//		message = new MqttPubRel(2);
//		
//		// PUBCOMP (success)
//		message = new MqttPubComp(2);
//		
//		// SUBSCRIBE (success)
//		String subTopic = "ying";
//		int qos = 1;
//		message = new MqttSubscribe(subTopic, qos);
//		
//		// SUBACK (success)
//		message = new MqttSubAck(2, 1);
//		
//		// UNSUBSCRIBE (success)
//		String unsubTopic = "lucy";
//		message = new MqttUnsubscribe(unsubTopic);
//		
//		// UNSUBACK (success)
//		message = new MqttUnsubAck(2);
//		
//		// PINGREQ (success)
//		message = new MqttPingReq();
//		
//		// PINGRESP (success)
//		message = new MqttPingResp();
//		
//		// DISCONNECT (success)
//		message = new MqttDisconnect();
//		
//		sendMessage(message);
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
				pipeline.addLast(new MqttEncoder());
				pipeline.addLast(new MqttDecoder());
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
						System.out.println();
					}
				}
			});
			
		} else {
			System.out.println("消息发失败?,连接尚未建立!");
		}
	}

}
