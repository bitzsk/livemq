package org.livemq.server.pers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

import org.livemq.MqttMessage;
import org.livemq.exception.MqttException;

public interface DataPersistence {
	
	/**
	 * 已发消息后缀(已发送且已收到ack)
	 */
	public static final String MESSAGE_FILE_PUBLISH_EXTENSION = ".msg";
	/**
	 * 等待的消息(包含客户端未在线保留的消息和未收到ack的消息)
	 */
	public static final String MESSAGE_FILE_WAIT_EXTENSION = ".wait";
	/**
	 * 遗嘱消息后缀
	 */
	public static final String MESSAGE_FILE_WILL_EXTENSION = ".will";
	
	
	public static final FilenameFilter MESSAGE_FILE_PUBLISH_FILTER = new FilenameFilter() {
		
		public boolean accept(File dir, String name) {
			return name.endsWith(MESSAGE_FILE_PUBLISH_EXTENSION);
		}
	};
	
	public static final FilenameFilter MESSAGE_FILE_WAIT_FILTER = new FilenameFilter() {
		
		public boolean accept(File dir, String name) {
			return name.endsWith(MESSAGE_FILE_WAIT_EXTENSION);
		}
	};
	
	public static final FilenameFilter MESSAGE_FILE_WILL_FILTER = new FilenameFilter() {
		
		public boolean accept(File dir, String name) {
			return name.endsWith(MESSAGE_FILE_WILL_EXTENSION);
		}
	};
	
	
	/**
	 * 上线(接收到报文 CONNECT 时调用)
	 * @param key
	 * @param channel
	 */
	public void open(String key, SocketChannel channel, boolean clearsession);

	/**
	 * 下线(接收到报文 DISCONNECT 时调用)
	 * @param key
	 * @param channel
	 */
	public void close(SocketChannel channel) throws IOException;
	
	/**
	 * 出错(程序出错时调用)
	 * @param channel
	 */
	public void error(SocketChannel channel) throws IOException;
	
	/**
	 * 通过客户端标识符从持久化中获取客户端连接通道
	 * @param key
	 * @return
	 */
	public List<SocketChannel> getCleintChannel(String key);
	
	/**
	 * 通过客户端通道从持久化中获取客户端标识符
	 * @param channel
	 * @return
	 */
	public String getCleintKey(SocketChannel channel);
	
	/**
	 * 收到订阅时持久化调用
	 * @param key 客户端标识符
	 * @param topics 订阅主题集
	 * @param qos 订阅主题集对应的qos集
	 * @return 返回订阅主题集的最大 QoS 集
	 */
	public int[] subscribe(String key, String[] topics, int[] qoss);
	
	/**
	 * 取消订阅时调用持久化
	 * @param key 客户端标识符
	 * @param topics 取消主题集
	 * @return 返回主题集对应的报文表示符集
	 */
	public void unsubscribe(String key, String[] topics);
	
	/**
	 * 持久化消息
	 * @param topic 消息接收者
	 * @param payload 消息有效荷载
	 * @param filename 文件名
	 */
	public void persistenceMessage(String topic, MqttMessage message, String filename) throws MqttException;

	/**
	 * 从持久化中删除消息
	 * @param topic
	 * @param message
	 * @param filename
	 */
	public void unPersistenceMessage(String topic, String filename);
	
	/**
	 * 通过主题和持久化消息文件名获取内容
	 * @param topic
	 * @param filename
	 * @return
	 */
	public byte[] getMessage(String topic, String filename) throws IOException;
	
	/**
	 * 从持久化中获取该topic的订阅集
	 * @param topic 被订阅主题
	 * @return 订阅该主题的主题集
	 */
	public List<Map<String, Integer>> subscriptions(String topic);
	
	/**
	 * 从持久化中得到某客户端对于某主题的订阅信息
	 * @param topic 被订阅主题
	 * @param key 订阅该主题的客户端标识符
	 * @return
	 */
	public Map<String, Integer> subscription(String topic, String key);
	
	/**
	 * 根据传入的 FileFilter 获取某个客户端的持久化消息集
	 * @param topic
	 * @param filter
	 * @return
	 */
	public File[] getPersMessages(String topic, FilenameFilter filter);
}
