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
 * @Title SUBACK - 订阅确认
 * @Package org.livemq.core.wire
 * @Description 服务端发送 SUBACK 报文给客户端，用于确认它已收到并且正在处理 SUBSCRIBE 报文。<br><br>
 * 		SUBACK 报文包含一个返回码清单，它们指定了 SUBSCRIBE 请求的每个订阅被授予的最大 QoS 等级。
 * @author xinxisimple@163.com
 * @date 2018-07-18 16:14
 * @version 1.0.0
 * @see https://github.com/mcxiaoke/mqtt/blob/master/mqtt/0309-SUBACK.md
 */
public class MqttSubAck extends MqttAck {

	/** 成功: 最大 QoS 0*/
	public static final int OK_00 = 0x00;
	/** 成功: 最大 QoS 1*/
	public static final int OK_01 = 0x01;
	/** 成功: 最大 QoS 2*/
	public static final int OK_02 = 0x02;
	/** 失败*/
	public static final int FAIL = 0x80;
	
	private int[] grantedQos;
	
	public MqttSubAck(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_SUBACK);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
		
		int index = 0;
		grantedQos = new int[data.length - 2]; // -2: 报文标识符占用 2 个 byte 位
		int qos = dis.read();
		while(qos != -1) {
			grantedQos[index] = qos;
			index ++;
			qos = dis.read();
		}
	}

	public MqttSubAck(int msgId, int grantedQos) {
		this(msgId, new int[] { grantedQos});
	}

	public MqttSubAck(int msgId, int[] grantedQos) {
		super(MqttWireMessage.MESSAGE_TYPE_SUBACK);
		this.msgId = msgId;
		this.grantedQos = grantedQos;
	}

	@Override
	public byte[] getVariableHeader() throws MqttException {
		return encodeMessageId();
	}

	@Override
	public byte[] getPayload() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			
			for (int qos : grantedQos) {
				dos.write(qos);
			}
			
			dos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
	}
	
}
