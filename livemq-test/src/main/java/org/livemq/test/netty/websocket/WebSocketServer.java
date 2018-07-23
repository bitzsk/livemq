package org.livemq.test.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {

	public static void main(String[] args) {
		WebSocketServer.run(HOST, PORT);
	}
	
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8089;
	
	/**用于分配处理业务线程的线程组个数 */  
    private static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2; // cpu 核数*2
    /** 业务出现线程大小*/  
    private static final int BIZTHREADSIZE = 4; 
    
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    private static final EventLoopGroup workGroup = new NioEventLoopGroup(BIZTHREADSIZE);
	
	public static void run(String host, int port) {
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					//首先添加 HttpServerCodec，将请求和应答消息编码或者解码为 HTTP 消息
					pipeline.addLast("http-codec", new HttpServerCodec());
					//增加 HttpObjectAggregator， 它的目的是将 HTTP 消息的多个部分组合成一条完整的 HTTP 消息
					pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
					//添加 ChunkedWriteHandler 来向客户端发送 HTML5 文件，它主要用于支持浏览器和服务端进行 WebSocket 通信
					pipeline.addLast("http-chunked", new ChunkedWriteHandler());
					//WebSocket 服务端 handler
					pipeline.addLast("handler", new WebSocketServerHandler(host, port));
				}
			});
			
			//发起异步连接操作
			bootstrap.bind(host, port).sync();
			System.out.println("Netty web socket server start ok, connect to ws://"+ host +":" + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
