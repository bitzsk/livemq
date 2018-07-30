package org.livemq.cache.redis.manager;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.livemq.api.spi.common.CacheManager;
import org.livemq.cache.redis.connection.RedisConnectionFactory;
import org.livemq.common.data.RedisNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;

/**
 * 
 * @Title RedisManager
 * @Package org.livemq.cache.redis.manager
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-21 19:08
 * @version 1.0.0
 */
public final class RedisManager implements CacheManager {
	private static final Logger logger = LoggerFactory.getLogger(RedisManager.class);
	
	private static volatile RedisManager instance;
	
	private final RedisConnectionFactory factory = new RedisConnectionFactory();
	
	public static RedisManager getInstance() {
		if(instance == null) {
			synchronized (RedisManager.class) {
				if(instance == null) {
					instance = new RedisManager();
				}
			}
		}
		return instance;
	}
	
	private RedisManager() {}
	
	@Override
	public void start() {
		factory.setNode(new RedisNode("192.168.10.170", 6379));
		factory.init();
	}

	@Override
	public void stop() {
		if(factory != null) factory.destroy();
	}
	
	/**
	 * 执行 jedis 命令
	 * Function<T, R>.apply(T t) 执行 T 返回 R
	 * @param function
	 * @param r
	 * @return
	 */
	private <R> R call(Function<JedisCommands, R> function, R r) {
		if(factory.isCluster()) {
			try {
				return function.apply(factory.getJedisCluster());
			} catch (Exception e) {
				logger.error("redis ex", e);
				throw new RuntimeException(e);
			}
		}else {
			Jedis jedis = null;
			try {
				jedis = factory.getJedis();
				return function.apply(jedis);
			} catch(Exception e) {
				logger.error("redis ex", e);
				throw new RuntimeException(e);
			} finally {
				try {
					if(jedis != null) {
						factory.close(jedis);
					}
				} catch (Exception ex) {
					logger.error("jedis close ex", ex);
					throw new RuntimeException(ex);
				}
			}
		}
	}
	
	/**
	 * 执行 jedis 命令
	 * Consumer<T>.accept(T t) 执行 T，无返回值
	 * @param consumer
	 */
	private void call(Consumer<JedisCommands> consumer) {
		if(factory.isCluster()) {
			try {
				consumer.accept(factory.getJedisCluster());
			} catch (Exception e) {
				logger.error("redis ex", e);
				throw new RuntimeException(e);
			}
		}else {
			Jedis jedis = null;
			try {
				jedis = factory.getJedis();
				consumer.accept(jedis);
			} catch(Exception e) {
				logger.error("redis ex", e);
				throw new RuntimeException(e);
			} finally {
				try {
					if(jedis != null) {
						factory.close(jedis);
					}
				} catch (Exception ex) {
					logger.error("jedis close ex", ex);
					throw new RuntimeException(ex);
				}
			}
		}
	}

	@Override
	public String set(String key, String value) {
		return call(jedis -> jedis.set(key, value), null);
	}

	@Override
	public String get(String key) {
		return call(jedis -> jedis.get(key), null);
	}

	@Override
	public Boolean exists(String key) {
		return call(jedis -> jedis.exists(key), false);
	}

	@Override
	public Long expire(String key, int seconds) {
		return call(jedis -> jedis.expire(key, seconds), 0L);
	}

	@Override
	public Long ttl(String key) {
		return call(jedis -> jedis.ttl(key), 0L);
	}

	@Override
	public Long incr(String key) {
		return call(jedis -> jedis.incr(key), 0L);
	}

	@Override
	public Long hset(String key, String field, String value) {
		return call(jedis -> jedis.hset(key, field, value), 0L);
	}

	@Override
	public String hget(String key, String field) {
		return call(jedis -> jedis.hget(key, field), null);
	}

	@Override
	public Long hdel(String key, String ... field) {
		return call(jedis -> jedis.hdel(key, field), 0L);
	}

	@Override
	public Boolean hexists(String key, String field) {
		return call(jedis -> jedis.hexists(key, field), false);
	}

	@Override
	public List<String> hvals(String key) {
		return call(jedis -> jedis.hvals(key), null);
	}

	@Override
	public Long del(String key) {
		return call(jedis -> jedis.del(key), 0L);
	}
	
}
