package org.livemq.internal.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.SocketFactory;

/**
 * TCP连接实现
 * @author w.x
 * @date 2018年2月8日 下午2:50:00
 */
public class TCPNetwork implements Network {

	protected Socket socket;
	private SocketFactory factory;
	private String host;
	private int port;
	private int conTimeout;
	
	public TCPNetwork(SocketFactory factory, String host, int port){
		this.factory = factory;
		this.host = host;
		this.port = port;
	}
	
	public void start() throws IOException {
		socket = factory.createSocket();
		SocketAddress sockaddr = new InetSocketAddress(host, port);
		socket.connect(sockaddr, conTimeout * 1000);
	}

	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	public void stop() throws IOException {
		if(socket != null){
			socket.close();
		}
	}
	
	public void setConTimeout(int conTimeout) {
		this.conTimeout = conTimeout;
	}

}
