package org.livemq.test.netty.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private static String URL;
	
	public HttpFileServerHandler(String url) {
		URL = url;
	}
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		System.out.println(URL);
		//首先对 HTTP 请求消息的解码结果进行判断，如果解码失败，直接构造 HTTP 400 错误返回。
		if(!request.getDecoderResult().isSuccess()) {
			sendError(ctx,HttpResponseStatus.BAD_REQUEST);
			return;
		}
		
		//对请求的方法进行判断，如果不是从浏览器或者表单设置为 GET 发起的请求（例如 POST），则构造 HTTP 405 错误返回。
		if(request.getMethod() != HttpMethod.GET) {
			sendError(ctx,HttpResponseStatus.METHOD_NOT_ALLOWED);
			return;
		}
		
		//对 RUL 进行包装
		String uri = request.getUri();
		String path = sanitizeUri(uri);
		//如果构造的 URI 不合法，则返回 HTTP 403 错误。
		if(path == null) {
			sendError(ctx, HttpResponseStatus.FORBIDDEN);
			return;
		}
		
		//使用新组装的 RUI 路径构造 File 对象。
		File file = new File(path);
		//如果文件不存在或是系统隐藏文件，则构造 HTTP 404 异常返回。
		if(file.isHidden() || !file.exists()) {
			sendError(ctx, HttpResponseStatus.NOT_FOUND);
			return;
		}
		
		//如果是目录，则发送目录的链接给客户端浏览器
		if(file.isDirectory()) {
			if(uri.endsWith("/")) {
				sendListing(ctx, file);
			}else {
				sendRedirect(ctx, uri + "/");
			}
			return;
		}
		
		/**
		 * 如果用户在浏览器上点击超链接直接打开活着下载文件，
		 * 此处会对超链接的文件进行合法性判断，如果不是合法文件，则返回 HTTP 403 错误。
		 */
		if(!file.isFile()) {
			sendError(ctx, HttpResponseStatus.FORBIDDEN);
			return;
		}
		
		/**
		 * 校验通过后，使用随机文件读写类以只读的方式打开文件，如果文件打开失败，则返回 HTTP 404 错误。
		 */
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "r");
		} catch (FileNotFoundException e) {
			sendError(ctx, HttpResponseStatus.NOT_FOUND);
			return;
		}
		
		//获取文件的长度
		long fileLength = randomAccessFile.length();
		//构造成功的 HTTP 应答消息
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		//然后在消息头中设置 content length 和 content type
		HttpHeaders.setContentLength(response, fileLength);
		setContentTypeHeader(response, file);
		//判断是否为 Keep-Alive，如果是，则在应答消息头中设置 Connection 为 Keep-Alive
		if(HttpHeaders.isKeepAlive(request)) {
			response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}
		//发送响应消息
		ctx.write(response);
		
		ChannelFuture sendFileFuture = null;
		//通过 Netty 的 ChunkedFile 对象直接将文件写入到发送缓冲区中
		sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
		//最后为 sendFuture 增加 GenericFutureListener
		sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
			
			@Override
			public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
				//total unkonw
				if(total < 0) {
					System.err.println("Transfer progress: " + progress);
				}else {
					System.err.println("Transfer progress: " + progress + " / " + total);
				}
			}
			
			//如果发送完成则打印 "Transfer complete."
			@Override
			public void operationComplete(ChannelProgressiveFuture future) throws Exception {
				System.err.println("Transfer complete.");
			}
			
		});
		
		/**
		 * 如果使用 chunked 编码，最后需要发送一个编码结束的空消息体，将 LastHttpContent 的 EMPTY_LAST_CONTENT 发送到缓冲区中，
		 * 标识所有的消息体已经发送完成，同时调用 flush 方法将之前发送缓冲区的消息刷新到 SocketChannel 中发送给对方。
		 */
		ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		//如果是非 Keep-Alive 的，最后一包消息发送完成后，服务器要主动关闭连接。
		if(!HttpHeaders.isKeepAlive(request)) {
			lastContentFuture.addListener(ChannelFutureListener.CLOSE);
		}
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}

	private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
	
	private String sanitizeUri(String uri) {
		//首先使用 JDK 的 java.net.URLDecoder 对 URL 进行解码，使用 UTF-8 字符集。
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			try {
				uri = URLDecoder.decode(uri, "ISO-8859-1");
			} catch (Exception e2) {
				throw new Error();
			}
		}
		
		/**
		 * 解码成功之后，对 URL 的合法性进行判断，
		 * 如果 URI 与允许访问的 URI 一致或者是其子目录（文件），则校验通过，否则返回空。
		 */
		if(!uri.startsWith(URL)) {
			return null;
		}
		if(!uri.startsWith("/")) {
			return null;
		}
		
		//将硬编码的文件路径分隔符替换为本地操作系统的文件路径分隔符。
		uri = uri.replace('/', File.separatorChar);
		
		//对新的 URI 做二次合法性校验，如果校验失败则直接返回空。
		if(uri.contains(File.separator + '.') 
				|| uri.contains('.' + File.separator) 
				|| uri.startsWith(".") 
				|| uri.endsWith(".") 
				|| INSECURE_URI.matcher(uri).matches()) {
			return null;
		}
		
		//最后对文件进行拼接，使用当前运行程序所在的工程目录 + URI 构造绝对路径返回。
		return System.getProperty("user.dir") + File.separator + uri;
	}
	
	private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
	
	private void sendListing(ChannelHandlerContext ctx, File dir) {
		//首先创建成功的 HTTP 响应消息
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		//随后设置消息头的类型为 "text/html; charset=UTF-8"
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
		StringBuilder builder = new StringBuilder();
		String dirPath = dir.getPath();
		builder.append("<!DOCTYPE html>");
		builder.append("<html><head><title>");
		builder.append(dirPath);
		builder.append("</title><head><body>");
		builder.append("<h3>");
		builder.append(dirPath).append("目录：");
		builder.append("</h3>");
		builder.append("<ul>");
		builder.append("<li>链接：<a href='../'>..</a></li>");
		for(File f : dir.listFiles()) {
			if(f.isHidden() || !f.canRead()) {
				continue;
			}
			
			String name = f.getName();
			if(!ALLOWED_FILE_NAME.matcher(name).matches()) {
				continue;
			}
			
			builder.append("<li>链接：<a href='"+ name +"'>"+ name +"</a></li>");
		}
		builder.append("</ul></body></html>");
		
		//分配对应消息的缓冲对象
		ByteBuf buffer = Unpooled.copiedBuffer(builder, CharsetUtil.UTF_8);
		//将缓冲区中的响应消息存放在 HTTP 应答消息中
		response.content().writeBytes(buffer);
		//然后释放缓冲区
		buffer.release();
		//最后调用 writeAndFlush 将响应消息发送到缓冲区并刷新到 SocketChannel 中。
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	private void sendRedirect(ChannelHandlerContext ctx, String newUri) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
		response.headers().set(HttpHeaders.Names.LOCATION, newUri);
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, 
				status, 
				Unpooled.copiedBuffer("Failure:" + status.toString() + "\r\n", CharsetUtil.UTF_8));
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	private void setContentTypeHeader(HttpResponse response, File file) {
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
	}
	
}
