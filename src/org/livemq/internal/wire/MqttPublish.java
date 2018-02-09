package org.livemq.internal.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.MqttMessage;
import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;
import org.livemq.internal.stream.CountingInputStream;


/**
 * <h1>MQTT 发布消息报文</h1>
 * <p></p>
 * <h2>响应</h2>
 * PUBLISH 报文的接收者必须按照根据 PUBLISH 报文中的 QoS 等级发送响应 <br>
 * PUBLISH 报文的预期响应 <br>
 * ------------------------------ <br>
 * | 服务质量等级  	|	预期响应		| <br>
 * ------------------------------ <br>
 * | QoS 0		|	无响应		| <br>
 * ------------------------------ <br>
 * | QoS 1		|	PUBACK响应	| <br>
 * ------------------------------ <br>
 * | QoS 2		|	PUBREC响应	| <br>
 * ------------------------------ <br>
 */
public class MqttPublish extends MqttWireMessage {

	private MqttMessage message;
	private String topicName;
	
	private byte[] encodedPayload;
	
	public MqttPublish(String topicName, MqttMessage message) {
		super(MqttWireMessage.MESSAGE_TYPE_PUBLISH);
		this.topicName = topicName;
		this.message = message;
	}
	
	/**
	 * @param info the message info byte
	 * @param data the variable header and payload bytes
	 * @throws MqttException 
	 * @throws Exception 
	 */
	public MqttPublish(byte info, byte[] data) throws MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBLISH);
		message = new MqttMessage();
		message.setQos((info >> 1) & 0x03);
		if ((info & 0x01) == 0x01) {
			message.setRetained(true);
		}
		if ((info & 0x08) == 0x08) {
			message.setDuplicate(true);
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		CountingInputStream counter = new CountingInputStream(bais);
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(counter);
			topicName = decodeUTF8(dis);
			if (message.getQos() > 0) {
				msgId = dis.readUnsignedShort();
			}
			byte[] payload = new byte[data.length-counter.getCounter()];
			dis.readFully(payload);
			message.setPayload(payload);
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
	 * 参照 3.3.1 固定报头
	 * @return
	 */
	@Override
	public byte getMessageInfo() {
		byte info = (byte) (message.getQos() << 1);
		// 0000 0001 -> 1
		if(message.isRetained()){
			info |= 0x01;
		}
		// 0000 1000 -> 8
		if(message.isDuplicate() || duplicate){
			info |= 0x08;
		}
		return info;
	}

	/**
	 * 参照 3.3.2 可变报头
	 * @return
	 * @throws MqttException 
	 */
	@Override
	public byte[] getVariableHeader() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DataOutputStream dos = new DataOutputStream(baos);
			
			/**
			 * 主题名
			 * 主题名必须是PUBLISH 报文可变报头的第一个字段。它必须是 1.5.3 节定义的 UTF-8 编码的字符串
			 * PUBLISH 报文中的主题名不能包含通配符
			 * 服务端发送给客户端的 PUBLISH 报文的主题名必须匹配该订阅的主题过滤器
			 */
			encodeUTF8(dos, topicName);

			/**
			 * 报文标识符
			 * 只有当QoS等级是 1 或 2 时，报文标识符字段才能出现咋 PUBLISH 报文中
			 */
			if(message.getQos() > 0){
				dos.writeShort(msgId);
			}
			
			dos.flush();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return baos.toByteArray();
	}
	
	@Override
	public byte[] getPayload() {
		if(encodedPayload == null){
			encodedPayload = encodePayload(message);
		}
		return encodedPayload;
	}

	private byte[] encodePayload(MqttMessage message) {
		return message.getPayload();
	}
	
	public MqttMessage getMessage() {
		return message;
	}
	
	public String getTopic() {
		return topicName;
	}
	
}
