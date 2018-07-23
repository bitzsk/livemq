package org.livemq.tools.util;

import java.lang.reflect.Type;
import java.util.List;

import org.livemq.api.constant.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class JsonUtil {
	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	
	public static String toJson(Object object) {
		try {
			return JSON.toJSONString(object);
		} catch (Exception e) {
			logger.error("toJson error, bean=" + object, e);
		}
		return null;
	}
	
	public static <T> T fromJson(String json, Class<T> clazz) {
		try {
			return JSON.parseObject(json, clazz);
		} catch (Exception e) {
			logger.error("fromJson error, json=" + json, e);
		}
		return null;
	}
	
	public static <T> T fromJson(byte[] data, Class<T> clazz) {
		return fromJson(new String(data, Charsets.UTF_8), clazz);
	}
	
	public static <T> T fromJson(String json, Type type) {
		try {
			return JSON.parseObject(json, type);
		} catch (Exception e) {
			logger.error("fromJson error, json=" + json, e);
		}
		return null;
	}
	
	public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
		try {
			return JSON.parseArray(json, clazz);
		} catch (Exception e) {
			logger.error("fromJson error, json=" + json, e);
		}
		return null;
	}
	
}
