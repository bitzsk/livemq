package com.livemq.common.exception;

/**
 * 自定义异常
 * @author xinxisimple@163.com
 * @date 2018-07-04 12:14
 */
public class MqttException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final int EXCEPTION_DEFAULT = 0x00;
	
	private int code;
	private Throwable cause;
	
	public MqttException() {
		super();
	}
	
	public MqttException(int code) {
		super();
		this.code = code;
	}
	
	public MqttException(Throwable cause) {
		super();
		this.code = EXCEPTION_DEFAULT;
		this.cause = cause;
	}
	
	public MqttException(int code, Throwable cause) {
		super();
		this.code = code;
		this.cause = cause;
	}
	
	public MqttException(String msg) {
		super(msg);
		this.code = EXCEPTION_DEFAULT;
	}
	
	public int getCode() {
		return code;
	}
	
	public Throwable getCause() {
		return cause;
	}
	
	public String getMessage(int code) {
		return "MESSAGE_" + code;
	}
	
	@Override
	public String toString() {
		return "MqttException [code=" + code + ", message="+ getMessage() +", cause=" + cause + "]";
	}
	
}
