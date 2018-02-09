package org.livemq.client;

import java.util.Hashtable;
import java.util.Vector;

import org.livemq.Configuration;
import org.livemq.MqttClientCallback;
import org.livemq.MqttClientPersistence;
import org.livemq.MqttMessage;
import org.livemq.internal.wire.MqttAck;
import org.livemq.internal.wire.MqttConnack;
import org.livemq.internal.wire.MqttConnect;
import org.livemq.internal.wire.MqttPingReq;
import org.livemq.internal.wire.MqttPubAck;
import org.livemq.internal.wire.MqttPubComp;
import org.livemq.internal.wire.MqttPubRec;
import org.livemq.internal.wire.MqttPubRel;
import org.livemq.internal.wire.MqttPublish;
import org.livemq.internal.wire.MqttWireMessage;

/**
 * 客户端内部核心处理类
 * @author w.x
 * @date 2018年2月8日 下午3:29:30
 */
public class MqttHandler {

	/** 最小报文标识符*/
	private static final int MIN_MSG_ID = 0;
	/** 最大报文标识符*/
	private static final int MAX_MSG_ID = Integer.MAX_VALUE;
	/** next 报文标识符*/
	private int nextMsgId = MIN_MSG_ID + 1;
	
	/** 在使用的消息ID集*/
	private Hashtable<Integer, Integer> inUseMsgIds;
	
	/** 待发送的消息(PUBLISH 的消息)*/
	volatile private Vector<MqttWireMessage> pendingMessages;
	/** 待发送的消息(非 PUBLISH 的消息)*/
	volatile private Vector<MqttWireMessage> pendingFlows;
	
	/** 队列锁*/
	private Object queueLock = new Object();
	
	private MqttCore core;
	private Configuration config;
	private MqttClientPersistence persistence;
	private MqttClientCallback callback;
	
	private long keepAlive;
	private long lastPing = 0;
	private MqttWireMessage pingMessage;
	
	public MqttHandler(MqttCore core, Configuration config, MqttClientPersistence persistence){
		this.core = core;
		this.config = config;
		this.persistence = persistence;
		setKeepAlive(this.config.getKeepAliveInterval());
		
		this.inUseMsgIds = new Hashtable<Integer, Integer>();
		this.pingMessage = new MqttPingReq();
		this.pendingMessages = new Vector<MqttWireMessage>();
		this.pendingFlows = new Vector<MqttWireMessage>();
	}
	
	/**
	 * 从消息队列中得到新的待发送报文
	 * @author w.x
	 * @date 2018年2月8日 下午4:12:08
	 */
	public MqttWireMessage get() {
		MqttWireMessage result = null;
		
		synchronized (queueLock) {
			while(result == null){
				if(pendingMessages.isEmpty() && pendingFlows.isEmpty()){
					try {
						// TODO 等待ping的预执行时间(需要计算)
						queueLock.wait(1000);
					} catch (Exception e) { }
				}
				
				/**
				 * 如果客户端未连接时:
				 * 1.如果流队列中没消息时返回null
				 * 2.流队列有消息，但是第一位的消息不是MqttConnect时返回null
				 */
				if(!core.isConnected() && (pendingFlows.isEmpty() || !(pendingFlows.elementAt(0) instanceof MqttConnect))){
					return null;
				}
				
				/**
				 * 这里需要发送一个心跳检测来保持session的存活
				 */
				checkForActivity();
				
				if(!pendingMessages.isEmpty()){
					result = pendingMessages.elementAt(0);
					pendingMessages.removeElementAt(0);
				}
				else if(!pendingFlows.isEmpty()){
					result = pendingFlows.elementAt(0);
					pendingFlows.removeElementAt(0);
				}
			}
		}
		
		return result;
	}

	/**
	 * 发送队列中的消息
	 * 发送消息之前，如果有必要（如：QoS 1、QoS 2 或 MqttPubRel 这三种消息）的情况下会先将该消息持久化
	 * 然后在 MqttReceiver 中消费对应的消息
	 * @author w.x
	 * @date 2018年2月8日 下午4:28:06
	 */
	public void send(MqttWireMessage message) {
		settingLastPing();
		
		if (message.isMessageIdRequired() && (message.getMessageId() == 0)) {
			message.setMessageId(getNextMessageId());
		}
		
		if(message instanceof MqttPublish){
			synchronized (queueLock) {
				
				MqttMessage innerMessage = ((MqttPublish) message).getMessage();
				
				//1.持久化
				switch (innerMessage.getQos()) {
				case 1:
					persistence.put(String.valueOf(message.getMessageId()), message);
					break;
				case 2:
					persistence.put(String.valueOf(message.getMessageId()), message);
					break;
				}
				
				//2.加入队列
				pendingMessages.addElement(message);
				queueLock.notifyAll();
			}
		}else{
			if(message instanceof MqttConnect){
				synchronized (queueLock) {
					pendingFlows.insertElementAt(message, 0);
					queueLock.notifyAll();
				}
			}else{
				
				synchronized (queueLock) {
					pendingFlows.addElement(message);
					queueLock.notifyAll();
				}
			}
		}
	}

	/**
	 * 移除队列中的消息
	 * @author w.x
	 * @date 2018年2月8日 下午4:29:13
	 */
	public void undo(MqttWireMessage message) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 处理收到的报文
	 * @author w.x
	 * @date 2018年2月8日 下午4:10:02
	 */
	public void handleReceivedMessage(MqttWireMessage message) {
		settingLastPing();
		
		switch (message.getType()) {
		case MqttWireMessage.MESSAGE_TYPE_CONNACK:
			if(message instanceof MqttConnack && core.isConnecting()){
				core.setConState(MqttCore.CONNECTED);
			}
			break;
		case MqttWireMessage.MESSAGE_TYPE_SUBACK:
			
			break;
		case MqttWireMessage.MESSAGE_TYPE_UNSUBACK:
			
			break;
		case MqttWireMessage.MESSAGE_TYPE_PINGRESP:
			
			break;
		case MqttWireMessage.MESSAGE_TYPE_PUBACK:
			/**
			 * 1.删除客户端缓存的已发送消息
			 * 2.释放 msgId
			 */
			
			//1.
			persistence.remove(String.valueOf(message.getMessageId()));
			
			//2.
			releaseMsgId(message.getMessageId());
			
			break;
		case MqttWireMessage.MESSAGE_TYPE_PUBREC:
			/**
			 * qos 2 的第二个报文，需要回复第三个报文 PUBREL
			 */
			MqttPubRel pubRel = new MqttPubRel((MqttPubRec) message);
			send(pubRel);
			
			break;
		case MqttWireMessage.MESSAGE_TYPE_PUBCOMP:
			/**
			 * QoS 2 的第四个(最后一个)报文
			 * 1.删除客户端缓存的已发送消息
			 * 2.释放 msgId
			 */
			
			//1.
			persistence.remove(String.valueOf(message.getMessageId()));
			
			//2.
			releaseMsgId(message.getMessageId());
			
			break;
		
		//下面两种报文类型为消息报文，不是ack
		case MqttWireMessage.MESSAGE_TYPE_PUBLISH:
			/**
			 * 根据消息的 QoS 判断
			 * qos 1 时发送 PUBACK
			 * qos 2 时发送 PUBREC(qos 2 的第二个报文)
			 */
			MqttPublish publish = (MqttPublish) message;
			MqttMessage msg = publish.getMessage();
			MqttAck ack = null;
			
			switch (msg.getQos()) {
			case 1:
				ack = new MqttPubAck(publish);
				break;
			case 2:
				ack = new MqttPubRec(publish);
				break;
			}
			send(ack);
			
			break;
		case MqttWireMessage.MESSAGE_TYPE_PUBREL:
			/**
			 * qos 2 的第三个报文，需要回复 PUBCOMP(qos 2 的第四个（最后一个）报文)
			 */
			MqttPubComp pubComp = new MqttPubComp((MqttPubRel) message);
			send(pubComp);
			
			break;
		}
		
		
		if(message instanceof MqttAck){
			callback.messageArrived("ack", message);
		}else{
			callback.messageArrived("message", message);
		}
	}
	
	/**
	 * 处理发送完成的报文
	 * @author w.x
	 * @date 2018年2月8日 下午4:13:30
	 */
	public void handleSentMessage(MqttWireMessage message) {
		callback.deliveryComplete(message);
	}
	
	/**
	 * 释放 msgId
	 * @param id
	 */
	private void releaseMsgId(int id) {
		Integer msgId = new Integer(id);
		if(inUseMsgIds.containsKey(msgId)){
			inUseMsgIds.remove(msgId);
		}
	}
	
	private void settingLastPing() {
		lastPing = System.currentTimeMillis();
	}
	
	/**
	 * 获取下一个报文标识符
	 * @author w.x
	 * @date 2018年2月8日 下午4:52:57
	 */
	private synchronized int getNextMessageId() {
	    do {
	        nextMsgId++;
	        if ( nextMsgId > MAX_MSG_ID ) {
	            nextMsgId = MIN_MSG_ID;
	        }
	    } while( inUseMsgIds.containsKey( new Integer(nextMsgId) ) );
	    Integer id = new Integer(nextMsgId);
	    inUseMsgIds.put(id, id);
	    return nextMsgId;
	}
	
	/**
	 * 检查心跳活跃状况
	 * @author w.x
	 * @date 2018年2月8日 下午4:42:53
	 */
	private void checkForActivity() {
		if(core.isConnected() && this.keepAlive > 0){
			long time = System.currentTimeMillis();
			if(lastPing == 0){
				lastPing = time;
			}else{
				if((time - lastPing) >= this.keepAlive){
					lastPing = time;
					pendingFlows.addElement(pingMessage);
				}
			}
		}
	}
	
	private void setKeepAlive(long keepAlive) {
		this.keepAlive = keepAlive * 1000;
	}
	
	public void setCallback(MqttClientCallback callback) {
		this.callback = callback;
	}

}
