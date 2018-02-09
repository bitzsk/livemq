package org.livemq.server;

import org.livemq.exception.ExceptionHelper;
import org.livemq.exception.MqttException;
import org.livemq.log.Logger;
import org.livemq.log.LoggerFactory;

/**
 * 服务端启动入口
 * @author w.x
 * @date 2018年2月8日 下午5:04:28
 */
public class ServerMain {

	private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

	public void initializeAndRun(String[] args) throws MqttException {
		logger.log("Starting the server");
		
		ServerConfig config = null;
		
		if(args == null || args.length == 0){
			throw ExceptionHelper.createMqttException(MqttException.CODE_SERVER_CONFIG_FILE_PATH_IS_NULL_EXCEPTION);
		}
		
		if(args.length == 1){
			logger.log("Parsing configuration");
			
			config = new ServerConfig();
			config.parse(args[0]);
		}

		// TODO
		// 启动和调度清除任务
		// 清除日志，数据等残留文件信息
		
		if(args.length == 1){
			logger.log("Run server from config");
			long start = System.currentTimeMillis();
			
			runFromConfig(config, start);
		}
	}
	
	private void runFromConfig(ServerConfig config, long start) throws MqttException {
		ServerThread thread = new ServerThread(config);
		logger.log("Starting the server thread");
		
		thread.start(start);
	}
}
