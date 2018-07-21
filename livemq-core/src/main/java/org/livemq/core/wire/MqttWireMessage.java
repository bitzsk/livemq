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
 * @Title MqttWireMessage
 * @Package org.livemq.core.wire
 * @Description 控制报文顶级父类
 * @author xinxisimple@163.com
 * @date 2018-07-18 11:01
 * @version 1.0.0
 */
public abstract class MqttWireMessage {
	public static final byte MESSAGE_TYPE_CONNECT = 1;
	public static final byte MESSAGE_TYPE_CONNACK = 2;
	public static final byte MESSAGE_TYPE_PUBLISH = 3;
	public static final byte MESSAGE_TYPE_PUBACK = 4;
	public static final byte MESSAGE_TYPE_PUBREC = 5;
	public static final byte MESSAGE_TYPE_PUBREL = 6;
	public static final byte MESSAGE_TYPE_PUBCOMP = 7;
	public static final byte MESSAGE_TYPE_SUBSCRIBE = 8;
	public static final byte MESSAGE_TYPE_SUBACK = 9;
	public static final byte MESSAGE_TYPE_UNSUBSCRIBE = 10;
	public static final byte MESSAGE_TYPE_UNSUBACK = 11;
	public static final byte MESSAGE_TYPE_PINGREQ = 12;
	public static final byte MESSAGE_TYPE_PINGRESP = 13;
	public static final byte MESSAGE_TYPE_DISCONNECT = 14;

	protected static final String CHARSET_UTF8 = "UTF-8";
	
	/** 0, 15: 保留*/
	static final String PACKET_NAMES[] = { "Reserved", "CONNECT", "CONNACK", "PUBLISH", "PUBACK", "PUBREC", "PUBREL",
			"PUBCOMP", "SUBSCRIBE", "SUBACK", "UNSUBSCRIBE", "UNSUBACK", "PINGREQ", "PINGRESP", "DISCONNECT",
			"Reserved" };

	// The type of the message
	private byte type;
	// The MQTT message ID
	protected int msgId;
	
	protected boolean duplicate = false;
	
	/** 报头 (包含固定报头和可变报头)*/
	private byte[] encodeHeader = null;
	
	public MqttWireMessage(byte type) {
		this.type = type;
		this.msgId = 0;
	}
	
	/**
	 * <h1>报头</h1>
	 * 包括 <strong>固定报头</strong> 和 <strong>可变报头</strong>
	 * @return encodeHeader
	 * @throws MqttException
	 */
	public byte[] getHeader() throws MqttException {
		if(encodeHeader == null) {
			// 固定报头
			// 1 个 byte
			// 7-4位: 报文类型(所有报文都有)
			// 3-0位: 用于指定报文类型的标志位(部分报文有，没有的则标记为 "保留(0)" 的标记位)
			int first = ((getType() & 0x0f) << 4) ^ (getMessageInfo() & 0x0f);
			
			// 可变报头
			byte[] varHeader = getVariableHeader();
			
			// 剩余长度
			// 注:[2.3.3] 
			// 剩余长度(Remaining Length)表示当前报文剩余部分的字节数，包括可变报头和负载的数据。
			// 剩余长度不包括用于编码剩余长度字段本身的字节数
			int remLen = varHeader.length + getPayload().length;
			// 剩余长度字段使用一个变长度编码方案
			byte[] remLenEncode = encodeMBI(remLen);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			try {
				dos.write(first);
				dos.write(remLenEncode);
				dos.write(varHeader);
				dos.flush();
				encodeHeader = baos.toByteArray();
			} catch (IOException e) {
				throw ExceptionHelper.createMqttException(e);
			}
		}
		return encodeHeader;
	}
	
	/**
	 * 返回报文的总长度 （头部 and 有效荷载）
	 * @return
	 * @throws MqttException
	 */
	public int length() throws MqttException {
		return getHeader().length + getPayload().length;
	}
	
	/**
	 * <h1>可变报头</h1>
	 * @return variableHeader
	 */
	public abstract byte[] getVariableHeader() throws MqttException;

	/**
	 * <h1>固定报头标志位</h1>
	 * @return messageInfo
	 */
	public abstract byte getMessageInfo();
	
	/**
	 * <h1>有效荷载</h1>
	 * @return payload
	 */
	public byte[] getPayload() throws MqttException {
		return new byte[0];
	};
	
	/**
	 * 报文是否必须有标识符
	 * @return true
	 */
	public boolean isMessageIdRequired() {
		return true;
	}
	
	/**
	 * 返回控制报文类型
	 * @return
	 */
	public byte getType() {
		return type;
	}
	
	/**
	 * 返回报文标识符
	 * @return msgId
	 */
	public int getMessageId() {
		return msgId;
	}
	
	/**
	 * 设置该消息是否为重发消息
	 * @param duplicate
	 */
	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}
	
	/**
	 * 报文标识符
	 * @return msgId
	 * @throws MqttException
	 */
	protected byte[] encodeMessageId() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeShort(msgId);
			
			dos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
	}
	
	/**
	 * 剩余长度变长度编码
	 * @param remLen 剩余长度
	 * @return 返回变长度编码后的剩余长度字节数组
	 */
	public static byte[] encodeMBI(long remLen) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		do {
			byte encodeByte = (byte) (remLen % 0x80);
			remLen /= 0x80;
			if(remLen > 0) encodeByte |= 0x80;
			baos.write(encodeByte);
		} while (remLen > 0);
		return baos.toByteArray();
	}
	
	/**
	 * 剩余长度变长度解码
	 * @param bytes 编码后的剩余长度字节数组
	 * @return 返回变长度解码后的剩余长度
	 * @throws MqttException 
	 */
	public static long decodeMBI(DataInputStream in) throws MqttException {
		byte encodeByte = 0;
		int multiplier = 1;
		long value = 0L;
		
		try {
			do {
				encodeByte = in.readByte();
				value += (encodeByte & 0x7f) * multiplier;
				multiplier *= 0x80;
				if(multiplier > 0x80*0x80*0x80) {
					throw ExceptionHelper.createMqttException("Malformed Remaining Length");
				}
			} while ((encodeByte & 0x80) != 0);
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return value;
	}
	
	/**
	 * <h1>UTF-8 编码</h1>
	 * 
	 * <p>
	 * 每一个字符串都有一个两字节的长度字段作为前缀，它给出了这个字符串 UTF-8 编码的字节数，
	 * 它们在 <i>1.5.3</i> 图例中描述。因此可以传送的 UTF-8 编码的字符串大小有一个限制，不能超过 65535 字节。
	 * 
	 * <br><br>除非另有说明，所有的 UTF-8 编码字符串的长度都必须在 0 到 65535 字节这个范围内。
	 * </p>
	 * 
	 * @param str
	 * @return
	 * @throws MqttException 
	 */
	public static void encodeUTF8(DataOutputStream dos, String str) throws MqttException {
		try {
			byte[] bytes = str.getBytes(CHARSET_UTF8);
			byte msb = (byte) ((bytes.length >>> 8) & 0xFF);
			byte lsb = (byte) ((bytes.length >>> 0) & 0xFF);
			dos.write(msb);
			dos.write(lsb);
			dos.write(bytes);
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
	}
	
	/**
	 * UTF-8 解码
	 * @param bytes
	 * @return
	 * @throws MqttException
	 */
	public static String decodeUTF8(DataInputStream dis) throws MqttException {
		String content = null;
		try {
			// 根据文档描述：每个字符串都有一个两字节的长度作为前缀，它给出了这个字符串 UTF8 编码的字节数
			int len = dis.readUnsignedShort();
			
			byte[] str = new byte[len];
			dis.readFully(str);
			content = new String(str, CHARSET_UTF8);
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return content;
	}
	
	/**
	 * 构建报文
	 * @param bytes
	 * @return
	 * @throws MqttException 
	 */
	public static MqttWireMessage createWireMessage(byte[] bytes) throws MqttException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);
		return createWireMessage(dis);
	}
	
	private static MqttWireMessage createWireMessage(DataInputStream dis) throws MqttException {
		MqttWireMessage message = null;
		
		try {
			int first = dis.readUnsignedByte();
			byte type = (byte) (first >> 4);
			byte info = (byte) (first &= 0x0f);
			// 从流中解析的剩余长度
			long remLen = decodeMBI(dis);
			
			byte[] data = new byte[0];
			if(remLen > 0) {
				data = new byte[(int) remLen];
				dis.readFully(data);
			}
			
			switch (type) {
			case MqttWireMessage.MESSAGE_TYPE_CONNECT:
				message = new MqttConnect(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_CONNACK:
				message = new MqttConnAck(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_PUBLISH:
				message = new MqttPublish(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_PUBACK:
				message = new MqttPubAck(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_PUBREC:
				message = new MqttPubRec(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_PUBREL:
				message = new MqttPubRel(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_PUBCOMP:
				message = new MqttPubComp(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_SUBSCRIBE:
				message = new MqttSubscribe(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_SUBACK:
				message = new MqttSubAck(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_UNSUBSCRIBE:
				message = new MqttUnsubscribe(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_UNSUBACK:
				message = new MqttUnsubAck(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_PINGREQ:
				message = new MqttPingReq(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_PINGRESP:
				message = new MqttPingResp(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_DISCONNECT:
				message = new MqttDisconnect(info, data);
				break;
			}
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return message;
	}

	@Override
	public String toString() {
		return PACKET_NAMES[getType()] + " msgId: " + msgId;
	}
	
}
