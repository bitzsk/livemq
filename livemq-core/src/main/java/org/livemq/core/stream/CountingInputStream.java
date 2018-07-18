package org.livemq.core.stream;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @Title 自定义的计数输入流
 * @Package org.livemq.core.stream
 * @Description 用来计算某个输入流已经读取的字节数
 * @author xinxisimple@163.com
 * @date 2018-07-18 15:04
 * @version 1.0.0
 */
public class CountingInputStream extends InputStream {

	private InputStream in;
	private int counter;
	
	public CountingInputStream(InputStream in) {
		this.in = in;
	}
	
	@Override
	public int read() throws IOException {
		int read = in.read();
		if(read != -1) {
			counter ++;
		}
		return read;
	}
	
	public int getCounter() {
		return counter;
	}

	public void resetCounter() {
		counter = 0;
	}

}
