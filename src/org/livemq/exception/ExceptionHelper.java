package org.livemq.exception;

/**
 * 自定义异常帮助类
 * @author w.x
 * @date 2018年2月8日 下午2:08:00
 */
public class ExceptionHelper {

	public static MqttException createMqttException(int code){
		return new MqttException(code);
	}

	public static MqttException createMqttException(Throwable cause){
		return new MqttException(cause);
	}
	
}
