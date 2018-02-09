package org.livemq.server.pers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.livemq.MqttMessage;
import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;
import org.livemq.internal.wire.MqttSuback;
import org.livemq.log.Logger;
import org.livemq.log.LoggerFactory;
import org.livemq.server.ServerConfig;
import org.livemq.util.FileUtil;
import org.livemq.util.StringUtil;


public class DefaultPersistence implements DataPersistence {
	private static final Logger logger = LoggerFactory.getLogger(DefaultPersistence.class);
	
	/**
	 * session 持久化到内存中
	 */
	private static final Map<String, List<SocketChannel>> channels = new HashMap<String, List<SocketChannel>>();
	
	private ServerConfig config;
	
	public DefaultPersistence(ServerConfig config){
		this.config = config;
	}
	
	public void open(String key, SocketChannel channel, boolean clearsession) {
		if(clearsession){
			remove(key);
		}
		put(key, channel);
	}

	public void close(SocketChannel channel) throws IOException {
		if(channel != null && channel.isConnected() && channel.isOpen()){
			String topic = getCleintKey(channel);
			logger.warn("客户端 " + topic + " 断开连接 close()");
			
			remove(channel);
			channel.close();
		}
	}

	public void error(SocketChannel channel) throws IOException {
		close(channel);
	}
	
	/**
	 * 向持久化中加入某个客户端标识符对应的连接通道
	 * @param key
	 * @param channel
	 */
	private void put(String key, SocketChannel channel) {
		List<SocketChannel> list = channels.get(key);
		if(list == null){
			list = new ArrayList<SocketChannel>();
		}
		list.add(channel);
		channels.put(key, list);
	}

	/**
	 * 从持久化中移除某个客户端标识符对应的所有通道
	 * @param key
	 */
	private void remove(String key) {
		channels.remove(key);
	}
	
	/**
	 * 从持久化中移除某个客户端通道
	 * @param channel
	 */
	private void remove(SocketChannel channel) {
		for(String key : channels.keySet()){
			if(channels.get(key) != null && channels.get(key).contains(channel)){
				channels.get(key).remove(channel);
				break;
			}
		}
	}

	public List<SocketChannel> getCleintChannel(String key) {
		return channels.get(key);
	}

	public String getCleintKey(SocketChannel channel) {
		Set<Entry<String, List<SocketChannel>>> set = channels.entrySet();
		Iterator<Entry<String, List<SocketChannel>>> iterator = set.iterator();
		while(iterator.hasNext()){
			Entry<String, List<SocketChannel>> entry = iterator.next();
			List<SocketChannel> values = entry.getValue();
			if(values.contains(channel)){
				return entry.getKey();
			}
		}
		return null;
	}
	
	public int[] subscribe(String key, String[] topics, int[] qoss) {
		StringBuffer buffer = new StringBuffer();
		for(int i = 0;i < topics.length;i++){
			String topic = topics[i];
			if(StringUtil.isNotEmpty(topic)){
				int qos = qoss[i];
				try {
					File sub = new File(config.getDataDir() + ServerConfig.DIR_CLIENT_SESSION_SUBSCRIPTIONS, topic);
					//被订阅者
					if(!sub.exists()){
						if(!sub.mkdir()){
							logger.warn("客户端 "+ key +" 订阅主题["+ topic +"] 被订阅者 目录创建失败");
							continue;
						}
					}
					
					//订阅者
					File clt = new File(sub, key);
					if(!clt.exists()){
						if(!clt.mkdir()){
							logger.warn("客户端 "+ key +" 订阅主题["+ topic +"] 订阅者 目录创建失败");
							continue;
						}
					}
					
					//被订阅者qos
					File[] qss = clt.listFiles();
					if(qss.length > 0){
						for(File qs : qss){
							qs.delete();
						}
					}
					File qs = new File(clt, String.valueOf(qos));
					if(!qs.createNewFile()){
						logger.warn("客户端 "+ key +" 订阅主题["+ topic +"] qos 文件创建失败");
						continue;
					}
					
					buffer.append(qos + ",");
					
					logger.log("客户端 "+ key +" 订阅主题["+ topic +"]["+ qos +"] 持久化成功");
				} catch (Exception e) {
					buffer.append(MqttSuback.SUB_FAILE + ",");
					
					logger.warn("客户端 "+ key +" 订阅主题["+ topic +"]["+ qos +"] 持久化成功");
				}
			}
		}
		
		/**
		 * 组装 grantedQos
		 */
		String str = buffer.toString();
		int[] grantedQos = null;
		if(StringUtil.isNotEmpty(str)){
			str = str.substring(0, str.length()-1);
			String[] strs = str.split(",");
			grantedQos = new int[strs.length];
			for(int i = 0;i < strs.length;i++){
				grantedQos[i] = Integer.parseInt(strs[i]);
			}
			logger.log("grantedQos:" + str);
		}
		
		return grantedQos;
	}
	
	public void unsubscribe(String key, String[] topics) {
		for(int i = 0;i < topics.length;i++){
			String topic = topics[i];
			if(StringUtil.isNotEmpty(topic)){
				try {
					File sub = new File(config.getDataDir() + ServerConfig.DIR_CLIENT_SESSION_SUBSCRIPTIONS, topic);
					//被订阅者
					if(!sub.exists()){
						logger.warn("客户端 "+ key +" 取消订阅主题["+ topic +"] 失败，" + topic + " 被订阅主题不存在");
						continue;
					}
					
					//订阅者
					File clt = new File(sub, key);
					if(!clt.exists()){
						logger.warn("客户端 "+ key +" 取消订阅主题["+ topic +"] 失败，" + key + " 未订阅该主题");
						continue;
					}
					
					FileUtil.deleteFile(clt);
					
					logger.log("客户端 "+ key +" 取消订阅主题["+ topic +"] 成功");
				} catch (Exception e) {
					logger.warn("客户端 "+ key +" 取消订阅主题["+ topic +"] 失败");
				}
			}
		}
	}

	public void persistenceMessage(String topic, MqttMessage message, String filename) throws MqttException {
		File dir = new File(config.getDataDir() + ServerConfig.DIR_CLIENT_MESSAGES + "/" + topic);
		if(!dir.exists()){
			if(!dir.mkdir()){
				throw ExceptionHelper.createMqttException(MqttException.CODE_FILE_CREATE_EXCEPTION);
			}
		}
		
		File file = new File(dir, filename);
//		if(file.exists()){
//			logger.warn("消息["+ file.getAbsolutePath() +"]已存在，禁止持久化");
//			return;
//		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(message.getQos());
			fos.write(message.getPayload(), 0, message.getPayload().length);
//			String hex = StringUtil.bytes2Hex(message.getPayload());
//			fos.write(hex.getBytes(), 0, hex.getBytes().length);
			// 同步写入
			fos.getFD().sync();
			
			logger.log("客户端 ["+ topic +"] 持久化消息 ["+ file.getAbsolutePath() +"] 完成。");
		} catch (Exception e) {
			logger.warn("客户端 ["+ topic +"] 持久化消息 ["+ file.getAbsolutePath() +"] 失败。");
		} finally{
			try {
				if(fos != null) fos.close();
			} catch (Exception ex) {
				throw ExceptionHelper.createMqttException(MqttException.CODE_FLOW_CLOSE_EXCEPTION);
			}
		}
	}
	
	public void unPersistenceMessage(String topic, String filename) {
		File dir = new File(config.getDataDir() + ServerConfig.DIR_CLIENT_MESSAGES + "/" + topic);
		if(!dir.exists()){
			logger.warn("客户端 ["+ topic +"] 持久化消息目录 ["+ dir.getAbsolutePath() +"] 不存在，删除失败");
			return;
		}
		
		File file = new File(dir, filename);
		if(!file.exists()){
			logger.warn("客户端 ["+ topic +"] 持久化消息 ["+ file.getAbsolutePath() +"] 不存在，删除失败");
		}else{
			FileUtil.deleteFile(file);
		}
	}
	
	public byte[] getMessage(String topic, String filename) throws IOException {
		byte[] bytes = new byte[0];
		
		File dir = new File(config.getDataDir() + ServerConfig.DIR_CLIENT_MESSAGES + "/" + topic);
		if(!dir.exists()){
			logger.warn("客户端 ["+ topic +"] 持久化消息目录 ["+ dir.getAbsolutePath() +"] 不存在");
		}
		File file = new File(dir, filename);
		if(!file.exists()){
			logger.warn("客户端 ["+ topic +"] 持久化消息 ["+ file.getAbsolutePath() +"] 不存在");
		}else{
			bytes = FileUtil.readFileByte(file);
		}
		return bytes;
	}

	public List<Map<String, Integer>> subscriptions(String topic) {
		List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();
		Map<String, Integer> map = null;
		
		File data = new File(config.getDataDir() + ServerConfig.DIR_CLIENT_SESSION_SUBSCRIPTIONS + "/" + topic);
		if(data.exists()){
			File[] files = data.listFiles();
			for(File file : files){
				if(file.isDirectory()){
					File[] qos = file.listFiles();
					if(qos != null && qos.length == 1){
						map = new HashMap<String, Integer>();
						map.put(file.getName(), Integer.parseInt(qos[0].getName()));
						list.add(map);
					}
				}
			}
		}
		return list;
	}
	
	public Map<String, Integer> subscription(String topic, String key) {
		Map<String, Integer> map = null;
		
		File data = new File(config.getDataDir() + ServerConfig.DIR_CLIENT_SESSION_SUBSCRIPTIONS + "/" + topic);
		if(data.exists()){
			File[] files = data.listFiles();
			for(File file : files){
				if(file.isDirectory()){
					if(file.getName().equals(key)){
						File[] qos = file.listFiles();
						if(qos != null && qos.length == 1){
							map = new HashMap<String, Integer>();
							map.put(file.getName(), Integer.parseInt(qos[0].getName()));
							break;
						}
					}
				}
			}
		}
		return map;
	}

	public File[] getPersMessages(String topic, FilenameFilter filter) {
		File[] files = new File[0];
		File dir = new File(config.getDataDir() + ServerConfig.DIR_CLIENT_MESSAGES + "/" + topic);
		if(dir.exists()){
			if(filter == null){
				files = dir.listFiles();
			}else{
				files = dir.listFiles(filter);
			}
		}
		return files;
	}

}
