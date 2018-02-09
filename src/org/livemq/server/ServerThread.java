package org.livemq.server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;
import org.livemq.internal.nio.NIOHandle;
import org.livemq.log.Logger;
import org.livemq.log.LoggerFactory;
import org.livemq.server.pers.DefaultPersistence;

public class ServerThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ServerThread.class);
	
	private ServerConfig config;
	
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private ServerSocket serverSocket;
	
	private Thread thread;
	private boolean running = false;
	
	private NIOHandle handle;
	
	public ServerThread(ServerConfig config){
		this.config = config;
	}
	
	public void start(long start) throws MqttException{
		try {
			//初始化选择器
			selector = Selector.open();
			
			//初始化 ServerSocketChannel
			serverSocketChannel = ServerSocketChannel.open();
			if(serverSocketChannel == null){
				throw ExceptionHelper.createMqttException(MqttException.CODE_SERVER_SOCKET_CHANNEL_OPEN_EXCEPTION);
			}
			
			serverSocket = serverSocketChannel.socket();
			//绑定 ip:port
			serverSocket.bind(config.getClientPortAddress());
			
			// 必须设置为非阻塞。
			// 与 Selector 一起使用时， Channel 必须处于非阻塞模式下。
			// 这意味着不能将 FileChannel 与 Selector 一起使用，因为 FileChannel 不能切换到非阻塞模式。
			// 而套接字通道都可以。
			serverSocketChannel.configureBlocking(false);
			
			//将 server 通道注册到 选择器
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			// 初始化 handle
			handle = new ServerHandle(new DefaultPersistence(config));
			
			// 初始化线程
			thread = new Thread(this);
			thread.start();
			
			// 开启
			running = true;
			
			long end = System.currentTimeMillis();
			logger.log("Server startup in "+ (end - start) +" ms");
		} catch (BindException e) {
			throw ExceptionHelper.createMqttException(MqttException.CODE_SERVER_PORT_IS_NOT_FREE_EXCEPTION);
		} catch (IOException e) {
			throw ExceptionHelper.createMqttException(MqttException.CODE_SERVER_START_IO_EXCEPTION);
		}
	}
	
	public void shutdown() throws MqttException{
		try {
			running = false;
			
			serverSocket.close();
			serverSocketChannel.close();
			selector.close();
		} catch (Exception e) {
			throw ExceptionHelper.createMqttException(MqttException.CODE_SERVER_CLOSE_EXCEPTION);
		}
	}
	
	public void run() {
		while(running){
			try {
				if(selector.select() == 0) continue;
				
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while(iterator.hasNext()){
					SelectionKey key = iterator.next();
					handle(key);
					iterator.remove();
				}
				
			} catch (Exception e) {
				logger.warn("一个客户端断开连接");
			}
		}
	}

	private void handle(SelectionKey key) throws IOException {
		if(key.isAcceptable()){
			handle.accept(key);
		}
		else if(key.isConnectable()){
			handle.connect(key);
		}
		else if(key.isReadable()){
			handle.read(key);
		}
		else if(key.isWritable()){
			handle.write(key);
		}
	}
	
	/**
	 * 集群
	 */
	public void join(){
		
	}
}
