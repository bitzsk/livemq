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
 * <h1>MQTT 订阅消息报文</h1>
 * <p></p>
 */
public class MqttSubscribe extends MqttWireMessage {
	
	private String topics[];
	private int[] qos;
	private int count;
	
	public MqttSubscribe(byte info, byte[] data) throws MqttException{
		super(MqttWireMessage.MESSAGE_TYPE_SUBSCRIBE);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		try {
			msgId = dis.readUnsignedShort();

			count = 0;
			topics = new String[10];
			qos = new int[10];
			boolean end = false;
			while (!end) {
				try {
					topics[count] = decodeUTF8(dis);
					qos[count++] = dis.readByte();
				} catch (Exception e) {
					end = true;
				}
			}
			dis.close();
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
	
	public MqttSubscribe(String[] topics, int[] qos) throws MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_SUBSCRIBE);
		this.topics = topics;
		this.qos = qos;
		
		if(topics.length != qos.length){
			throw ExceptionHelper.createMqttException(MqttException.CODE_MESSAGE_TOPICS_QOS_LENGTH_EXCEPTION);
		}
		
		for(int i = 0;i < qos.length;i++){
			MqttMessage.validateQos(qos[i]);
		}
	}

	/**
	 * SUBSCRIBE 控制报文固定报头的第 3，2，1，0 位是保留位，必须设置为 0，0，1，0.服务端必须将其它的任何值都当做不合法的并关闭网络连接
	 */
	@Override
	public byte getMessageInfo() {
		return (byte) (2 | (duplicate ? 8 : 0));
	}

	/**
	 * 3.8.2 可变报头包含客户端标识符(2 字节)
	 * 参照 3.8.2.1 中的可变报头非规范示例
	 * @throws MqttException 
	 */
	@Override
	public byte[] getVariableHeader() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeShort(msgId);
			dos.flush();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return baos.toByteArray();
	}
	
	/**
	 * SUBSCRIBE 报文的有效荷载包含了一个主题过滤器列表，它们表示客户端想要订阅的主题。
	 * 1.SUBSCRIBE 报文有效荷载中的主题过滤器必须是 1.5.3 节定义的 UTF-8 字符串。
	 * 2.服务端应该支持包含通配符的主题过滤器。
	 * 3.如果服务器选择不支持包含通配符的主题过滤器，必须拒绝任何包含统配符过滤器的订阅要求。
	 * 4.每一个过滤器后面跟着一个字节，这个字节被叫做 服务质量要求（Requested Qos）。它给出了服务端向客户端发送应用消息所允许的最大QoS 等级
	 * @throws MqttException 
	 */
	@Override
	public byte[] getPayload() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DataOutputStream dos = new DataOutputStream(baos);
			for (int i=0; i<topics.length; i++) {
				encodeUTF8(dos,topics[i]);
				dos.writeByte(qos[i]);
			}
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return baos.toByteArray();
	}
	
	public String[] getTopics() {
		return topics;
	}

	public void setTopics(String[] topics) {
		this.topics = topics;
	}

	public int[] getQos() {
		return qos;
	}

	public void setQos(int[] qos) {
		this.qos = qos;
	}
	
}
