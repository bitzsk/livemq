package org.livemq.test.netty.websocket;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

	private static String HOST = "127.0.0.1";
	private static int PORT = 8089;
	private static String URL = "ws://";
	
	public WebSocketServerHandler(String host, int port) {
		HOST = host;
		PORT = port;
		URL += (HOST + ":" + PORT);
	}
	
	private WebSocketServerHandshaker handshaker;
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
		//传统的 HTTP 接入(第一次握手请求消息由 HTTP 协议承载，所以它是一个 HTTP 消息)
		if(msg instanceof FullHttpRequest) {
			handlerHttpRequest(ctx, (FullHttpRequest) msg);
		}
		//WebSocket 接入
		else if(msg instanceof WebSocketFrame) {
			handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
	
		/**
		 * 如果 HTTP 解码失败，返回 HTTP 异常
		 * 如果消息头中没有包含 Upgrade 字段或者它的值不是 websocket，则返回 HTTP 400 响应
		 */
		if(!request.getDecoderResult().isSuccess() || !"websocket".equals(request.headers().get("Upgrade"))) {
			setHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		
		//构造握手响应返回，本机测试
		WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(URL, null, false);
		handshaker = factory.newHandshaker(request);
		if(handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
		}else {
			handshaker.handshake(ctx.channel(), request);
		}
	}
	
	private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		//判断是否是关闭链路的指令
		if(frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		
		//判断是否为 ping 消息，如果是则返回一个 pong 消息
		if(frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		
		//本例程仅支持文本消息，不支持二进制消息
		if(!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
		}
		
		//返回应答消息
		String request = ((TextWebSocketFrame) frame).text();
		System.out.println(String.format("%s received %s", ctx.channel(), request));
		TextWebSocketFrame text = new TextWebSocketFrame(request + "，欢迎使用 Netty WebSocket 服务，现在时刻：" + new Date().toString());
		ctx.channel().write(text);
	}

	private void setHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request,
			FullHttpResponse response) {
		//返回应答给客户端
		if(response.getStatus().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8);
			response.content().writeBytes(buf);
			buf.release();
			HttpHeaders.setContentLength(response, response.content().readableBytes());
		}
		
		//如果是非 Keep-Alive，关闭连接
		ChannelFuture channelFuture = ctx.channel().writeAndFlush(response);
		if(!HttpHeaders.isKeepAlive(request) || response.getStatus().code() != 200) {
			channelFuture.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
}
