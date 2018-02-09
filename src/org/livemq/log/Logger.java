package org.livemq.log;

/**
 * 日志接口
 * @author w.x
 * @date 2018年2月8日 下午2:13:19
 */
public interface Logger {

	public void init(Class<?> clazz);
	
	public void warn(Object message);

	public void log(Object message);
	
}
