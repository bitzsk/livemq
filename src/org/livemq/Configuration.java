package org.livemq;

import java.net.URI;
import java.net.URISyntaxException;

import javax.net.SocketFactory;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;

/**
 * 客户端连接配置参数
 * @author w.x
 * @date 2018年2月8日 下午1:44:38
 */
public class Configuration {

	/** 默认清除会话*/
	public static final boolean CLEAN_SESSION_DEFAULT = true;
	/** 默认连接超时时间*/
	public static final int CONNECTION_TIMEOUT = 30;
	/** 默认心跳检测间隔(单位:秒)*/
	public static final int KEEP_ALIVE_INTERVAL = 60;
	
	/**
	 * 连接服务端的网络类型
	 */
	public static final int URI_TYPE_TCP = 0;
	public static final int URI_TYPE_SSL = 1;
	
	/** 客户端标识符*/
	private String clientId;
	/** 服务端连接地址*/
	private String serverURI;
	/** 套接字工厂*/
	private SocketFactory socketFactory;
	
	/** 清除会话*/
	private boolean cleanSession = CLEAN_SESSION_DEFAULT;
	/** 连接超时时间*/
	private int connectionTimeout = CONNECTION_TIMEOUT;
	/** 心跳检测间隔*/
	private int keepAliveInterval = KEEP_ALIVE_INTERVAL;
	/** 用户名*/
	private String username;
	/** 密码*/
	private char[] password;
	/** 遗嘱主题*/
	private String willTopic;
	/** 遗嘱消息*/
	private MqttMessage willMessage;
	
	
	public static final char[] CHAR_NUMBER = new char[] {
			'0', '1', '2', '3', '4', 
			'5', '6', '7', '8', '9'};

	public static final char[] CHAR_LETTER_UPPER = new char[] {
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 
			'O', 'P', 'Q', 'R', 'S', 'T', 
			'U', 'V', 'W', 'X', 'Y', 'Z'};
	
	public static final char[] CHAR_LETTER_LOWER = new char[] {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 
			'o', 'p', 'q', 'r', 's', 't', 
			'u', 'v', 'w', 'x', 'y', 'z'};

	/**
	 * 校验客户端ID
	 * 
	 * @param clientId
	 */
	public static void validateClientId(String clientId) {
		if (clientId == null || "".equals(clientId.trim()) || clientId.trim().length() == 0) {
			throw new IllegalArgumentException("客户端ID不能为空");
		} else {
			char[] ids = clientId.toCharArray();
			for(char id : ids){
				if(!isContains(CHAR_NUMBER, id) 
						&& !isContains(CHAR_LETTER_UPPER, id) 
						&& !isContains(CHAR_LETTER_LOWER, id)){
					throw new IllegalArgumentException("客户端ID包含错误格式的数据:" + id);
				}
			}
		}
	}
	
	public static boolean isContains(char[] src, char t){
		for(char c : src){
			if(c == t) return true;
		}
		return false;
	}

	public static int validateURI(String serverURI) throws MqttException {
		URI uri = null;
		try {
			uri = new URI(serverURI);
		} catch (URISyntaxException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		if("tcp".equals(uri.getScheme())){
			return URI_TYPE_TCP;
		}
		else if("ssl".equals(uri.getScheme())){
			return URI_TYPE_SSL;
		}
		throw ExceptionHelper.createMqttException(MqttException.CODE_SERVER_URI_EXCEPTION);
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getServerURI() {
		return serverURI;
	}

	public void setServerURI(String serverURI) {
		this.serverURI = serverURI;
	}

	public SocketFactory getSocketFactory() {
		return socketFactory;
	}

	public void setSocketFactory(SocketFactory socketFactory) {
		this.socketFactory = socketFactory;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public String getWillTopic() {
		return willTopic;
	}

	public void setWillTopic(String willTopic) {
		this.willTopic = willTopic;
	}

	public MqttMessage getWillMessage() {
		return willMessage;
	}

	public void setWillMessage(MqttMessage willMessage) {
		this.willMessage = willMessage;
	}
	
}
