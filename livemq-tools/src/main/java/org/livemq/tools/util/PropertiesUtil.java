package org.livemq.tools.util;

import java.awt.geom.IllegalPathStateException;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @Title PropertiesUtils
 * @Package org.livemq.tools
 * @Description 读取配置文件工具类
 * @author xinxisimple@163.com
 * @date 2018-07-23 10:25
 * @version 1.0.0
 */
public class PropertiesUtil {

	private static Properties properties = new Properties();
	
	public static void load(String path) {
		try {
			properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(path));
		} catch (IOException e) {
			throw new IllegalPathStateException("属性文件加载失败");
		}
		
		System.out.println("size:" + properties.size());
		
		for (String key : properties.stringPropertyNames()) {
			System.out.println(key + "=" + properties.getProperty(key));
		}
	}
}
