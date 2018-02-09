package org.livemq.internal.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 网络连接接口
 * @author w.x
 * @date 2018年2月8日 下午2:49:03
 */
public interface Network {

	public static final int DEFAULT_PORT_TCP = 1883;
	
	public void start() throws IOException;
	
	public InputStream getInputStream() throws IOException;
	
	public OutputStream getOutputStream() throws IOException;
	
	public void stop() throws IOException;
}
