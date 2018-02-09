package org.livemq.internal.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;
import org.livemq.internal.stream.CountingInputStream;


/**
 * 
 * <h1>MQTT 顶级抽象报文类</h1>
 * <p>需要 MQTT 14 种报文消息去继承</p>
 */
public abstract class MqttWireMessage {

	public static final String STRING_ENCODING = "UTF-8";
	
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
	
	static String packet_names[] = { "reserved", "CONNECT", "CONNACK", "PUBLISH",
			"PUBACK", "PUBREC", "PUBREL", "PUBCOMP", "SUBSCRIBE", "SUBACK",
			"UNSUBSCRIBE", "UNSUBACK", "PINGREQ", "PINGRESP", "DISCONNECT" };
	
	/**
	 * 报文类型
	 */
	private byte type;

	/**
	 * 消息id(报文标识符,部分报文包含)
	 */
	protected int msgId;
	
	/**
	 * 消息重发标志
	 */
	protected boolean duplicate = false;
	
	/**
	 * 转码后的报文头部
	 */
	private byte[] encodedHeader = null;
	
	public MqttWireMessage(byte type) {
		this.type = type;
	}
	
	public byte[] getHeader() throws MqttException{
		if(encodedHeader == null){
			try {
				/**
				 * 1.固定报头
				 * 一个byte
				 * 7-4:控制报文类型 (所有控制报文都有)
				 * 3-0:用于指定控制报文类型的标志位 (个别类型有,没有的则标记为"保留"的标志位)
				 */
				int first = ((getType() & 0x0f) << 4) ^ (getMessageInfo() & 0x0f);
				
				/** 
				 * 2.可变报头
				 * 因为是部分报文包含,所以需要子类重写
				 */
				byte[] varHeader = getVariableHeader();
				
				// 剩余长度
				int remLen = varHeader.length + getPayload().length;
				byte[] remLenEncode = encodeMBI(remLen);
//				System.out.println(getTypeName(getType()) + "  总长度："+(1+remLenEncode.length+remLen)+",固定报头长度"+ (1+remLenEncode.length) +",剩余长度：" + remLen + ",可变报头长度：" + varHeader.length + ",有效荷载长度：" + getPayload().length);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				dos.writeByte(first);
				dos.write(remLenEncode);
				dos.write(varHeader);
				dos.flush();
				encodedHeader = baos.toByteArray();
			} catch (IOException e) {
				throw ExceptionHelper.createMqttException(e);
			}
		}
		return encodedHeader;
	}

	/**
	 * 待重写的可变报头
	 * @return
	 */
	public abstract byte[] getVariableHeader() throws MqttException;
	
	/**
	 * 待重写的固定报头3-0:用于指定控制报文类型的标志位(没有的则标记为"保留"的标志位)
	 * @return
	 */
	public abstract byte getMessageInfo() throws MqttException;

	/**
	 * <h1>获取有效荷载</h1>
	 * <p>有效荷载:部分控制报文包含</p>
	 * <p>如包含则重写即可</p>
	 * @return
	 */
	public byte[] getPayload() throws MqttException{
		return new byte[0];
	}
	
	/**
	 * 是否包含消息标识<br>
	 * 部分报文包含可重新<br>
	 * @return
	 */
	public boolean isMessageIdRequired() {
		return true;
	}
	
	/**
	 * <h1>UTF-8 编码</h1>
	 * <p>
	 * 编码后的数据结构:<br>
	 * 		byte 1			字符串长度的最高有效字节（MSB）<br>
	 * 		byte 2			字符串长度的最低有效字节（LSB）<br>
	 * 		byte n(n > 2)	如果长度大于0，这里是 UTF-8 编码的字符数据<br>
	 * </p>
	 * <p>参照 1.5.3 UTF-8 编码字符串</p>
	 * @param dos
	 * @param str
	 * @throws MqttException 
	 */
	public void encodeUTF8(DataOutputStream dos, String str) throws MqttException{
		try {
			byte[] bytes = str.getBytes();
			// 最高有效位
			byte msb = (byte) ((bytes.length >>> 8) & 0xFF);
			// 最低有效位
			byte lsb = (byte) ((bytes.length >>> 0) & 0xFF);
			dos.write(msb);
			dos.write(lsb);
			dos.write(bytes);
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
	}
	
	/**
	 * <h1>UTF-8 解码</h1>
	 * @param input
	 * @return
	 * @throws MqttException 
	 * @throws Exception
	 */
	public String decodeUTF8(DataInputStream input) throws MqttException{
		String result = null;
		int encodedLength = 0;
		try {
			/**
			 * 根据文档 1.5.3 描述：每个字符串都有一个两字节的长度作为前缀，它给出这个字符串 UTF-8 编码的字节数
			 * 也就是方法 {@link cn.fmiss.livemq.core.wire.MqttWireMessage#encodeUTF8}
			 */
			try {
				encodedLength = input.readUnsignedShort();
			} catch (Exception e) {}
			
			byte[] encodedString = new byte[encodedLength];
			
			// 从输入流中读取指定长度的字节数组到 encodedString 中,也就是真正的 UTF-8 编码的字符串
			input.readFully(encodedString);
			result = new String(encodedString, "UTF-8");
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return result;
	}
	
	/**
	 * <h1>剩余长度编码</h1>
	 * 具体算法参照文档 非规范性评注
	 * @param remLen 剩余长度
	 * @return
	 * @throws MqttException 
	 */
	public static byte[] encodeMBI(long remLen){
		int numByteLen = 0;
		long remLenBup = remLen;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		do {
			byte remLenByte = (byte) (remLenBup % 128);
			remLenBup = remLenBup / 128;
			if(remLenBup > 0){
				remLenByte |= 0x80;
			}
			bos.write(remLenByte);
			numByteLen++;
		
		// numByteLen < 4 指的是剩余长度的编码最多四个字节 0-3
		} while (remLenBup > 0 && numByteLen < 4);
		
		return bos.toByteArray();
	}
	
	/**
	 * <h1>剩余长度解码</h1>
	 * 具体算法参照文档 非规范性评注
	 * @param in 输入流
	 * @return
	 * @throws MqttException 
	 */
	public static long decodeMBI(DataInputStream in) throws MqttException{
		byte remLenByte;
		int multiplier = 1;
		long remLen = 0;
		
		try {
			do {
				// next byte from stream
				remLenByte = in.readByte();
				remLen += ((remLenByte & 0x7F) * multiplier);
				multiplier *= 0x80;
				
				if(multiplier > 0x80*0x80*0x80){
					throw ExceptionHelper.createMqttException(MqttException.CODE_MESSAGE_REMLEN_EXCEPTION);
				}
			} while ((remLenByte & 0x80) != 0);
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		
		return remLen;
	}
	
	public byte getType() {
		return type;
	}
	
	public int getMessageId() {
		return msgId;
	}

	public void setMessageId(int msgId) {
		this.msgId = msgId;
	}

	public static MqttWireMessage createWireMessage(byte[] bytes) throws MqttException  {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		return createWireMessage(bais);
	}
	
	private static MqttWireMessage createWireMessage(ByteArrayInputStream inputStream) throws MqttException  {
		MqttWireMessage result = null;
		try {
			CountingInputStream counter = new CountingInputStream(inputStream);
			DataInputStream in = new DataInputStream(counter);
			int first = in.readUnsignedByte();
			byte type = (byte) (first >> 4);
			byte info = (byte) (first &= 0x0f);
			//从流中解析的剩余长度
			long remLen = decodeMBI(in);
			//总长度
			long totalToRead = counter.getCounter() + remLen;
			//剩余长度=总长度-固定报头长度
			long remainder = totalToRead - counter.getCounter();
			byte[] data = new byte[0];
			// The remaining bytes must be the payload...
			if (remainder > 0) {
				data = new byte[(int) remainder];
				in.readFully(data, 0, data.length);
			}
			
			if (type == MqttWireMessage.MESSAGE_TYPE_CONNECT) {
				result = new MqttConnect(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_PUBLISH) {
				result = new MqttPublish(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_PUBACK) {
				result = new MqttPubAck(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_PUBCOMP) {
				result = new MqttPubComp(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_CONNACK) {
				result = new MqttConnack(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_PINGREQ) {
				result = new MqttPingReq(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_PINGRESP) {
				result = new MqttPingResp(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_SUBSCRIBE) {
				result = new MqttSubscribe(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_SUBACK) {
				result = new MqttSuback(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_UNSUBSCRIBE) {
				result = new MqttUnsubscribe(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_UNSUBACK) {
				result = new MqttUnsubAck(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_PUBREL) {
				result = new MqttPubRel(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_PUBREC) {
				result = new MqttPubRec(info, data);
			}
			else if (type == MqttWireMessage.MESSAGE_TYPE_DISCONNECT) {
				result = new MqttDisconnect(info, data);
			}
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return result;
	}

	/**
	 *  TODO 测试代码
	 * @param type2
	 * @return
	 */
	public static String getTypeName(byte type) {
		return packet_names[type];
	}

}
