package org.livemq.test.tools;

import org.junit.Test;
import org.livemq.tools.util.PropertiesUtil;

public class TestPropertiesUtils {

	@Test
	public void test() {
		String path = "livemq.cfg";
		PropertiesUtil.load(path);
	}
}
