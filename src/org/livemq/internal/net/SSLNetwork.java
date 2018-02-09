package org.livemq.internal.net;

import java.io.IOException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * SSL安全连接实现
 * @author w.x
 * @date 2018年2月9日 下午1:36:07
 */
public class SSLNetwork extends TCPNetwork {

	private String[] enabledCiphers;
	
	public SSLNetwork(SSLSocketFactory factory, String host, int port) {
		super(factory, host, port);
	}

	public void setEnabledCiphers(String[] enabledCiphers) {
		this.enabledCiphers = enabledCiphers;
		if(socket != null && enabledCiphers != null){
			((SSLSocket) socket).setEnabledCipherSuites(enabledCiphers);
		}
	}
	
	@Override
	public void start() throws IOException {
		super.start();
		setEnabledCiphers(enabledCiphers);
		((SSLSocket)socket).startHandshake();
	}
	
}
