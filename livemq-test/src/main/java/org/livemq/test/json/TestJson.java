package org.livemq.test.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.livemq.test.Param;
import org.livemq.tools.util.JsonUtil;

import com.alibaba.fastjson.TypeReference;

public class TestJson {

	@Test
	public void test() {
		Param param = new Param("name", "Jack");
		
		List<String> list = new ArrayList<>();
		list.add("A");
		list.add("B");
		
		Map<String, Object> map = new HashMap<>();
		map.put("title", "This is title");
		map.put("body", "HTML body");
		
		// 1.Java Bean 转 json 字符串
		String beanJson = JsonUtil.toJson(param);
		// 2.List 转 json 字符串
		String listJson = JsonUtil.toJson(list);
		// 3.Map 转 json 字符串
		String mapJson = JsonUtil.toJson(map);
		
		System.out.println(beanJson);
		System.out.println(listJson);
		System.out.println(mapJson);
		
		// 4.从 json 字符串转为 Java Bean
		Param p2 = JsonUtil.fromJson(beanJson, Param.class);
		System.out.println(p2);
		
		// 5.从 json 字符串转为 List
		List<String> l2 = JsonUtil.fromJsonArray(listJson, String.class);
		System.out.println(l2);
		
		// 6.从 json 字符串转为 Map
		Map<String, Object> m2 = JsonUtil.fromJson(mapJson, new TypeReference<HashMap<String, Object>>() {}.getType());
		System.out.println(m2);
	}
}
