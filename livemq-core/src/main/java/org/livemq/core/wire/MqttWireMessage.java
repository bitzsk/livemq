package org.livemq.core.wire;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.common.exception.ExceptionHelper;
import org.livemq.common.exception.MqttException;

/**
 * <h1>控制报文顶级父类</h1>
 * 
 * <h2>控制报文的结构</h2>
 * <ul>
 * 	<li>固定报头，所有控制报文都包含</li>
 * 	<li>可变报头，部分控制报文包含</li>
 * 	<li>有效荷载，部分控制报文包含</li>
 * </ul>
 *
 * <h2>1 固定报头</h2>
 * <ul>
 * 	<li>byte 1 7-4 MQTT 控制报文的类型 3-0 用于指定控制报文类型的标志位</li>
 * 	<li>byte 2... <strong>剩余长度</strong></li>
 * </ul>
 * 
 * <h2>1.1 剩余长度</h2>
 * <p>
 * 位置：从第 2 个字节开始。 <br>
 * 剩余长度（Remaining Length）表示当前报文剩余部分的字节数，包括可变报头和负载的数据。剩余长度不包括用于编码剩余长度字段本身的字节数。<br>
 * 剩余长度字段使用一个变长度编码方案（具体看编码方案）
 * </p>
 * 
 * <h2>2 可变报头</h2>
 * <p>
 * 某些 MQTT 控制报文包含一个可变报头部分。它在固定报头和负载之间。可变报头的内容根据报文类型的不同而不同。
 * </p>
 * 
 * <h2>2.1 报文标识符</h2>
 * <p>
 * 可变报头的报文标识符（Packet Identifier）字段存在于在多个类型的报文里。<br>
 * （如：PUBLISH [QoS > 0时]、PUBACK、PUBREC、PUBREL、PUBCOMP、SUBSCRIBE、SUBACK、UNSUBSCRIBE、UNSUBACK）<br><br>
 * 
 * SUBSCRIBE、UNSUBSCRIBE 和 PUBLISH（QoS 大于 0）控制报文必须包含一个非零的 16 位报文标识符（Packet Identifier）。<br>
 * 客户端每次发送一个新的这些类型的报文时都 <strong>必须</strong> 分配一个当前未使用的报文表示符。<br>
 * 如果客户端要重发这个特殊的控制报文，在随后重发那个报文时，它 <strong>必须</strong> 使用相同的标识符。<br>
 * 当客户端处理完这个报文对应的确认后，这个报文标识符就释放可重用。<br>
 * QoS 1 的 PUBLISH 对应的是 PUBACK，QoS 2 的 PUBLISH 对应的 PUBCOMP，与 SUBSCRIBE 或 UNSUBSCRIBE 对应的分别是 SUBACK 或 UNSUBACK。<br>
 * 发送一个 QoS 0 的 PUBLISH 报文时，相同的条件也适用于服务端。<br><br>
 * 
 * QoS 设置为 0 的 PUBLISH 报文 <strong>不能</strong> 包含报文标识符。<br><br>
 * 
 * PUBACK，PUBREC，PUBREL 报文 <strong>必须</strong> 包含与最初发送的 PUBLISH 报文相同的报文标识符。<br>
 * 类似的，SUBACK 和 UNSUBACK <strong>必须</strong> 包含在对应的 SUBSCRIBE 和 UNSUBSCRIBE 报文中使用的报文标识符。<br><br>
 * 
 * 客户端和服务端彼此独立地分配报文标识符。因此，客户端服务端组合使用相同的报文标识符可以实现并发的消息交换。<br>
 * </p>
 * 
 * <h2>3 有效荷载</h2>
 * <p>
 * 某些 MQTT 控制报文在报文的最后部分包含一个有效荷载。<br>
 * ------------------------------------------------------------------<br>
 * | 控制报文 		| 有效荷载 											|<br>
 * | CONNECT 	| 需要（因为需要携带连接参数。如：客户端标识符，遗嘱主题等） 		|<br>
 * | CONNACK	| 不需要												|<br>
 * | PUBLISH	| 可选（有内容则存在有效荷载）								|<br>
 * | PUBACK		| 不需要												|<br>
 * | PUBREC		| 不需要												|<br>
 * | PUBREL		| 不需要												|<br>
 * | PUBCOMP 	| 不需要												|<br>
 * | SUBSCRIBE	| 需要（携带订阅的主题集和 QoS集）							|<br>
 * | SUBACK 	| 需要												|<br>
 * | UNSUBSCRIBE| 需要												|<br>
 * | UNSUBACK 	| 不需要												|<br>
 * | PINGREQ 	| 不需要												|<br>
 * | PINGRESP 	| 不需要												|<br>
 * | DISCONNECT | 不需要												|<br>
 * ------------------------------------------------------------------<br>
 * </p>
 * 
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-04 11:00
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
	
	/**
	 * 0, 15: 保留
	 */
	private static final String PACKET_NAMES[] = { "Reserved", "CONNECT", "CONNACK", "PUBLISH", "PUBACK", "PUBREC", "PUBREL",
			"PUBCOMP", "SUBSCRIBE", "SUBACK", "UNSUBSCRIBE", "UNSUBACK", "PINGREQ", "PINGRESP", "DISCONNECT",
			"Reserved" };

	// The type of the message
	private byte type;
	// The MQTT message ID
	protected int msgId;
	
	/**
	 * 消息重发标志
	 */
	protected boolean duplicate = false;
	
	/**
	 * 报头(包含固定报头和可变报头)
	 */
	private byte[] encodeHeader = null;
	
	public MqttWireMessage(byte type) {
		this.type = type;
		this.msgId = 0;
	}
	
	/**
	 * <h1>报头</h1>
	 * 
	 * <p>包括 <strong>固定报头</strong> 和 <strong>可变报头</strong> </p>
	 * 
	 * @return
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
	 * <h1>可变报头</h1>
	 * 
	 * <p>部分报文包含</p>
	 * 
	 * @return
	 */
	public abstract byte[] getVariableHeader() throws MqttException;

	/**
	 * <h1>固定报头标志位</h1>
	 * @return
	 */
	public abstract byte getMessageInfo();
	
	/**
	 * 返回有效荷载
	 * @return
	 */
	public byte[] getPayload() throws MqttException {
		return new byte[0];
	};
	
	/**
	 * 报文是否必须有标识符
	 * @return
	 */
	public boolean isMessageIdRequired() {
		return true;
	}
	
	public byte getType() {
		return type;
	}
	
	/**
	 * 剩余长度变长度编码
	 * @param remLen 剩余长度
	 * @return 返回变长度编码后的剩余长度字节数组
	 */
	public static byte[] encodeMBI(long remLen) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		do {
			byte encodeByte = (byte) (remLen % 128);
			remLen /= 128;
			if(remLen > 0) encodeByte |= 128;
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
				value += (encodeByte & 127) * multiplier;
				multiplier *= 128;
				if(multiplier > 128*128*128) {
					throw ExceptionHelper.createMqttException("Malformed Remaining Length");
				}
			} while ((encodeByte & 128) != 0);
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
	 * 校验 QoS
	 * @param qos
	 * @throws MqttException 
	 */
	public static void validateQos(int qos) throws MqttException {
		if(qos < 0 || qos > 2) throw ExceptionHelper.createMqttException("QoS " + qos + " 不正确");
	}
	
	/**
	 * 构建报文
	 * @param bytes
	 * @return
	 * @throws MqttException 
	 */
	public static MqttWireMessage createWireMessage(DataInputStream dis) throws MqttException {
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
			case MqttWireMessage.MESSAGE_TYPE_SUBSCRIBE:
				message = new MqttSubscribe(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_PINGREQ:
				message = new MqttPingReq(info, data);
				break;
			case MqttWireMessage.MESSAGE_TYPE_PINGRESP:
				message = new MqttPingResp(info, data);
				break;
			}
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		
		return message;
	}
	
	@Override
	public String toString() {
		return PACKET_NAMES[type];
	}
	
}
