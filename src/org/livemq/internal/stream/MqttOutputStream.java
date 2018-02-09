package org.livemq.internal.stream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;
import org.livemq.internal.wire.MqttWireMessage;


/**
 * <h1>核心输出流</h1>
 * <p></p>
 */
public class MqttOutputStream {
	
	private BufferedOutputStream out;
	
	public MqttOutputStream(OutputStream os){
		this.out = new BufferedOutputStream(os);
	}

	public void close() throws IOException {
		out.close();
	}
	
	public void flush() throws IOException {
		out.flush();
	}
	
	public void write(byte[] b) throws IOException {
		out.write(b);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}
	
	public void write(int b) throws IOException {
		out.write(b);
	}
	
	public void write(MqttWireMessage message) throws MqttException{
		byte[] bytes = message.getHeader();
		byte[] pl = message.getPayload();
		try {
			out.write(bytes,0,bytes.length);
			out.write(pl,0,pl.length);
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(e);
		}
	}

}
