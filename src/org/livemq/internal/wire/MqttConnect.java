package org.livemq.internal.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.MqttMessage;
import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;

/**
 * <h1>MQTT 客户端连接报文</h1>
 * <p></p>
 */
public class MqttConnect extends MqttWireMessage {

	/**
	 * 客户端ID
	 */
	private String clientId;
	/**
	 * 是否清除session
	 */
	private boolean cleanSession;
	/**
	 * 心跳检测频率(秒)
	 */
	private int keepAliveInterval;
	/**
	 * 遗嘱消息
	 */
	private MqttMessage willMessage;
	/**
	 * 遗嘱主题
	 */
	private String willTopic;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 密码
	 */
	private char[] password;
	
	/**
	 * <p>默认连接协议名</p>
	 * 如果协议名不正确服务端<strong> 可以 </strong>断开客户端的连接，也<strong> 可以 </strong>按照某些规范继续处理 CONNECT 报文。 <br>
	 * 对于后一种情况，按照 MQTT3.1.1 规范，服务器<strong> 不能 </strong>继续处理 CONNECT 报文。 <br>
	 */
	public static final String PROTOCOL_NAME_DEFAULT = "MQIsdp";
	
	/**
	 * <p>默认连接协议级别</p>
	 * 对于3.1.1版协议,协议级别字段值是4(0x04) <br>
	 * 如果发现不支持的协议级别，服务端<strong> 必须 </strong>给发送一个返回码为0x01(不支持的协议级别)的 CONNACK 报文响应CONNECT 报文，然后断开客户端连接 <br>
	 */
	public static final int PROTOCOL_LEVEL_DEFAULT = 4;
	
	/**
	 * 收到的报文头部数据
	 */
	private String protocolName = null;
	private int protocolLevel;
	private byte connectFlags;
	
	/**
	 * 收到的消息解析构造器
	 * @param info the message info byte
	 * @param data the variable header and payload bytes
	 * @throws MqttException 
	 * @throws Exception
	 */
	public MqttConnect(byte info, byte[] data) throws MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_CONNECT);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		try {
			//可变报头解析开始
			
			// 协议名称
			protocolName = decodeUTF8(dis);
			// 协议版本
			protocolLevel = dis.readByte();
			// 连接标志
			connectFlags = dis.readByte();
			// 保持连接时间
			keepAliveInterval = dis.readUnsignedShort();
			
			//有效荷载解析开始
			
			// 客户端标识符
			clientId = decodeUTF8(dis);
			
			byte temp = connectFlags;
			
			// 获取清理会话标志
			temp = (byte) (temp >> 1);
			byte clearSessionFlag = (byte)(temp & 1);
			// 清理会话
			if(clearSessionFlag == 0x01){
				cleanSession = true;
			}else{
				cleanSession = false;
			}
			
			// 获取遗嘱标志
			temp = (byte) (temp >> 1);
			byte willFlag = (byte)(temp & 1);
			if(willFlag == 0x01){
				temp = (byte) (temp >> 1);
				byte willQoSFlag = (byte)(temp & 2);
				
				temp = (byte) (temp >> 2);
				byte willRetainFlag = (byte)(temp & 1);
				
				// 遗嘱主题
				willTopic = decodeUTF8(dis);
				//读取willMessage有效荷载的长度
				byte[] payloadLenArray = new byte[2];
				dis.readFully(payloadLenArray, 0, payloadLenArray.length);
				
				int payloadLen = payloadLenArray[1];
				
				if(payloadLen > 0){
					//读取willMessage 的有效荷载
					byte[] payload = new byte[payloadLen];
					dis.readFully(payload, 0, payload.length);
					
					//构建遗嘱消息
					willMessage = new MqttMessage();
					willMessage.setPayload(payload);
					willMessage.setQos(willQoSFlag);
					willMessage.setRetained(willRetainFlag == 0x01 ? true : false);
				}
			}
			
			byte tmp = 0;
			if(willFlag == 0x01){
				// 获取账号标志
				tmp = (byte) (temp >> 2);
				// 获取密码标志
				temp = (byte) (temp >> 1);
			}else{
				// 获取账号标志
				tmp = (byte) (temp >> 5);
				// 获取密码标志
				temp = (byte) (temp >> 4);
			}
			
			if(tmp != 0){
				byte usernameFlag = (byte)(tmp & 1);
				if(usernameFlag == 0x01){
					//用户名
					userName = decodeUTF8(dis);
				}
				
				byte passwordFlag = (byte)(temp & 1);
				if(passwordFlag == 0x01){
					//密码
					String pwd = decodeUTF8(dis);
					if(pwd != null) password = pwd.toCharArray();
				}
			}
			
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		} finally{
			try {
				if(dis != null){
					dis.close();
				}
			} catch (Exception ex) {}
		}
	}
	
	/**
	 * 发送的消息构造器
	 * @param clientId
	 * @param cleanSession
	 * @param keepAliveInterval
	 * @param willMessage
	 * @param willTopic
	 * @param userName
	 * @param password
	 */
	public MqttConnect(String clientId, boolean cleanSession, int keepAliveInterval, MqttMessage willMessage, String willTopic, String userName, char[] password) {
		super(MqttWireMessage.MESSAGE_TYPE_CONNECT);
		this.clientId = clientId;
		this.cleanSession = cleanSession;
		this.keepAliveInterval = keepAliveInterval;
		this.willMessage = willMessage;
		this.willTopic = willTopic;
		this.userName = userName;
		this.password = password;
	}

	/**
	 * CONNECT 固定报头 报文标志位保留
	 */
	public byte getMessageInfo() {
		return 0;
	}

	/**
	 * CONNECT 可变报头按下列次序包含四个字段：协议名(Protocol Name),协议级别(Protocol Level),连接标志(Connect Flags)和保持连接(Keep Alive)
	 * @throws MqttException 
	 */
	@Override
	public byte[] getVariableHeader() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DataOutputStream dos = new DataOutputStream(baos);
			//1.协议名
			encodeUTF8(dos,PROTOCOL_NAME_DEFAULT);
			//2.协议级别
			dos.write(PROTOCOL_LEVEL_DEFAULT);
			//3.连接标志
			
			/**
			 * 	|--------|------------------|-----------------|--------------|------------|-----------|---------------|------------|
			 * 	|	Bit	 |	7				|	6			  |	5			 |	4   |  3  |	2		  |	1			  |	0		   |
			 * 	|--------|------------------|-----------------|--------------|------------|-----------|---------------|------------|
			 * 	|		 |	User Name Flag	|	Password Flag |	Will Retain	 |	Will Qos  |	Will Flag |	Clean Session |	Reserved   |
			 * 	|--------|------------------|-----------------|--------------|------------|-----------|---------------|------------|
			 * 	|	byte8|	X				|	X			  |	X			 |	X	|  X  |	X		  |	X			  |	0		   |
			 * 	|--------|------------------|-----------------|--------------|------------|-----------|---------------|------------|
			 */
			byte connectFlags = 0;
			/**
			 * 7:User Name Flag
			 * 6:Password Flag
			 * 5:Will Retain
			 * 4-3:Will Qos
			 * 2:Will Flag
			 * 1:Clean Session
			 * 0:保留(服务器必须验证 CONNECT 控制报文的保留标志位(第 0 位)是否位0，如果不为0必须断开客户端连接)
			 */
			
			//如果true,则cleaseSession位则为1,00000010转换为十六进制为2
			if(cleanSession){
				connectFlags |= 0x02;
			}
			//如果遗嘱消息存在,则will flag位则为1,00000100转换为十六进制为4
			if(willMessage != null){
				/**
				 * 如果遗嘱标志被设置为1，连接标志中的Will Qos和Wil Retain字段会被服务器用到，同时有效荷载中必须包含Will Topic和 Will Message字段
				 * 一旦被发布或者服务端收到了客户端发送的 DISCONNECT 报文，遗嘱消息就必须从存储的会话状态中移除
				 * 如果遗嘱标志被设置为0，连接标志中的Will Qos和Will Retain字段必须设置为0，并且有效荷载中不能包含Will Topic和Will Message字段
				 * 如果遗嘱标志被设置为0，网络连接断开时，不能发送遗嘱消息
				 * 服务端应该迅速发布遗嘱消息。在关机或故障的情况下(服务器关机或故障)，服务端可以推迟遗嘱消息的发布直到之后的重启。如果发生了这种情况，在服务器故障和遗嘱消息被发布之间可能会有一个延迟。
				 */
				connectFlags |= 0x04;
				//qos向左移动3位(0 1 2)到达4 3位
				connectFlags |= (willMessage.getQos()<<3);
				
				/**
				 * 如果遗嘱标志被设置为0，遗嘱保留（Will Retain）标志也必须设置为0.
				 * 如果遗嘱标志被设备为1：
				 * 		如果遗嘱保留被设置为0，服务端必须将遗嘱消息当作非保留消息发布
				 * 		如果遗嘱保留被设置为1，服务端必须将遗嘱消息当作保留消息发布
				 */
				//如果遗嘱保留被设置为1，则will retain位则为1，00100000转换为十六进制为20
				if(willMessage.isRetained()){
					connectFlags |= 0x20;
				}
			}
			
			//如果用户名被设置为1，则user name flag位则为1，10000000
			if(userName != null){
				connectFlags |= 0x80;
				//如果密码被设置为1，则password flag位则为1，01000000
				if(password != null){
					connectFlags |= 0x40;
				}
			}
			
			dos.write(connectFlags);
			
			//4.保持连接
			dos.writeShort(keepAliveInterval);
			
			dos.flush();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return baos.toByteArray();
	}
	
	/**
	 * CONNECT 有效荷载字段顺序：客户端标识符，遗嘱主题，遗嘱消息，用户名，密码
	 * @throws MqttException 
	 */
	@Override
	public byte[] getPayload() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DataOutputStream dos = new DataOutputStream(baos);
			encodeUTF8(dos,clientId);
			
			if(willMessage != null){
				encodeUTF8(dos,willTopic);
				/**
				 * 3.1.3.3 遗嘱消息
				 * 这个字段由一个两字节的长度和遗嘱消息的有效荷载组成，表示为零字节或多个字节序列。
				 * 长度给出了跟在后面的数据的字节数，不包含长度字段本身占用的两个字节
				 * 
				 * 至于这里为什么是writeShort  可看write(int v) 和 writeShort(int v)的源码
				 */
				dos.writeShort(willMessage.getPayload().length);
				dos.write(willMessage.getPayload());
			}
			
			if(userName != null){
				encodeUTF8(dos, userName);
				if(password != null){
					encodeUTF8(dos, new String(password));
				}
			}
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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	public MqttMessage getWillMessage() {
		return willMessage;
	}

	public void setWillMessage(MqttMessage willMessage) {
		this.willMessage = willMessage;
	}

	public String getWillTopic() {
		return willTopic;
	}

	public void setWillTopic(String willTopic) {
		this.willTopic = willTopic;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public String getProtocolName() {
		return protocolName;
	}

	public int getProtocolLevel() {
		return protocolLevel;
	}

	public byte getConnectFlags() {
		return connectFlags;
	}

}
