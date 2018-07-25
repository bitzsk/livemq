package org.livemq.test.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Title TestLog
 * @Package org.livemq.test.log
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-25 15:17
 * @version 1.0.0
 * @see https://blog.csdn.net/sunny_na/article/details/55212029
 */
public class TestLog {
	private static final Logger logger = LoggerFactory.getLogger(TestLog.class);
	
	//log4j.properties
	/*
	### 设置###
	### 日志等级: OFF、FATAL、ERROR、WARN、INFO、DEBUG、ALL ###
	# 将 debug 级别以上的日志输出到控制台 console, console 可以自定义名称
	log4j.rootLogger = debug,console,D,E

	### 输出信息到控制台 ###
	log4j.appender.console = org.apache.log4j.ConsoleAppender
	log4j.appender.console.Target = System.out
	log4j.appender.console.layout = org.apache.log4j.PatternLayout
	log4j.appender.console.layout.ConversionPattern = [%-5p] %d{yyyy-MM-dd HH:mm:ss.SSS} %l%n%m%n

	### 输出DEBUG 级别以上的日志到=info.log ###
	log4j.appender.D = org.apache.log4j.DailyRollingFileAppender
	log4j.appender.D.File = C://Users/Administrator/Desktop/log/info.log
	log4j.appender.D.Append = true
	log4j.appender.D.Threshold = DEBUG 
	log4j.appender.D.layout = org.apache.log4j.PatternLayout
	log4j.appender.D.layout.ConversionPattern = [%-5p] %d{yyyy-MM-dd HH:mm:ss.SSS} %l%n%m%n

	### 输出ERROR 级别以上的日志到=error.log  ###
	log4j.appender.E = org.apache.log4j.DailyRollingFileAppender
	log4j.appender.E.File = C://Users/Administrator/Desktop/log/error.log 
	log4j.appender.E.Append = true
	log4j.appender.E.Threshold = ERROR 
	log4j.appender.E.layout = org.apache.log4j.PatternLayout
	log4j.appender.E.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
	*/
	
	@Test
	public void test() {
		logger.info("This is info message");
		logger.debug("This is debug message");
		logger.warn("This is warn message");
		logger.error("This is error message");
	}
	
}
