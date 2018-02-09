package org.livemq.internal.stream;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;
import org.livemq.internal.wire.MqttWireMessage;

/**
 * <h1>核心输入流</h1>
 * <p></p>
 */
public class MqttInputStream {
	
	private DataInputStream in;
	
	public MqttInputStream(InputStream is){
		this.in = new DataInputStream(is);
	}

	public MqttWireMessage readMqttMessage() throws MqttException {
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		MqttWireMessage message = null;
		try {
			byte first = in.readByte();
			byte type = (byte) ((first >>> 4) & 0x0F);
			if ((type < MqttWireMessage.MESSAGE_TYPE_CONNECT) ||
				(type > MqttWireMessage.MESSAGE_TYPE_DISCONNECT)) {
				throw ExceptionHelper.createMqttException(MqttException.CODE_MESSAGE_TYPE_EXCEPTION);
			}
			long remLen = MqttWireMessage.decodeMBI(in);
			bais.write(first);
			// bit silly, we decode it then encode it
			bais.write(MqttWireMessage.encodeMBI(remLen));
			bais.flush();
			byte[] packet = new byte[(int)(bais.size()+remLen)];
			in.readFully(packet,bais.size(),packet.length - bais.size());
			byte[] header = bais.toByteArray();
			System.arraycopy(header,0,packet,0, header.length);
			message = MqttWireMessage.createWireMessage(packet);
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
		return message;
	}

}
