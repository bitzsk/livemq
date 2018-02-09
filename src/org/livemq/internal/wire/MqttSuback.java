package org.livemq.internal.wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;


/**
 * <h1>MQTT 订阅消息确认报文</h1>
 * <p>服务端发送 SUBACK 报文给客户端，用于确认它已收到并且正在处理的 SUBSCRIBE 报文</p>
 */
public class MqttSuback extends MqttAck {

	/**
	 * 返回码清单
	 * 每个返回码对应等待确认的 SUBSCRIBE 报文中的一个主题过滤器。
	 * 返回码的顺序必须和 SUBSCRIBE 报文中主题过滤器的顺序相同
	 */
	private int[] grantedQos;
	
	/**
	 * 成功 最大 QoS 0
	 */
	public static final int SUB_SUCCESS_QOS_ZERO = 0x00;
	/**
	 * 成功 最大 QoS 1
	 */
	public static final int SUB_SUCCESS_QOS_ONE = 0x01;
	/**
	 * 成功 最大 QoS 2
	 */
	public static final int SUB_SUCCESS_QOS_TWO = 0x02;
	/**
	 * 失败
	 */
	public static final int SUB_FAILE = 0x80;
	
	public MqttSuback(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_SUBACK);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
		int index = 0;
		grantedQos = new int[data.length-2];
		int qos = dis.read();
		while (qos != -1) {
			grantedQos[index] = qos;
			index++;
			qos = dis.read();
		}
		dis.close();
	}
	
	public MqttSuback(int[] grantedQos){
		super(MqttWireMessage.MESSAGE_TYPE_SUBACK);
		this.grantedQos = grantedQos;
	}
	
	public MqttSuback(MqttSubscribe message, int[] grantedQos){
		super(MqttWireMessage.MESSAGE_TYPE_SUBACK);
		msgId = message.getMessageId();
		this.grantedQos = grantedQos;
	}
	
	@Override
	public byte[] getVariableHeader() throws MqttException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeShort(msgId);
			dos.flush();
		}catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return baos.toByteArray();
	}
	
}
