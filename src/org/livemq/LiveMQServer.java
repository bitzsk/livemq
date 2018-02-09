package org.livemq;

import java.io.File;

import org.livemq.exception.MqttException;
import org.livemq.server.ServerMain;

/**
 * 服务端
 * @author w.x
 * @date 2018年2月8日 下午1:44:22
 */
public class LiveMQServer {

	public static void main(String[] args) throws MqttException {
		// TODO 测试增加以下代码 [windows]
		// Linux 下注释即可
		args = new String[1];
		File dataDir = new File(System.getProperty("user.dir"));
		File resource = new File(dataDir, "resource");
		File cfg = new File(resource, "livemq.cfg");
		args[0] = cfg.getAbsolutePath();
		
		ServerMain main = new ServerMain();
		main.initializeAndRun(args);
	}
}
