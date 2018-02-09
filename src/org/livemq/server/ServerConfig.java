package org.livemq.server;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;
import org.livemq.log.Logger;
import org.livemq.log.LoggerFactory;

/**
 * 服务端配置文件
 * @author w.x
 * @date 2018年2月8日 下午5:05:40
 */
public class ServerConfig {

	private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);
	
	/**
	 * 客户端session持久化根目录(未使用)
	 */
	public static String DIR_CLIENT_SESSIONS = "client_sessions";
	/**
	 * 客户端对应的订阅主题集持久化根目录(客户端订阅)
	 */
	public static String DIR_CLIENT_SESSION_SUBSCRIPTIONS = "client_session_subscriptions";
	/**
	 * 客户端消息持久化根目录
	 */
	public static String DIR_CLIENT_MESSAGES = "client_messages";
	
	private InetSocketAddress clientPortAddress;
	
	private String dataDir;

	/**
	 * 加载配置文件
	 * @param path
	 * @throws MqttException 
	 */
	public void parse(String path) throws MqttException{
		File configFile = new File(path);
		logger.log("Reading configuration from " + configFile);
		
		FileInputStream fis = null;
		try {
			if(!configFile.exists()){
				throw ExceptionHelper.createMqttException(MqttException.CODE_SERVER_CONFIG_FILE_IS_NOT_EXISTS_EXCEPTION);
			}
			
			Properties cfg = new Properties();
			fis = new FileInputStream(configFile);
			
			cfg.load(fis);
			
			parseProperties(cfg);
		} catch (Exception e) {
			throw ExceptionHelper.createMqttException(e);
		} finally{
			try {
				if(fis != null){
					fis.close();
				}
			} catch (Exception ex) {
				throw ExceptionHelper.createMqttException(ex);
			}
		}
	}
	
	/**
	 * 解析配置文件
	 * @param cfg
	 * @throws Exception
	 */
	private void parseProperties(Properties cfg) throws Exception {
		Set<Entry<Object, Object>> set = cfg.entrySet();
		Iterator<Entry<Object, Object>> iterator = set.iterator();

		int clientPort = 0;
		String clientPortAddress = null;
		
		while(iterator.hasNext()){
			Entry<Object, Object> entry = iterator.next();
			String key = entry.getKey().toString().trim();
			String value = entry.getValue().toString().trim();
			
			if(key.equals("dataDir")){
				dataDir = value;
			}
			else if(key.equals("clientPort")){
				clientPort = Integer.parseInt(value);
			}
			else if(key.equals("clientPortAddress")) {
                clientPortAddress = value.trim();
            }
		}
		
		if(dataDir == null){
			throw ExceptionHelper.createMqttException(MqttException.CODE_SERVER_DATA_DIR_IS_NULL_EXCEPTION);
		}else{
			if(dataDir.lastIndexOf("/") != (dataDir.length() - 1)){
				dataDir += "/";
			}
		}
		
		mkdirs(dataDir);
		
		if(clientPort == 0){
			throw ExceptionHelper.createMqttException(MqttException.CODE_SERVER_PORT_IS_NULL_EXCEPTION);
		}
		if(clientPortAddress != null){
			this.clientPortAddress = new InetSocketAddress(InetAddress.getByName(clientPortAddress), clientPort);
		}else{
			this.clientPortAddress = new InetSocketAddress(clientPort);
		}
	}

	/**
	 * 创建持久化数据所需的目录
	 * @param data
	 * @throws MqttException 
	 */
	private void mkdirs(String dir) throws MqttException {
		File data = new File(dir);
		if(!data.exists()){
			throw ExceptionHelper.createMqttException(MqttException.CODE_SERVER_CONFIG_FILE_IS_NOT_EXISTS_EXCEPTION);
		}
		
		/**
		 * 创建客户端持久化目录
		 */
		File sessions = new File(data, DIR_CLIENT_SESSIONS);
		if(!sessions.exists()){
			if (!sessions.mkdir()) {
				throw ExceptionHelper.createMqttException(MqttException.CODE_FILE_CREATE_EXCEPTION);
			}
		}

		/**
		 * 创建客户端订阅持久化目录
		 */
		File sessionSubs = new File(data, DIR_CLIENT_SESSION_SUBSCRIPTIONS);
		if(!sessionSubs.exists()){
			if (!sessionSubs.mkdir()) {
				throw ExceptionHelper.createMqttException(MqttException.CODE_FILE_CREATE_EXCEPTION);
			}
		}
		
		/**
		 * 创建客户端消息持久化目录
		 */
		File willMessage = new File(data, DIR_CLIENT_MESSAGES);
		if(!willMessage.exists()){
			if (!willMessage.mkdir()) {
				throw ExceptionHelper.createMqttException(MqttException.CODE_FILE_CREATE_EXCEPTION);
			}
		}
	}

	public InetSocketAddress getClientPortAddress() {
		return clientPortAddress;
	}
	
	public String getDataDir() {
		return dataDir;
	}
}
