package org.livemq.test.redis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.livemq.cache.redis.manager.RedisManager;

public class TestJedis {

	private static final RedisManager manager = RedisManager.getInstance();
	
	@Before
	public void before() {
		manager.init();
	}
	
	@After
	public void after() {
		manager.destroy();
	}
	
	@Test
	public void test() {
		String key = "livemq";
		String value = "1";
		System.out.println("Test set("+key+", "+value+"): " + manager.set(key, value));
		System.out.println("Test set(test, "+value+"): " + manager.set("test", value));
		System.out.println("test get("+key+"): " + manager.get(key));
		System.out.println("Test exists("+key+"): " + manager.exists(key));
		System.out.println("Test expire("+key+", 1000): " + manager.expire(key, 1000));
		System.out.println("Test ttl("+key+"): " + manager.ttl(key));
		System.out.println("Test incr("+key+"): " + manager.incr(key));
		
		String hk = "person";
		System.out.println("Test hset("+hk+", name, Lucy): " + manager.hset(hk, "name", "Lucy"));
		System.out.println("Test hset("+hk+", age, 20): " + manager.hset(hk, "age", "20"));
		System.out.println("Test hset("+hk+", birthday, 2018-07-23): " + manager.hset(hk, "birthday", "2018-07-23"));
		System.out.println("Test hset("+hk+", address, 中国 - 深圳): " + manager.hset(hk, "address", "中国 - 深圳"));
		
		System.out.println("Test hget("+hk+", name): " + manager.hget(hk, "name"));
		System.out.println("Test hget("+hk+", age): " + manager.hget(hk, "age"));
		System.out.println("Test hget("+hk+", birthday): " + manager.hget(hk, "birthday"));
		System.out.println("Test hget("+hk+", address): " + manager.hget(hk, "address"));
		
		System.out.println("Test hdel("+hk+", age, birthday): " + manager.hdel(hk, "age", "birthday"));
		System.out.println("Test hexists("+hk+", age): " + manager.hexists(hk, "age"));
		
		System.out.println("Test hvals("+hk+"): " + manager.hvals(hk));
		System.out.println("Test del(test): " + manager.del("test"));
	}
}
