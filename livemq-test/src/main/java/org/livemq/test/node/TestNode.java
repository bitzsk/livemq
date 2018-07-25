package org.livemq.test.node;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.livemq.test.node.entity.RedisNode;
import org.livemq.test.node.entity.ZookeeperNode;

/**
 * 
 * @Title Test
 * @Package org.livemq.test.node
 * @Description 测试自定义链表
 * @author xinxisimple@163.com
 * @date 2018-07-25 17:17
 * @version 1.0.0
 */
public class TestNode {

	private static List list = null;
	
	static {
		list = new List();
		list.add(new RedisNode("redis"));
		list.add(new ZookeeperNode("zookeeper"));
		list.end();
	}
	
	@Before
	public void before() {
		list.start();
	}

	@After
	public void after() {
		list.stop();
	}
	
	@Test
	public void test() {
		
	}
	
}
