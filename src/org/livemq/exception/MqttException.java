package org.livemq.exception;

/**
 * 自定义异常类
 * @author w.x
 * @date 2018年2月8日 下午1:58:13
 */
public class MqttException extends Exception {

	private static final long serialVersionUID = 1993L;
	
	/**
	 * 客户端默认的异常编码
	 */
	public static final short CODE_CLIENT_EXCEPTION = 0x00;
	
	/** 客户端连接配置为空*/
	public static final short CODE_CONFIGURATION_EXCEPTION = 0x01;
	/** 服务端连接地址错误*/
	public static final short CODE_SERVER_URI_EXCEPTION = 0x02;
	/** 文件创建失败*/
	public static final short CODE_FILE_CREATE_EXCEPTION = 0x03;
	/** 流关闭失败*/
	public static final short CODE_FLOW_CLOSE_EXCEPTION = 0x04;
	
	/** 消息报文类型错误*/
	public static final short CODE_MESSAGE_TYPE_EXCEPTION = 1001;
	/** QoS 错误*/
	public static final short CODE_MESSAGE_QOS_EXCEPTION = 1002;
	/** 主题错误*/
	public static final short CODE_MESSAGE_TOPIC_EXCEPTION = 1003;
	/** 主题为空*/
	public static final short CODE_MESSAGE_TOPIC_IS_EMPTY_EXCEPTION = 1004;
	/** 主题集和qos集长度不一致*/
	public static final short CODE_MESSAGE_TOPICS_QOS_LENGTH_EXCEPTION = 1005;
	/** 剩余长度格式错误(Malformed Remaining Length)*/
	public static final short CODE_MESSAGE_REMLEN_EXCEPTION = 1006;

	/** 客户端状态为：已连接*/
	public static final short CODE_CLIENT_STATE_CONNECTED_EXCEPTION = 2000;
	/** 客户端状态为：正在连接中*/
	public static final short CODE_CLIENT_STATE_CONNECTING_EXCEPTION = 2001;
	/** 客户端状态为：正在断开连接中*/
	public static final short CODE_CLIENT_STATE_DISCONNECTING_EXCEPTION = 2002;
	/** 客户端状态为：已断开连接*/
	public static final short CODE_CLIENT_STATE_DISCONNECTED_EXCEPTION = 2003;
	/** 客户端状态为：已关闭*/
	public static final short CODE_CLIENT_STATE_CLOSED_EXCEPTION = 2004;

	/** 服务端配置文件地址为空*/
	public static final short CODE_SERVER_CONFIG_FILE_PATH_IS_NULL_EXCEPTION = 3001;
	/** 服务端配置文件不存在*/
	public static final short CODE_SERVER_CONFIG_FILE_IS_NOT_EXISTS_EXCEPTION = 3002;
	/** 服务端配置文件解析失败*/
	public static final short CODE_SERVER_CONFIG_FILE_PARSE_EXCEPTION = 3003;
	/** 服务端消息持久化地址未配置*/
	public static final short CODE_SERVER_DATA_DIR_IS_NULL_EXCEPTION = 3004;
	/** 服务端监听端口未配置*/
	public static final short CODE_SERVER_PORT_IS_NULL_EXCEPTION = 3005;

	/** ServerSocketChannel 初始化失败*/
	public static final short CODE_SERVER_SOCKET_CHANNEL_OPEN_EXCEPTION = 4001;
	/** 服务端端口被占用*/
	public static final short CODE_SERVER_PORT_IS_NOT_FREE_EXCEPTION = 4002;
	/** 服务器启动失败，IO 异常*/
	public static final short CODE_SERVER_START_IO_EXCEPTION = 4003;
	/** 服务器关闭失败*/
	public static final short CODE_SERVER_CLOSE_EXCEPTION = 4004;
	
	

	private int code;
	private Throwable cause;
	
	public MqttException(int code){
		super();
		this.code = code;
	}

	public MqttException(Throwable cause){
		super();
		this.code = CODE_CLIENT_EXCEPTION;
		this.cause = cause;
	}
	
	public MqttException(int code, Throwable cause){
		super();
		this.code = code;
		this.cause = cause;
	}
	
	public int getCode() {
		return code;
	}
	
	public Throwable getCause() {
		return cause;
	}
	
	public String getMessage(){
		return "国际化信息暂无";
	}

	@Override
	public String toString() {
		return "MqttException [code=" + code + ", message="+ getMessage() +", cause=" + cause + "]";
	}
	
}
