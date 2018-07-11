package com.livemq.common.exception;

/**
 * 异常辅助类
 * @author xinxisimple@163.com
 * @date 2018-07-04 12:14
 */
public class ExceptionHelper {

	public static MqttException createMqttException(int code) {
		return new MqttException(code);
	}
	
	public static MqttException createMqttException(Throwable cause){
		return new MqttException(cause);
	}
	
	public static MqttException createMqttException(String msg){
		return new MqttException(msg);
	}
}
