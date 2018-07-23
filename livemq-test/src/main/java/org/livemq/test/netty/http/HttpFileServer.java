package org.livemq.test.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {

	private static final String DEFAULT_FILE_URL = "/src/main/java/org/livemq/";
	
	public static void main(String[] args) {
		HttpFileServer.run(HOST, PORT);
	}
	
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8090;
	
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

				/**
				 * 首先向 ChannelPipeline 中添加 HTTP 请求消息解码器，
				 * 随后，又添加了 HttpObjectAggregator 解码器，它的作用是将多个消息转换为单一的 FullHttpRequest 或者 FullHttpResponse，
				 * 		原因是 HTTP 解码器在每个 HTTP 消息中会产生多个消息对象。
				 * 		{
				 * 			(1) HttpRequest / HttpResponse
				 * 			(2) HttpContent
				 * 			(3) LastHttpContent
				 * 		}
				 * 然后新增 HTTP 响应编码器，对 HTTP 响应消息进行编码。
				 * 然后新增 Chunked handler，它的主要作用是支持异步发送大的码流（例如大的文件传输），但不占用过多的内存，防止发生 Java 内存溢出错误
				 * 
				 * 最后添加 HttpFileServerHandler，用于文件服务器的业务逻辑处理。
				 */
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
					ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
					ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
					ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
					ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(DEFAULT_FILE_URL));
				}
			});
			
			//发起异步连接操作
			bootstrap.bind(host, port).sync();
			System.out.println("HTTP 文件目录服务器启动，网址是：http://"+ host +":" + port + DEFAULT_FILE_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
