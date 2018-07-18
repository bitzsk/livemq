package org.livemq.core.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.common.exception.ExceptionHelper;
import org.livemq.common.exception.MqttException;

/**
 * 
 * @Title CONNACK - 确认连接请求
 * @Package org.livemq.core.wire
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-18 13:33
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0302-CONNACK.md
 */
public class MqttConnAck extends MqttAck {

	/** 0x00 连接已接受: 连接已被服务端接受*/
	public static final int OK = 0;
	/** 0x01 连接已拒绝，不支持的协议版本: 服务端不支持客户端请求的 MQTT 协议级别*/
	public static final int FAIL_PROTOCOL_VERSION = 1;
	/** 0x02 连接已拒绝，不合格的客户端标识符: 客户端标识符是正确的 UTF-8 编码，但服务端不允许使用*/
	public static final int FAIL_CLIENTID = 2;
	/** 0x03 连接已拒绝，服务端不可用: 网络连接已建立，但 MQTT 服务不可用*/
	public static final int FAIL_SERVER = 3;
	/** 0x04 连接已拒绝，无效的用户名或密码: 用户名或密码的数据格式无效*/
	public static final int FAIL_USERNAME_PASSWORD = 4;
	/** 0x05 连接已拒绝，未授权: 客户端未被授权连接到此服务器*/
	public static final int FAIL_TOKEN = 5;
	/** 保留: 最小值*/
	public static final int RESERVED_MIN = 6;
	/** 保留: 最大值*/
	public static final int RESERVED_MAX = 255;
	
	
	private int sessionPresent;
	private int returnCode;
	
	public MqttConnAck(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_CONNACK);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		// & 0x01: 位 7-1 是保留位且 必须 设置为 0
		sessionPresent = dis.readUnsignedByte() & 0x01;
		returnCode = dis.readUnsignedByte();
	}

	public MqttConnAck(int sessionPresent, int returnCode) {
		super(MqttWireMessage.MESSAGE_TYPE_CONNACK);
		this.sessionPresent = sessionPresent;
		this.returnCode = returnCode;
	}

	@Override
	public byte[] getVariableHeader() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			
			// 1.连接确认标志
			// & 0x01: 位 7-1 是保留位且 必须 设置为 0
			byte connectConfirm = 0;
			connectConfirm |= sessionPresent;
			connectConfirm &= 0x01;
			dos.write(connectConfirm);
			
			// 2.连接返回码
			dos.write(returnCode);
			
			dos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
	}
	
	@Override
	public boolean isMessageIdRequired() {
		return false;
	}

	@Override
	public String toString() {
		return "MqttConnAck [sessionPresent=" + sessionPresent + ", returnCode=" + returnCode + "]";
	}

}
