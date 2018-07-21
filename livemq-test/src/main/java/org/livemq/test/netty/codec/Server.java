package org.livemq.test.netty.codec;

import org.livemq.test.netty.codec.NettyDecoder;
import org.livemq.test.netty.codec.NettyEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

	public static void main(String[] args) {
		Server.run(HOST, PORT);
	}
	
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8088;
	
	/**用于分配处理业务线程的线程组个数 */  
    private static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2; // cpu 核数*2
    /** 业务出现线程大小*/  
    private static final int BIZTHREADSIZE = 4; 
    
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    private static final EventLoopGroup workGroup = new NioEventLoopGroup(BIZTHREADSIZE);
	
	public static void run(String ip, int port) {
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new NettyEncoder());
					pipeline.addLast(new NettyDecoder());
					pipeline.addLast(new ServerHandler());
				}
			});
			
			//发起异步连接操作
			bootstrap.bind(ip, port).sync();
			System.out.println("Netty server start ok [127.0.0.1:"+ port +"]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
