package org.livemq.test.log;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

/**
 * 
 * @Title TestLog4j
 * @Package org.livemq.test.log
 * @Description 测试动态配置 Log4j
 * @author xinxisimple@163.com
 * @date 2018-07-25 15:20
 * @version 1.0.0
 * @see @see https://blog.csdn.net/sunny_na/article/details/55212029
 */
public class TestLog4j {
	private static final Logger logger = Logger.getLogger(TestLog4j.class);
	
	static {
		Properties properties = new Properties();
		// ### 设置###
		// ### 日志等级: OFF、FATAL、ERROR、WARN、INFO、DEBUG、ALL ###
		// # 将 debug 级别以上的日志输出到控制台 console, console 可以自定义名称
		properties.put("log4j.rootLogger", "debug,console,D,E");
		
		// ### 输出信息到控制台 ###
		properties.put("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
		properties.put("log4j.appender.console.Target", "System.out");
		properties.put("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");
//		properties.put("log4j.appender.console.layout.ConversionPattern", "[%-5p] %d{yyyy-MM-dd HH:mm:ss.SSS} [%l] %m%n");
		properties.put("log4j.appender.console.layout.ConversionPattern", "[%-5p] %d{yyyy-MM-dd HH:mm:ss.SSS} [%c#%L] %m%n");
	
		// ### 输出DEBUG 级别以上的日志到=info.log ###
		properties.put("log4j.appender.D", "org.apache.log4j.DailyRollingFileAppender");
		properties.put("log4j.appender.D.File", "C:/Users/Administrator/Desktop/log/info.log");
		properties.put("log4j.appender.D.Append", "true");
		properties.put("log4j.appender.D.Threshold", "DEBUG");
		properties.put("log4j.appender.D.layout", "org.apache.log4j.PatternLayout");
		properties.put("log4j.appender.D.layout.ConversionPattern", "%-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n");
		
		// ### 输出ERROR 级别以上的日志到=error.log  ###
		properties.put("log4j.appender.E", "org.apache.log4j.DailyRollingFileAppender");
		properties.put("log4j.appender.E.File", "C:/Users/Administrator/Desktop/log/error.log");
		properties.put("log4j.appender.E.Append", "true");
		properties.put("log4j.appender.E.Threshold", "ERROR");
		properties.put("log4j.appender.E.layout", "org.apache.log4j.PatternLayout");
		properties.put("log4j.appender.E.layout.ConversionPattern", "%-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n");
		
		PropertyConfigurator.configure(properties);
	}
	
	@Test
	public void testCodeLog() {
		logger.info("This is info message");
		logger.debug("This is debug message");
		logger.warn("This is warn message");
		logger.error("This is error message");
	}
}
