package org.livemq.internal.wire;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;

/**
 * <h1>连接确认报文</h1>
 * @author w.x
 * @date 2018年2月8日 下午3:32:21
 */
public class MqttConnack extends MqttAck {

	/**
	 * 返回码的最小值
	 */
	public static final int CONNECT_MIN = 0x00;
	/**
	 * 返回码的最大值
	 */
	public static final int CONNECT_MAX = 0xFF;
	
	/**
	 * 连接已接受 <br>
	 * 连接已被服务端接受 <br>
	 */
	public static final int CONNECT_OK = 0x00;
	/**
	 * 连接已拒绝，不支持的协议版本 <br>
	 * 服务端不支持客户端请求的MQTT协议级别 <br>
	 */
	public static final int CONNECT_FAILE_PROTOCOL = 0x01;
	/**
	 * 连接已拒绝，不合格的客户端标识符 <br>
	 * 客户端标识符是正确的UTF-8编码，但服务端不允许使用 <br>
	 */
	public static final int CONNECT_FAILE_CLIENTID = 0x02;
	/**
	 * 连接已拒绝，服务端不可用 <br>
	 * 网络连接已建立，但MQTT服务不可用 <br>
	 */
	public static final int CONNECT_FAILE_SERVER = 0x03;
	/**
	 * 连接已拒绝，无效的用户名或密码 <br>
	 * 用户名或密码的数据格式无效 <br>
	 */
	public static final int CONNECT_FAILE_USERNAME_PASSWORD = 0x04;
	/**
	 * 连接已拒绝，未授权 <br>
	 * 客户端未被授权连接到此服务器 <br>
	 */
	public static final int CONNECT_FAILE_TOKEN = 0x05;
	/**
	 * 6-255 保留 <br>
	 */
	public static final int CONNECT_FAILE_RESERVED = CONNECT_MAX;
	
	private boolean cleanSession;
	/**
	 * 保留标志：服务端是否已保存客户端的session(会话状态)
	 */
	private boolean retained;
	private int returnCode;
	
	public MqttConnack(){
		super(MqttWireMessage.MESSAGE_TYPE_CONNACK);
	}
	
	public MqttConnack(byte info, byte[] variableHeader) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_CONNACK);
	}
	
	public MqttConnack(int returnCode){
		super(MqttWireMessage.MESSAGE_TYPE_CONNACK);
		validateConnectCode(returnCode);
	}
	
	/**
	 * 服务端向客户端发送时的构造器
	 * @param cleanSession 是否清除session
	 * @param retained 保留标志：服务端是否已保存客户端的session(会话状态)
	 * @param returnCode 连接返回码
	 */
	public MqttConnack(boolean cleanSession, boolean retained, int returnCode){
		super(MqttWireMessage.MESSAGE_TYPE_CONNACK);
		this.cleanSession = cleanSession;
		this.retained = retained;
		validateConnectCode(returnCode);
	}

	private void validateConnectCode(int code) {
		if(code < CONNECT_MIN || code > CONNECT_MAX){
			throw new IllegalAccessError("CONNACK 返回码错误");
		}
		this.returnCode = code;
	}

	/**
	 * 参照 3.2
	 * 
	 * byte 1
	 * 第一个字节是 连接确认标志，位 7-1 是保留位且必须设置为 0
	 * 第 0 (SP)位 是当前会话（Session Present）标志
	 * 
	 * byte 2
	 * 连接返回码
	 * @throws MqttException 
	 */
	@Override
	public byte[] getVariableHeader() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DataOutputStream dos = new DataOutputStream(baos);
			
			byte sp = 0;
			// 如果服务端发送了一个非零返回码的CONNACK 报文它必须将当前会话标志设置为0
			if(returnCode == 0){
				if(cleanSession){
					returnCode = CONNECT_OK;
				}else{
					if(retained){
						sp = 1;
					}
				}
			}
			
			dos.write(sp);
			dos.write(returnCode);
			
			dos.flush();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return baos.toByteArray();
	}
	
	@Override
	public boolean isMessageIdRequired() {
		return false;
	}

}
