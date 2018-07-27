package org.livemq.cache.redis.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.livemq.common.data.RedisNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.util.Pool;

/**
 * 
 * @Title RedisConnection
 * @Package org.livemq.cache.redis.connection
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-21 18:30
 * @version 1.0.0
 */
public final class RedisConnectionFactory {
	private static final Logger logger = LoggerFactory.getLogger(RedisConnectionFactory.class);
	
	/** 最大活跃连接数，默认：30*/
	public static final int MAX_ACTIVE = 30;
	/** 默认最大空闲连接数，默认：10*/
	public static final int MAX_IDLE = 10;
	/** 默认最大等待可用连接时间，默认：2000，单位：毫秒。-1 表示永不超时*/
	public static final int MAX_WAIT = 2000;
	/** 从池中获取连接的时候，是否进行有效性检查*/
	public static final boolean TEST_ON_BORROW = true;
	/** 归还连接的时候，是否进行有效性检查*/
	public static final boolean TEST_ON_RETURN = false;

	private int maxActive = MAX_ACTIVE;
	private int maxIdle = MAX_IDLE;
	private int maxWait = MAX_WAIT;
	private boolean testOnBorrow = TEST_ON_BORROW;
	private boolean testOnReturn = TEST_ON_RETURN;
	
	/** 与服务器建立连接的地址，默认: localhost*/
	private String hostName = Protocol.DEFAULT_HOST;
	/** 与服务器建立连接的端口号，默认: 6379*/
	private int port = Protocol.DEFAULT_PORT;
	/** 与服务器建立连接超时时间 (单位: 毫秒)*/
	private int timeout = Protocol.DEFAULT_TIMEOUT;
	// TODO: Not used
	private String password;
	
	private List<RedisNode> nodes = new ArrayList<>();
	private boolean isCluster = false;
	
	private static Pool<Jedis> pool;
	private static JedisCluster cluster;
	private JedisPoolConfig config;
	
	/**
	 * init
	 */
	public void init() {
		initConfig();
		
		if(isCluster) {
			cluster = createCluster();
		}else {
			pool = createPool();
		}
		logger.info("redis init success, isCluster:{}, nodes={}", isCluster, nodes);
	}
	
	/**
	 * destroy
	 */
	public void destroy() {
		if(pool != null) {
			try {
				pool.destroy();
			} catch (Exception e) {
				logger.warn("Cannot properly close Jedis pool", e);
			}
			pool = null;
		}
		if(cluster != null) {
			try {
				cluster.close();
			} catch (IOException e) {
				logger.warn("Cannot properly close Jedis cluster", e);
			}
			cluster = null;
		}
		logger.info("redis destroy success");
	}
	
	/**
	 * close jedis
	 * @param jedis
	 */
	public void close(Jedis jedis) {
		jedis.close();
	}
	
	/**
	 * 从 jedis 连接池中获取一个新的连接
	 * @return
	 */
	public Jedis getJedis() {
		try {
			if(pool != null) {
				return pool.getResource();
			}
			
			Jedis jedis = new Jedis(hostName, port, timeout);
			jedis.connect();
			return jedis;
		} catch (Exception e) {
			throw new RuntimeException("Cannot get Jedis connection", e);
		}
	}
	
	/**
	 * 获取 redis 集群连接
	 * @return
	 */
	public JedisCluster getJedisCluster() {
		return cluster;
	}

	private void initConfig() {
		if(config == null) {
			config = new JedisPoolConfig();
			config.setMaxTotal(maxActive);
			config.setMaxIdle(maxIdle);
			config.setMaxWaitMillis(maxWait);
			config.setTestOnBorrow(testOnBorrow);
			config.setTestOnReturn(testOnReturn);
		}
	}

	private Pool<Jedis> createPool() {
		return pool == null ? new JedisPool(config, hostName, port, timeout, password) : pool;
	}

	private JedisCluster createCluster() {
		return cluster == null ? createRedisCluster() : cluster;
	}
	
	private JedisCluster createRedisCluster() {
		Set<HostAndPort> set = new HashSet<HostAndPort>();
		
		for (RedisNode node : nodes) {
			set.add(new HostAndPort(node.getHost(), node.getPort()));
		}
		
		return new JedisCluster(set, timeout, config);
	}
	
	/**
	 * 设置 Redis 节点
	 * @param node
	 */
	public void setNode(RedisNode node) {
		if(node != null) {
			List<RedisNode> nodes = new ArrayList<>();
			nodes.add(node);
			setNodes(nodes);
		}
	}
	
	/**
	 * 设置 Redis 节点集
	 * @param nodes
	 */
	public void setNodes(List<RedisNode> nodes) {
		this.nodes = nodes;
		if(!nodes.isEmpty()) {
			if(nodes.size() > 1) {
				isCluster = true;
			}else {
				hostName = nodes.get(0).getHost();
				port = nodes.get(0).getPort();
			}
		}
	}
	
	/**
	 * 设置 redis 密码
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * 返回 redis 是否为集群模式
	 * @return
	 */
	public boolean isCluster() {
		return isCluster;
	}
}
