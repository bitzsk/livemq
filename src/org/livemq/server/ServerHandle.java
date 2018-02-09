package org.livemq.server;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.livemq.Configuration;
import org.livemq.MqttMessage;
import org.livemq.exception.MqttException;
import org.livemq.internal.nio.NIOHandle;
import org.livemq.internal.wire.MqttAck;
import org.livemq.internal.wire.MqttConnack;
import org.livemq.internal.wire.MqttConnect;
import org.livemq.internal.wire.MqttDisconnect;
import org.livemq.internal.wire.MqttPingReq;
import org.livemq.internal.wire.MqttPingResp;
import org.livemq.internal.wire.MqttPubAck;
import org.livemq.internal.wire.MqttPubComp;
import org.livemq.internal.wire.MqttPubRec;
import org.livemq.internal.wire.MqttPubRel;
import org.livemq.internal.wire.MqttPublish;
import org.livemq.internal.wire.MqttSuback;
import org.livemq.internal.wire.MqttSubscribe;
import org.livemq.internal.wire.MqttUnsubAck;
import org.livemq.internal.wire.MqttUnsubscribe;
import org.livemq.internal.wire.MqttWireMessage;
import org.livemq.log.Logger;
import org.livemq.log.LoggerFactory;
import org.livemq.server.pers.DataPersistence;
import org.livemq.util.ByteUtil;
import org.livemq.util.FileUtil;
import org.livemq.util.Random;
import org.livemq.util.StringUtil;

public class ServerHandle implements NIOHandle {

	private static final Logger logger = LoggerFactory.getLogger(ServerHandle.class);
	
	//默认1024字节的缓冲区
	private ByteBuffer buffer = ByteBuffer.allocate(1024);

	private DataPersistence persistence;
	
	public ServerHandle(DataPersistence persistence){
		this.persistence = persistence;
	}
	
	public void accept(SelectionKey key) throws IOException {
		SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
		socketChannel.configureBlocking(false);
		//该时间为客户端通道连接时间
		long start = new Date().getTime();
		socketChannel.register(key.selector(), SelectionKey.OP_READ, start);
	}

	public void connect(SelectionKey key) {
		// TODO Auto-generated method stub
		
	}

	public void read(SelectionKey key) {
		SocketChannel channel = (SocketChannel) key.channel();
		Object att = key.attachment();
		try {
			// 清空整个缓冲区
			buffer.clear();
			// 从通道读取数据到 Buffer
			int len = channel.read(buffer);
			if(len == -1){
				key.cancel();
				return;
			}
			if(len > 0){
				//切换 Buffer 的模式：从写模式切换到读模式
				buffer.flip();
				
				// 读取 Buffer 中的数据构建报文
				byte[] packet = new byte[len];
				buffer.get(packet, 0, len);
				
				/**
				 * 缓冲区默认设置了100个字节大小，但是如果传输的是文件并且文件大小超过了100字节。
				 * 解析剩余长度，重新初始化缓冲区的大小为读取大的数据的总长度，然后重新读取。
				 * TODO 但是又遇到问题：读取一部分后通道被关闭。待解决
				 */
//				ByteArrayInputStream bais = new ByteArrayInputStream(packet);
//				CountingInputStream counter = new CountingInputStream(bais);
//				DataInputStream in = new DataInputStream(counter);
//				int first = in.readUnsignedByte();
//				byte type = (byte) (first >> 4);
//				byte info = (byte) (first &= 0x0f);
//				//从流中解析的剩余长度
//				long remLen = MqttWireMessage.decodeMBI(in);
//				//总长度
//				long totalToRead = counter.getCounter() + remLen;
//				
//				System.out.println(MqttWireMessage.getTypeName(type) + "  从流中解析的总长度：" + totalToRead + ",固定报头长度：" + counter.getCounter() + ",剩余长度：" + remLen);
//				System.out.println("首次读到的长度:" + len);
//				
//				if(totalToRead > len){
//					//totalToRead=2561964
//					buffer = ByteBuffer.allocate((int) totalToRead);
//					buffer.clear();
//					len = channel.read(buffer);
//					System.out.println("第二次读到的长度:" + len + "    " + buffer.limit());
//					//第二次读到的长度:23274    2561964
//					
//					buffer.flip();
//					packet = new byte[len];
//					buffer.get(packet, 0, len);
//					
//					buffer = ByteBuffer.allocate(100);
//				}
				
				MqttWireMessage message = MqttWireMessage.createWireMessage(packet);
				System.out.println("");
				logger.log("收到消息：" + MqttWireMessage.getTypeName(message.getType()));
				
				//此处校验客户端通道连接时间的发送 CONNECT 报文的时间差
				if(message.getType() == MqttWireMessage.MESSAGE_TYPE_CONNECT){
					long now = new Date().getTime();
					long start = Long.parseLong(att.toString());
					long fix = now - start;
					logger.log("时间差(ms): " + fix);
					// 如果发送 CONNECT 报文和通道连接的时间差大于10秒，则关闭连接
					if(fix > 10000000){
						close(channel);
						return;
					}
				}
				
				handleMessage(channel, message);
			}else{
				// 这里关闭 channel，因为客户端已经关闭 channel 或者异常了
				close(channel);
			}
		} catch (Exception e) {
			try {
				persistence.error(channel);
			} catch (Exception ex) {
				
			}
		}
	}

	public void write(SelectionKey key) {
		// TODO Auto-generated method stub
		
	}
	
	private void handleMessage(SocketChannel client, MqttWireMessage message) throws IOException, MqttException {
		switch (message.getType()) {
		case MqttWireMessage.MESSAGE_TYPE_CONNECT:
			handleConnect(client, (MqttConnect) message);
			break;
		case MqttWireMessage.MESSAGE_TYPE_DISCONNECT:
			handleDisconnect(client, (MqttDisconnect) message);
			break;
		case MqttWireMessage.MESSAGE_TYPE_SUBSCRIBE:
			handleSubscribe(client, (MqttSubscribe) message);
			break;
		case MqttWireMessage.MESSAGE_TYPE_UNSUBSCRIBE:
			handleUnsubscribe(client, (MqttUnsubscribe) message);
			break;
		case MqttWireMessage.MESSAGE_TYPE_PUBLISH:
			handlePublish(client, (MqttPublish) message);
			break;
		case MqttWireMessage.MESSAGE_TYPE_PUBACK:
			handlePubAck(client, (MqttPubAck) message);
			break;
		case MqttWireMessage.MESSAGE_TYPE_PUBREC:
			handlePubRec(client, (MqttPubRec) message);
			break;
		case MqttWireMessage.MESSAGE_TYPE_PUBREL:
			handlePubrel(client, (MqttPubRel) message);
			break;
		case MqttWireMessage.MESSAGE_TYPE_PUBCOMP:
			handlePubComp(client, (MqttPubComp) message);
			break;
		case MqttWireMessage.MESSAGE_TYPE_PINGREQ:
			handlePingreq(client, (MqttPingReq) message);
			break;
		case MqttWireMessage.MESSAGE_TYPE_CONNACK:
			
			break;
		case MqttWireMessage.MESSAGE_TYPE_SUBACK:
			
			break;
		case MqttWireMessage.MESSAGE_TYPE_UNSUBACK:
			
			break;
		case MqttWireMessage.MESSAGE_TYPE_PINGRESP:
			
			break;
		}
	}
	
	/**
	 * 处理心跳检测报文
	 * @param client
	 * @param message
	 * @throws IOException
	 * @throws MqttException 
	 */
	private void handlePingreq(SocketChannel client, MqttPingReq message) throws IOException, MqttException {
		/**
		 * 服务端必须发送 PINGRESP 报文响应客户端的 PINGREQ 报文。表示服务端还活着。
		 */
		sendMessage(client, new MqttPingResp());
	}

	/**
	 * 处理发布报文
	 * @author Xinxi
	 * @date 2018年1月21日下午3:32:01
	 * @param client
	 * @param message
	 * @return
	 * @throws IOException 
	 * @throws MqttException 
	 */
	private void handlePublish(SocketChannel client, MqttPublish message) throws IOException, MqttException {
		
		MqttAck ack = null;
		
		/**
		 * 
		 * QoS 0：
		 * QoS 1：返回 PUBACK 报文
		 * QoS 2：返回 PUBREC 报文(第 2 个报文)
		 * 
		 */
		MqttMessage msg = message.getMessage();
		String topic = message.getTopic();
		String key = persistence.getCleintKey(client);

		logger.log("消息ID："+ message.getMessageId() +"，qos："+ msg.getQos() +"，发送者：" + key + "，接收主题：" + topic + "，内容:" + new String(message.getPayload()));
		
		switch (msg.getQos()) {
		case 1:
			ack = new MqttPubAck(message);
			break;
		case 2:
			ack = new MqttPubRec(message);
			break;
		}
		
		
		//发送ack
		sendMessage(client, ack);
		
		//1.获取该消息主题的订阅集
		List<Map<String, Integer>> subscriptions = persistence.subscriptions(topic);
		if(subscriptions.size() == 0){
			logger.warn("消息接收者：" + topic + "，暂无客户端订阅。");
			return;
		}
		//2.循环判断每个订阅者是否在线
		for(Map<String, Integer> map : subscriptions){
			if(map != null && map.size() == 1){
				String clientId = null;
				Integer qos = null;
				
				Iterator<Entry<String, Integer>> iterator = map.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<String, Integer> entry = iterator.next();
					clientId = entry.getKey();
					qos = entry.getValue();
					break;
				}
				
				if(StringUtil.isNotEmpty(clientId) && qos != null){
					//将该消息的qos设置为订阅者希望设置的qos
					message.getMessage().setQos(qos);
					
					List<SocketChannel> channels = persistence.getCleintChannel(clientId);
					//在线订阅者
					if(channels != null && channels.size() > 0){
						//持久化消息
						switch (msg.getQos()) {
						case 0:
							//持久化为已发送消息
							persistence.persistenceMessage(clientId, msg, Random.next() + DataPersistence.MESSAGE_FILE_PUBLISH_EXTENSION);
							break;
						default:
							//持久化为发出中的消息
							persistence.persistenceMessage(clientId, msg, message.getMessageId() + DataPersistence.MESSAGE_FILE_WAIT_EXTENSION);
							break;
						}
						
						//发送消息
						for(SocketChannel channel : channels){
							sendMessage(channel, message);
						}
					}
					//未在线订阅者
					else{
						//持久化为保留消息
						persistence.persistenceMessage(clientId, msg, message.getMessageId() + DataPersistence.MESSAGE_FILE_WAIT_EXTENSION);
					}
				}
			}
		}
	}
	
	/**
	 * 处理发布确认报文(QoS 1 的最后一个报文)
	 * @param client
	 * @param message
	 * @throws IOException 
	 * @throws MqttException 
	 */
	private void handlePubAck(SocketChannel client, MqttPubAck message) throws MqttException, IOException {
		logger.log("消息ID："+ message.getMessageId());
		
		String topic = persistence.getCleintKey(client);
		String filename = message.getMessageId() + DataPersistence.MESSAGE_FILE_WAIT_EXTENSION;
		//1.从持久化中移除发出中的消息
		//1.1.通过 topic 和 msgId 得到该消息的内容
		byte[] bytes = persistence.getMessage(topic, filename);
		persistence.unPersistenceMessage(topic, filename);
		
		//2.将该消息持久化为已发送消息
		if(bytes.length > 1){
			//2.1.构建新的持久化消息
			MqttMessage msg = new MqttMessage();
			byte[] payload = new byte[bytes.length-1];
			System.arraycopy(bytes, 1, payload, 0, bytes.length-1);
			msg.setQos(bytes[0]);
			msg.setPayload(payload);
			filename = Random.next() + DataPersistence.MESSAGE_FILE_PUBLISH_EXTENSION;
			//2.2.持久化为已发送消息
			persistence.persistenceMessage(topic, msg, filename);
		}
	}
	
	/**
	 * 处理发布收到报文(QoS 2 的第 2 个报文)
	 * @author Xinxi
	 * @date 2018年1月21日下午4:18:24
	 * @param client
	 * @param message
	 * @throws IOException 
	 * @throws MqttException 
	 */
	private void handlePubRec(SocketChannel client, MqttPubRec message) throws IOException, MqttException {
		logger.log("消息ID："+ message.getMessageId());
		MqttPubRel ack = new MqttPubRel(message);
		sendMessage(client, ack);
	}
	
	/**
	 * 处理发布释放报文(QoS 2 的第 3 个报文)
	 * @author Xinxi
	 * @date 2018年1月21日下午4:18:24
	 * @param client
	 * @param message
	 * @throws IOException 
	 * @throws MqttException 
	 */
	private void handlePubrel(SocketChannel client, MqttPubRel message) throws IOException, MqttException {
		logger.log("消息ID："+ message.getMessageId());
		
		/**
		 * 返回 PUBCOMP 报文
		 * 
		 * PUBREL 控制报文固定报头的第 3,2,1,0 位是保留位，必须被设置为 0,0,1,0。 服务端必须将其它的任何值都当做是不合法的并关闭连接。
		 * 
		 */
		//校验固定报头的保留位
		byte info = message.getMessageInfo();
		if(info != 0x02){
			close(client);
			return;
		}
		
		MqttPubComp ack = new MqttPubComp(message);
		sendMessage(client, ack);
	}
	
	/**
	 * 处理发布完成报文(QoS 2 的第 4 个报文，也就是最后一个)
	 * @author Xinxi
	 * @date 2018年1月21日下午4:18:24
	 * @param client
	 * @param message
	 * @throws IOException 
	 * @throws MqttException 
	 */
	private void handlePubComp(SocketChannel client, MqttPubComp message) throws IOException, MqttException {
		String topic = persistence.getCleintKey(client);
		String filename = message.getMessageId() + DataPersistence.MESSAGE_FILE_WAIT_EXTENSION;
		//1.从持久化中移除发出中的消息
		//1.1.通过 topic 和 msgId 得到该消息的内容
		byte[] bytes = persistence.getMessage(topic, filename);
		persistence.unPersistenceMessage(topic, filename);
		
		//2.将该消息持久化为已发送消息
		if(bytes.length > 1){
			//2.1.构建新的持久化消息
			MqttMessage msg = new MqttMessage();
			byte[] payload = new byte[bytes.length-1];
			System.arraycopy(bytes, 1, payload, 0, bytes.length-1);
			msg.setQos(bytes[0]);
			msg.setPayload(payload);
			filename = Random.next() + DataPersistence.MESSAGE_FILE_PUBLISH_EXTENSION;
			//2.2.持久化为已发送消息
			persistence.persistenceMessage(topic, msg, filename);
		}
	}
	
	/**
	 * 处理订阅报文
	 * @author Xinxi
	 * @date 2018年1月21日下午3:31:49
	 * @param client
	 * @param message
	 * @return
	 * @throws IOException 
	 * @throws MqttException 
	 */
	private void handleSubscribe(SocketChannel client, MqttSubscribe message) throws IOException, MqttException {
		
		/**
		 * 
		 * SUBSCRIBE 控制报文固定报头的第 3，2，1，0 位是保留位，必须设置为 0，0，1，0.服务端必须将其它的任何值都当做是不合法的并关闭网络连接。
		 * 
		 * 响应：
		 * 
		 * 服务端收到客户端发送的一个 SUBSCRIBE 报文时，必须使用 SUBACK 报文响应。
		 * SUBACK 报文必须和等待确认的 SUBSCRIBE 报文有相同的报文标识符。
		 * 
		 * 允许服务端在发送 SUBACK 报文之前就开始发送与订阅匹配的 PUBLISH 报文。
		 * 
		 * 如果服务端收到一个 SUBSCRIBE 报文，报文的主题过滤器与一个现存订阅的主题过滤器相同，那么必须
		 * 使用新的订阅彻底替换现存的订阅。新订阅的主题过滤器和之前订阅的相同，但是它的最大 QoS 值可以不同。
		 * 与这个主题过滤器匹配的任何现存的保留消息必须被重发，但是发布流程不能中断。
		 * 
		 * 如果主题过滤器不同于任何现存订阅的过滤器，服务端会创建一个新的订阅并发送所有匹配的保留消息。
		 * 
		 * 如果服务端收到包含多个主题过滤器的 SUBSCRIBE 报文，它必须如同收到了一系列的多个 SUBSCRIBE
		 * 报文一样处理那个，处理需要将他们的响应合并到一个单独的 SUBACK 报文发送。
		 * 
		 * 服务端发送给客户端的 SUBACK 报文对每一对主题过滤器 和 QoS 等级都必须包含一个返回码。
		 * 这个返回码必须表示那个订阅被授予的最大 QoS 等级，或者表示这个订阅失败。服务端可以授权比订阅者要求的低一些的 QoS 等级。
		 * 为响应订阅而发出的消息的有效荷载的 QoS 必须是原始发布消息的 QoS 和服务端授予的 QoS 两者中的最小值。
		 * 如果原始消息的 QoS 是 1 而被授予的最大 QoS 是 0，允许服务端重复发送一个消息的副本给订阅者。
		 * 
		 * 
		 * 非规范示例
		 * 对某个特定的主题过滤器，如果正在订阅的客户端被授予的最大 QoS 等级是 1，那么匹配这个过滤器的 QoS 等级 0 的应用
		 * 消息会按 QoS 等级 0 分发给这个客户端。这意味着客户端最多收到这个消息的一个副本。从另一方面说，
		 * 发布给同一主题的 QoS 等级 2 的消息会被服务端降级到 QoS 等级 1 再分发给客户端，因此客户端可能会
		 * 收到重复的消息副本。
		 * 
		 * 非规范评注
		 * 使用 QoS 等级 2 订阅一个主题过滤器等于是说：我想要按照它们发布时的 QoS 等级接受匹配这个
		 * 过滤器的消息。这意味着，确定消息分发时可能的最大 QoS 等级是发布者的责任，
		 * 而订阅着可以要求服务端降低 QoS 到更适合它的等级。
		 * 
		 * 
		 */
		
		//校验固定报头的保留位
		byte info = message.getMessageInfo();
		if(info != 0x02){
			close(client);
			return;
		}
		
		String[] topics = message.getTopics();
		int[] qoss = message.getQos();
		
		//校验 topic，目前暂不支持通配符
		for(String topic : topics){
			if(StringUtil.isNotEmpty(topic)){
				Configuration.validateClientId(topic);
			}
		}
		//校验 qos
		for(int qos : qoss){
			MqttMessage.validateQos(qos);
		}
		
		//1.通过客户端 channel 获取对应的客户端ID key
		String key = persistence.getCleintKey(client);
		if(StringUtil.isEmpty(key)){
			close(client);
			return;
		}
		
		//2.将订阅的主题集分别持久化，里面包含订阅该主题的客户端
		int[] grantedQos = persistence.subscribe(key, topics, qoss);
		
		//3.发送ack
		sendMessage(client, new MqttSuback(message, grantedQos));
	}
	
	/**
	 * 处理取消订阅报文
	 * @author Xinxi
	 * @date 2018年1月21日下午4:16:01
	 * @param client
	 * @param message
	 * @throws IOException 
	 * @throws MqttException 
	 */
	private void handleUnsubscribe(SocketChannel client, MqttUnsubscribe message) throws IOException, MqttException {

		/**
		 * UNSUBSCRIBE 报文提供的主题过滤器（无论是否包含通配符）必须与服务端持有的这个客户端的当前主题过滤器集合逐个字符比较。
		 * 如果有任何过滤器完全匹配，那么它（服务端）自己的订阅将被删除，否则不会有进一步的处理。
		 * 
		 * 如果服务端删除了一个订阅：
		 * <ol>
		 * 		<li>它必须停止分发任何消息给这个客户端。</li>
		 * 		<li>它必须完成分发任何已经开始往客户端发送的 QoS 1 和 QoS 2 的消息。</li>
		 * 		<li>它可以继续发送任何现存的准备分发给客户端的缓存消息。</li>
		 * </ol>
		 * 
		 * 服务端必须发送 UNSUBACK 报文响应客户端的 UNSUBSCRIBE 请求。 UNSUBACK 报文必须包含和
		 * UNSUBSCRIBE 报文相同的报文标识符。即使没有删除任何主题订阅，服务端也必须发送一个 SUBACK 响应。
		 * 
		 * 如果服务端收到包含多个主题过滤器的 UNSUBSCRIBE 报文，它必须如同收到了一系列的多个 UNSUBSCRIBE 报文一样处理那个报文，
		 * 除了将他们的响应合并到一个单独的 UNSUBACK 报文外。
		 * 
		 */
		
		//校验固定报头的保留位
		byte info = message.getMessageInfo();
		if(info != 0x02){
			close(client);
			return;
		}
		
		String[] topics = message.getTopics();
		
		//校验 topic，目前暂不支持通配符
		for(String topic : topics){
			if(StringUtil.isNotEmpty(topic)){
				Configuration.validateClientId(topic);
			}
		}
		
		//1.通过客户端 channel 获取对应的客户端ID key
		String key = persistence.getCleintKey(client);
		if(StringUtil.isEmpty(key)){
			close(client);
			return;
		}
		
		//2.从持久化中删除订阅主题
		persistence.unsubscribe(key, topics);
		
		//3.构建一个ack，该ack的msgId是收到取消订阅报文的msgId
		MqttUnsubAck ack = new MqttUnsubAck(message);
		
		//4.发送ack
		sendMessage(client, ack);
		
	}

	/**
	 * 处理连接报文
	 * @author Xinxi
	 * @date 2018年1月21日下午3:30:39
	 * @param client
	 * @param message
	 * @return
	 * @throws IOException 
	 * @throws MqttException 
	 */
	private void handleConnect(SocketChannel client, MqttConnect message) throws IOException, MqttException {
		MqttConnack ack = null;
		
		/**
		 * 1.校验协议名
		 * 如果协议名不正确服务端可以断开客户端的连接，也可以按照某些其它规范处理 CONNECT 报文，
		 * 对于后一种情况，按照本规范，服务端不能继续处理 CONNECT 报文
		 * 
		 * 例如：可以使用协议名来识别 MQTT 流量
		 */
		if(message.getProtocolName() == null || !MqttConnect.PROTOCOL_NAME_DEFAULT.equals(message.getProtocolName())){
			close(client);
			return;
		}
		
		/**
		 * 2.校验协议级别
		 * 对于 3.1.1 版协议，协议级别字段值是 4(0x04)。
		 * 如果发现不支持的协议级别，服务端必须给发送一个返回码为 0x01(不支持的协议级别) 的 CONNACK 报文响应 CONNECT 报文，然后断开客户端的连接
		 */
		if(message.getProtocolLevel() != MqttConnect.PROTOCOL_LEVEL_DEFAULT){
			ack = new MqttConnack(MqttConnack.CONNECT_FAILE_PROTOCOL);
			sendMessage(client, ack);
			close(client);
			return;
		}
		
		/**
		 * 3.校验连接标志
		 * 服务端必须验证 CONNECT 控制报文的保留位（第 0 位）是否为 0，如果不为 0，必须断开客户端连接
		 */
		byte flags = message.getConnectFlags();
		
		//验证保留位
		byte temp = flags;
		temp = (byte) (temp >> 0);
		byte reservedFlag = (byte)(temp & 1);
		if(reservedFlag != 0x00){
			close(client);
			return;
		}
		
		/**
		 * 3.1.清理会话(cleanSession)
		 * 
		 * 位置：连接标志字节的第 1 位
		 * 
		 * 如果清理会话标志被设置为 0，服务端必须基于当前会话（使用客户端标识符）的状态恢复与客户端的通信。
		 * 如果没有与这个客户端标识符关联的会话，服务端必须创建一个新的会话。当连接断开后，客户端和服务端必须保存会话信息。
		 * 当清理会话标志为 0 的会话连接断开之后，服务端必须将之后的 QoS 1 和 QoS 2 级别的消息保存为会话状态的一部分，
		 * 如果这些消息匹配断开连接时客户端的任何订阅。服务端也可以保存相同满足条件的 QoS 0 级别的消息。
		 * 
		 * 如果清理会话标志被设置为 1，客户端和服务端必须丢弃之前的任何会话并开始一个新的会话。会话仅持续和网络连接同样长的时间。
		 * 与这个会话关联的状态数据不能被任何之后的会话重用。
		 * 
		 * 客户端的会话状态包括：
		 * <ul>
		 * 		<li>已经发送给服务端，但是还没有完成确认的 QoS 1 和 QoS 2 级别的消息</li>
		 * 		<li>已从服务端接收，但是还没有完成确认的 QoS 2 级别的消息</li>
		 * <ul>
		 * 
		 * 服务端的会话状态包括：
		 * <ul>
		 * 		<li>会话是否存在，即使会话状态的其它部分都是空</li>
		 * 		<li>客户端的订阅消息</li>
		 * 		<li>已经发送给客户端，但是还没有完成确认的 QoS 1 和 QoS 2 级别的消息</li>
		 * 		<li>即将传输给客户端的 QoS 1 和 QoS 2 级别的消息</li>
		 * 		<li>已从客户端接收，但是还没有完成确认的  QoS 2 级别的消息</li>
		 * 		<li>已选，准备发送给客户端的 QoS 0 级别的消息</li>
		 * <ul>
		 * 
		 * 保留消息不是服务端会话状态的一部分，会话终止时不能删除保留消息。
		 * 
		 * 当清理会话标志被设置为 1 时，客户端和服务端的状态删除不需要是原子操作。
		 * 
		 * 注：具体参照3.1.2.4 清理会话 [非规范评注]
		 * 
		 */
		persistence.open(message.getClientId(), client, message.isCleanSession());

		
		/**
		 * 3.2.遗嘱标志
		 * 
		 * 位置：连接标志的第 2 位
		 * 
		 * 遗嘱标志被设置为 1，表示如果连接被接受了，遗嘱消息必须被存储在服务端并且与这个网络连接关联。
		 * 之后网络连接关闭时，服务端必须发布这个遗嘱消息，除非服务端收到 DISCONNECT 报文时删除了这个遗嘱消息。
		 * 
		 * 遗嘱消息发布的条件，包括但不限于：
		 * <ul>
		 * 		<li>服务端检测到了一个 I/O 错误活着网络故障。</li>
		 * 		<li>客户端在保持连接的时间未能通讯。</li>
		 * 		<li>客户端没有先发送 DISCONNECT 报文直接关闭了网络连接。</li>
		 * 		<li>由于协议错误服务端关闭了网络连接。</li>
		 * </ul>
		 * 
		 * 如果遗嘱标志被设置为 1，连接标志中的 Will QoS 和 Will Retain 字段会被服务端用到，同时有效荷载中必须包含 Will Topic 和 Will Message字段。
		 * 
		 * 一旦被发布或者服务端收到了客户端发送的 DISCONNECT 报文，遗嘱消息就必须从存储的会话状态中移除。
		 * 
		 * 如果遗嘱标志被设置为 0，连接标志中的 Will Qos 和 Will Retain 字段必须设置为 0，并且有效荷载中不能包含 Will Topic 和 Will Message 字段。
		 * 
		 * 如果遗嘱标志被设置为 0，网络连接断开时，不能发送遗嘱消息。
		 * 
		 * 服务端应该迅速发布遗嘱消息。在关机或故障的情况下，服务端可以推迟遗嘱消息的发布直到之后的重启。
		 * 如果发生了这种情况，在服务器故障和遗嘱消息被发布之间可能会有一个延迟。
		 * 
		 */
		
		
		/**
		 * 3.3.遗嘱 QoS
		 * 
		 * 位置：连接标志的第 4 位和第 3 位
		 * 
		 * 这两位用于指定发布遗嘱消息时使用的服务质量等级。
		 * 
		 * 如果遗嘱标志被设置为 0，遗嘱 QoS 也必须设置为 0(0x00)。
		 * 如果遗嘱标志被设置为1，遗嘱 QoS 的值可以等于 0(0x00)，1(0x01)，2(0x02)。它的值不能等于3。
		 * 
		 */
		
		
		
		/**
		 * 3.4.遗嘱保留
		 * 
		 * 位置：连接标志的第 5 位
		 * 
		 * 如果遗嘱消息被发布时需要保留，需要指定这一位的值。
		 * 
		 * 如果遗嘱标志被设置为 0，遗嘱保留(Will Retain)标志也必须设置为 0。
		 * 如果遗嘱标志被设置为 1：
		 * <ul>
		 * 		<li>如果遗嘱保留被设置为 0，服务端必须将遗嘱消息当作非保留消息发布</li>
		 * 		<li>如果遗嘱保留被设置为 1，服务端必须将遗嘱消息当作保留消息发布</li>
		 * </ul>
		 * 
		 */
		
		
		/**
		 * 3.5.密码标志
		 * 
		 * 位置：连接标志的第 6 位
		 * 
		 * 如果密码标志被设置为 0，有效荷载中不能包含密码字段。
		 * 如果密码标志被设置为 1，有效荷载中必须包含密码字段。
		 * 
		 * 如果用户名标志被设置为 0，密码标志也必须设置为 0。
		 * 
		 */
		
		
		/**
		 * 3.6.用户名标志
		 * 
		 * 位置：连接标志的第 7 位
		 * 
		 * 如果用户名标志被设置为 0，有效荷载中不能包含用户名字段。
		 * 如果用户名标志被设置为 1，有效荷载中必须包含用户名字段。
		 * 
		 */
		
		
		/**
		 * 4.保持连接
		 * 
		 * 保持连接时一个以秒为单位的时间间隔，表示为一个 16 位的字，
		 * 它是指在客户端传输完成一个控制报文的时刻到发送下一个报文的时刻，两者之间允许空闲的最大时间间隔。
		 * 客户端负责保证控制报文发送的时间间隔不超过保持连接的值。
		 * 如果没有任何其它的控制报文可以发送，客户端必须发送一个 PINGREQ 报文。
		 * 
		 * 不管保持连接的值是多少，客户端任何时候都可以发送 PINGREQ 报文，并且使用 PINGRESP 报文以判断网络和服务端的活动状态。
		 * 
		 * 如果保持连接的值为零，并且服务端在一点五倍的保持连接时间内没有收到客户端的控制报文，它必须断开客户端的网络连接，认为网络连接已断开。
		 * 
		 * 客户端发送 PINGREQ 报文之后，如果在合理的时间内扔没有收到 PINGRESP 报文，它应该关闭服务端的网络连接。
		 * 
		 * 保持连接的值为零表示关闭保持连接。这意味着，服务端不需要因为客户端不活跃而断开连接。
		 * 注意：不管保持连接的值是多少，任何时候，只要服务端认为客户端是不活跃的或无响应的，可以断开客户端的连接。
		 * 
		 * [非规范评注]
		 * 保持连接的实际值是由应用指定，一般是几分钟。允许的最大值是 18 小时 12 分 15 秒。
		 * 
		 */
		
		
		/**
		 * 5.有效荷载
		 * 
		 * CONNECT 报文的有效荷载包含一个或多个以长度为前缀的字段，可变报头中的标志决定是否包含这些字段。
		 * 如果包含的话，必须按这个顺序出现：客户端标识符，遗嘱主题，遗嘱消息，用户名，密码
		 */
		
		/**
		 * 5.1.客户端标识符（clientId）
		 * 
		 * 服务端使用客户端标识符识别客户端。连接服务端的每个客户端都有唯一客户端标识符。
		 * 客户端和服务端都必须使用 ClientId 识别两者之间的 MQTT 会话相关的状态。
		 * 
		 * 客户端标识符必须存在而且必须是 CONNECT 报文有效荷载的第一个字段。
		 * 
		 * 客户端标识符必须是 1.5.3 节定义的 UTF-8 编码字符串。
		 * 
		 * 服务端必须允许 1 到 23 个字节长的 UTF-8 编码的客户端标识符，客户端标识符只能包含这些字符：
		 * "0123456789"
		 * "abcdefghijklmnopqrstuvwxyz"
		 * "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
		 * (大写字母，小写字母和数据)
		 * 
		 * 服务端可以允许编码后超过 23 个字节的客户端标识符。
		 * 服务端可以允许包含不是上面列表字符的客户端标识符。
		 * 
		 * 服务端可以允许客户端提供一个零字节的客户端标识符，如果这样做了，服务端必须将这看作特殊情况并分配唯一的客户端表示符给那个客户端。
		 * 然后它必须假设客户端提供了那个唯一的客户端标识符，正常处理这个 CONNECT 报文。
		 * 
		 * 如果客户端提供了一个零字节的客户端标识符，它必须同时将清理会话标志设置为1。
		 * 
		 * 如果客户端提供的 ClientId 为零字节并且清理会话标志为 0，
		 * 服务端必须发送返回码 0x02（表示标识符不合格）的 CONNACK 报文响应客户单的 CONNECT 报文，然后关闭网络连接。
		 * 
		 * 如果服务端拒绝了这个 ClientId，它必须发送返回码为 0x02（表示标识符不合格）的 CONNACK 报文
		 * 响应客户端的 CONNECT 报文，然后关闭网络连接。
		 * 
		 * 非规范评注：
		 * 客户端实现可以提供一个方便的方法用于生成随机的 ClientId。当清理会话标志被设置为 0 时应该主动放弃使用这种方法。
		 * 
		 */
		

		
		/**
		 * 5.2.遗嘱主题
		 * 
		 * 如果遗嘱标志被设置为 1，有效荷载的下一个字段是遗嘱主题（Will Topic）。
		 * 遗嘱主题必须是 1.5.3 节定义的 UTF-8 编码字符串。
		 * 
		 */

		
		/**
		 * 5.3.遗嘱消息
		 * 
		 * 如果遗嘱标志被设置为 1，有效荷载的下一个字段是遗嘱消息。遗嘱消息定义了将被发布到遗嘱主题的应用消息，见 3.1.2.5 节的描述。
		 * 这个字段由一个两字节的长度和遗嘱消息的有效荷载组成，表示为零字节或多个字节序列。
		 * 长度给出了跟在后面的数据的字节数，不包含长度字段本身占用的两个字节。
		 * 
		 * 遗嘱消息被发布到遗嘱主题时，它的有效荷载只包含这个字段的数据部分，不包含开头的两个长度字节。
		 * 
		 */
		
		
		/**
		 * 5.4.用户名
		 * 
		 * 如果用户名标志被设置为 1，有效荷载的下一字段就是它。
		 * 用户名必须是 1.5.3 节定义的 UTF-8 编码字符串。服务端可以将它用于身份验证和授权。
		 * 
		 */

		
		/**
		 * 5.5.密码
		 * 
		 * 如果密码标志被设置为 1，有效荷载的下一个字段就是它。
		 * 密码字段包含一个两字节的长度字段，长度表示二进制数据的字节数（不包含长度字段本身占用的两个字节），
		 * 后面跟着 0 到 65535 字节的二进制数据。
		 * 
		 * 图例：
		 * ------------------------------------------
		 * | Bit	| 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
		 * ------------------------------------------
		 * | byte 1	|		数据长度 MSB				|
		 * ------------------------------------------
		 * | byte 2	|		数据长度 LSB				|
		 * ------------------------------------------
		 * | byte 3	|	如果长度大于 0，这里就是数据部分		|
		 * ------------------------------------------
		 * 
		 */
		
		
		/**
		 * 响应
		 * 
		 * 注意：服务器可以在同一个 TCP 端口或其它网络端点上支持多种协议（包括本协议的早期版本）。
		 * 
		 * 如果服务器确定协议是 MQTT 3.1.1，那么它按照下面的方法验证连接请求。
		 * <ol>
		 * 		<li>网络连接建立后，如果服务端在合理的时间内没有收到 CONNECT 报文，服务端应该关闭这个连接。</li>
		 * 		<li>服务端必须按照 3.1 节的要求验证 CONNECT 报文，如果报文不符合规范，服务端不发送 CONNACK 报文直接关闭网络连接。</li>
		 * 		<li>服务端可以检查 CONNECT 报文的内容是不是满足任何进一步的限制，可以执行身份验证和授权检查。
		 * 如果任何一项检查没通过，按照 3.2 节的描述，它应该发送一个适当的、返回码为零的 CONNACK 响应，并且必须关闭这个网络连接。</li>
		 * </ol>
		 * 
		 * 如果验证成功，服务端会执行下列步骤。
		 * <ol>
		 * 		<li>如果 ClientId 表明客户端已经连接到这个服务端，那么服务端必须断开原有的客户端两节。</li>
		 * 		<li>服务端必须按照 3.1.2.4 节的描述执行清理会话的过程。</li>
		 * 		<li>服务端必须发送返回码为零的 CONNACK 报文作为 CONNECT 报文的确认响应。</li>
		 * 		<li>开始消息分发和保持连接状态监视</li>
		 * </ol>
		 * 
		 * 允许客户端在发送 CONNECT 报文之后立即发送其它的控制报文；客户端不需要等待服务端的 CONNACK报文。
		 * 如果服务端拒绝了 CONNECT，它不能处理客户端在 CONNECT 报文之后发送的任何数据。
		 * 
		 * 非规范评注：
		 * 客户端通常会等待一个 CONNACK 报文。然而客户端有权在收到 CONNACK 之前发送控制报文，
		 * 由于不需要维持连接状态，这可以建华客户端的实现。
		 * 
		 */
		
		logger.log("协议名：" + message.getProtocolName());
		logger.log("协议级别：" + message.getProtocolLevel());
		
		byte tm = flags;
		byte[] array = ByteUtil.byte2BitArray(tm);
		logger.log("连接标志：" + message.getConnectFlags());
		logger.log("连接标志 byte 转换后的结果： " +array[0]+array[1]+array[2]+array[3]+array[4]+array[5]+array[6]+array[7]);
		logger.log("连接标志：   保留位[0]："+array[7]+
				"，清除会话[1]："+array[6]+
				"，遗嘱标志[2]："+array[5]+
				"，遗嘱QoS[3,4]："+array[4]+array[3]+
				"，遗嘱保留[5]："+array[2]+
				"，密码[6]："+array[1]+
				"，账号[7]："+array[0]);
		logger.log("清除会话：" + message.isCleanSession());
		logger.log("保持连接时间：" + message.getKeepAliveInterval());
		
		logger.log("客户端标识符：" + message.getClientId());
		logger.log("遗嘱主题：" + message.getWillTopic());
		if(message.getWillMessage() == null){
			logger.log("遗嘱消息：null");
		}else if(message.getWillMessage() != null && message.getWillMessage().getPayload() != null){
			logger.log("遗嘱消息：{payload: "+new String(message.getWillMessage().getPayload())+", qos: "+message.getWillMessage().getQos()+", retain: "+message.getWillMessage().isRetained()+"}");
		}
		logger.log("用户名：" + message.getUserName());
		if(message.getPassword() == null){
			logger.log("密码：null");
		}else{
			logger.log("密码：" + new String(message.getPassword()));
		}
		
		
		//持久化遗嘱消息
		if(StringUtil.isNotEmpty(message.getWillTopic()) && message.getWillMessage() != null && message.getWillMessage().isRetained()){
			persistence.persistenceMessage(message.getWillTopic(), message.getWillMessage(), DataPersistence.MESSAGE_FILE_WILL_EXTENSION);
		}
		
		//发送连接确认报文 CONNACK
		ack = new MqttConnack();
		sendMessage(client, ack);
		

		File[] waitMessages = persistence.getPersMessages(message.getClientId(), DataPersistence.MESSAGE_FILE_WAIT_FILTER);
		logger.log("重发消息："+ waitMessages.length +" 条");
		restoreMessages(waitMessages, message.getClientId(), client);
	}
	
	/**
	 * 客户端连接成功后恢复消息<br>
	 * 保留消息：当前客户端离线时接收到的消息<br>
	 * 已发出消息：当前客户端在线时发出的未得到ack的消息<br>
	 * @param files 本地消息文件数组
	 * @param topic 发送的主题
	 * @param channel 发送的通道
	 * @throws IOException
	 * @throws MqttException 
	 */
	private void restoreMessages(File[] files, String topic, SocketChannel channel) throws IOException, MqttException {
		for(File file : files){
			byte[] bytes = FileUtil.readFileByte(file);
			if(bytes.length > 1){
				String name = file.getName();
				int pos = name.indexOf(".");
				name = name.substring(0, pos);
				int msgId = Integer.parseInt(name);
				int qos = bytes[0];
				byte[] payload = new byte[bytes.length-1];
				System.arraycopy(bytes, 1, payload, 0, bytes.length-1);
				
				logger.log("恢复消息中。msgId："+ msgId +"，qos："+ qos +"，接收者主题："+ topic +"，内容："+ new String(payload));
				MqttMessage msg = new MqttMessage();
				msg.setQos(qos);
				msg.setPayload(payload);
				MqttPublish publish = new MqttPublish(topic, msg);
				publish.setMessageId(msgId);
				
				sendMessage(channel, publish);
			}
		}
	}

	/**
	 * 处理断开连接报文
	 * @author Xinxi
	 * @date 2018年1月21日下午4:15:32
	 * @param client
	 * @param message
	 * @throws IOException 
	 */
	private void handleDisconnect(SocketChannel client, MqttDisconnect message) throws IOException {
		
		/**
		 * 服务端在收到 DISCONNECT 报文时：
		 * <ul>
		 * 		<li>必须丢弃任何与当前连接关联的未发布的遗嘱消息，具体见 3.1.2.5 节</li>
		 * 		<li>应该关闭网络连接，如果客户端还没有这么做。</li>
		 * </ul>
		 * 
		 * 服务端必须验证所有的保留位都被设置为 0，如果它们不为 0 必须断开连接。
		 * 
		 */
		
		//1.验证保留位
		byte info = message.getMessageInfo();
		if(info != 0){
			close(client);
			return;
		}
		
		//2.丢弃任何与当前连接关联的未发布的遗嘱消息
		String topic = persistence.getCleintKey(client);
		persistence.unPersistenceMessage(topic, DataPersistence.MESSAGE_FILE_WILL_EXTENSION);
		
		//3.关闭连接
		close(client);
		
	}

	/**
	 * 关闭客户端连接
	 * @param client
	 * @throws IOException 
	 */
	private void close(SocketChannel client) throws IOException {
		persistence.close(client);
	}

	private void sendMessage(SocketChannel client, MqttWireMessage message) throws MqttException, IOException {
		if(client != null && message != null){
			byte[] bytes = message.getHeader();
			byte[] pl = message.getPayload();
			
			buffer.clear();  
			buffer.put(bytes);  
			buffer.put(pl);  
			buffer.flip();  
			
			client.write(buffer);
			//输出到通道    
			client.write(buffer);
		}
	}
}
