package org.livemq.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Param {

	private String key;
	
	private Object value;

	private List<String> list = new ArrayList<>();
	
	private Map<String, Object> map = new HashMap<>();
	
	public Param(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public Param() {
		super();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public void setList(List<String> list) {
		this.list = list;
	}
	
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "Param [key=" + key + ", value=" + value + ", list=" + list + ", map=" + map + "]";
	}

}
