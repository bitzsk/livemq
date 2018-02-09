package org.livemq.log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志接口的默认实现
 * @author w.x
 * @date 2018年2月8日 下午2:13:41
 */
public class LoggerSample implements Logger {

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	private String className;
	
	public void init(Class<?> clazz) {
		this.className = clazz.getName();
	}

	public void warn(Object message) {
		System.err.println(format.format(new Date()) + " " + className + ": " + message.toString());
	}

	public void log(Object message) {
		System.out.println(format.format(new Date()) + " " + className + ": " + message.toString());
	}

}
