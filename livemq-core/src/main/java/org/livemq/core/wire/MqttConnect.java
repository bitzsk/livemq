package org.livemq.core.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.livemq.common.exception.ExceptionHelper;
import org.livemq.common.exception.MqttException;
import org.livemq.core.message.MqttMessage;

/**
 * 
 * @Title CONNECT - 连接服务器
 * @Package org.livemq.core.wire
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-18 10:59
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0301-CONNECT.md
 * 
 */
public class MqttConnect extends MqttWireMessage {
	
	/** 协议名*/
	public static final String PROTOCOL_NAME = "MQTT";
	/** 协议级别*/
	public static final byte PROTOCOL_VERSION = 4;
	
	private String clientId;
	private String username;
	private char[] password;
	private boolean clearSession;
	private int keepAliveInterval;
	private String willTopic;
	private MqttMessage willMessage;
	private String MqttName = PROTOCOL_NAME;
	private int MqttVersion = PROTOCOL_VERSION;
	
	public MqttConnect(byte info, byte[] data) throws MqttException, IOException {
		super(MqttWireMessage.MESSAGE_TYPE_CONNECT);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		
		/**
		 * 协议名，协议级别，连接标志和保持连接这四个是可变头部一定包含的
		 */
		MqttName = decodeUTF8(dis);
		MqttVersion = dis.readByte();
		byte connectFlags = dis.readByte();
		keepAliveInterval = dis.readUnsignedShort();
		
		// 解析可变报头中的连接标志
		clearSession = ((connectFlags >> 1) & 0x01) == 0x01;
		boolean hasWill = ((connectFlags >> 2) & 0x01) == 0x01;
		if(hasWill) {
			willMessage = new MqttMessage();
			int willQos = (connectFlags >> 3) & 0x03;
			boolean isRetained = ((connectFlags >> 5) & 0x01) == 0x01;
			willMessage.setQos(willQos);
			willMessage.setRetained(isRetained);
		}
		boolean hasUsername = ((connectFlags >> 7) & 0x01) == 0x01;
		boolean hasPassword = false;
		if(hasUsername) {
			hasPassword = ((connectFlags >> 6) & 0x01) == 0x01;
		}
		
		/** 客户端标识符是有效荷载中一定包含的*/
		clientId = decodeUTF8(dis);
		/** 下面这四个：遗嘱主题，遗嘱内容，用户名和密码 在有效荷载中不一定存在，需要根据可变报头中的连接标志去判断。*/
		try {
			if(hasWill) {
				willTopic = decodeUTF8(dis);
				int willPayloadLen = dis.readUnsignedShort();
				byte[] payload = new byte[willPayloadLen];
				dis.readFully(payload, 0, payload.length);
				willMessage.setPayload(payload);
			}
			if(hasUsername) {
				username = decodeUTF8(dis);
				if(hasPassword) {
					String pwd = decodeUTF8(dis);
					if(pwd != null) {
						password = pwd.toCharArray();
					}
				}
			}
		} catch (Exception e) {}
	}
	
	public MqttConnect(String clientId, String username, char[] password, 
			boolean clearSession, int keepAliveInterval,  
			String willTopic, MqttMessage willMessage) {
		super(MqttWireMessage.MESSAGE_TYPE_CONNECT);
		this.clientId = clientId;
		this.username = username;
		this.password = password;
		this.clearSession = clearSession;
		this.keepAliveInterval = keepAliveInterval;
		this.willTopic = willTopic;
		this.willMessage = willMessage;
	}

	@Override
	public byte[] getVariableHeader() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			// 1.协议名
			if(MqttVersion == PROTOCOL_VERSION) {
				encodeUTF8(dos, MqttName);
			}
			// 2.协议级别
			dos.write(MqttVersion);
			
			// 3.连接标志
			byte connectFlags = 0;
			// 3.0 保留标志位（第0位）必须为 0
			// 3.1 清除会话标志
			if(clearSession) {
				connectFlags |= 0x02;
			}
			// 3.2 3.3 3.4 3.5 遗嘱相关
			if(willMessage != null) {
				connectFlags |= 0x04;
				connectFlags |= (willMessage.getQos() << 3);
				if(willMessage.isRetained()) {
					connectFlags |= 0x20;
				}
			}
			// 3.6 3.7 密码 用户名
			if(username != null) {
				connectFlags |= 0x80;
				if(password != null) {
					connectFlags |= 0x40;
				}
			}
			dos.write(connectFlags);
			
			// 4.保持连接
			dos.writeShort(keepAliveInterval);

			dos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
	}

	@Override
	public byte getMessageInfo() {
		return (byte) 0;
	}

	@Override
	public byte[] getPayload() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			
			// 1.客户端标识符
			encodeUTF8(dos, clientId);
			
			if(willMessage != null) {
				// 2.遗嘱主题
				encodeUTF8(dos, willTopic);
				// 3.遗嘱消息
				dos.writeShort(willMessage.getPayload().length);
				dos.write(willMessage.getPayload());
			}
			
			// 4.用户名
			if(username != null) {
				encodeUTF8(dos, username);
				// 5.密码
				if(password != null) {
					encodeUTF8(dos, new String(password));
				}
			}

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
		return "MqttConnect [clientId=" + clientId + ", username=" + username + ", password="
				+ Arrays.toString(password) + ", clearSession=" + clearSession + ", keepAliveInterval="
				+ keepAliveInterval + ", willTopic=" + willTopic + ", willMessage=" + willMessage + ", MqttName="
				+ MqttName + ", MqttVersion=" + MqttVersion + "]";
	}
	
}
