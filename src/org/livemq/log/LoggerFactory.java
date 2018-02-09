package org.livemq.log;

/**
 * 日志工厂
 * @author w.x
 * @date 2018年2月8日 下午2:13:35
 */
public class LoggerFactory {

	private static final String DEFAULT_LOGGER_CLASSNAME = "org.livemq.log.LoggerSample";
	
	public static Logger getLogger(Class<?> clazz){
		Logger logger = null;
		Class<?> logClass = null;
		try {
			logClass = Class.forName(DEFAULT_LOGGER_CLASSNAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			logger = (Logger) logClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(logger != null) logger.init(clazz);
		return logger;
	}
	
}
