package org.livemq.util;

import java.util.Date;

public class Random {

	private static final java.util.Random random = new java.util.Random();
	
	/**
	 * 随机编号<br>
	 * 当前时间戳 + Math.random() * Integer.MAX_VALUE
	 * @return
	 */
	public static String next(){
		return new Date().getTime() + "-" + random.nextInt(Integer.MAX_VALUE) + 1;
	}
}
