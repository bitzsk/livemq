package org.livemq.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Title Zookeeper
 * @Package org.livemq.zk
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-23 19:00
 * @version 1.0.0
 * @see https://www.cnblogs.com/sigm/p/6749228.html
 * @see https://blog.csdn.net/qq_15370821/article/details/73692288
 */
public class ZKClient {
	private static final Logger logger = LoggerFactory.getLogger(ZKClient.class);
	
	private static volatile ZKClient instance;
	
	public static ZKClient getInstance() {
		if(instance == null) {
			synchronized (ZKServiceRegistryAndDiscovery.class) {
				if(instance == null) {
					instance = new ZKClient();
				}
			}
		}
		return instance;
	}
	
	private ZKClient() {}
	
	private CuratorFramework client;
	// 集群使用英文逗号 ',' 分隔即可
	private String hosts = "192.168.10.170:40110";
	private String namespace = "livemq";
	private int connectTimeout = 5000;
	private int sessionTimeout = 5000;
	/** 首次重试间隔，单位：毫秒*/
	private int baseSleepTimeMs = 1000;
	/** 最大重试次数*/
	private int maxRetries = 3;
	/** 每次重试时的最大睡眠时间，单位：毫秒*/
	private int maxSleepMs = 1000;

	private boolean running = false;
	
	public void init() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries, maxSleepMs);
		Builder builder = CuratorFrameworkFactory
			.builder()
			.connectString(hosts)
			.retryPolicy(retryPolicy)
			.namespace(namespace)
			.connectionTimeoutMs(connectTimeout)
			.sessionTimeoutMs(sessionTimeout);
		
		client = builder.build();
		logger.info("init zk client.");
	}
	
	public void start() {
		logger.info("zk startting...");
		if(isRunning()) {
			logger.info("zk is started.");
		}else {
			client.start();
			running = true;
			logger.info("zk started.");
		}
		logger.info("zk state is {}", client.getState());
	}
	
	public void stop() {
		logger.info("zk stopping...");
		if(isRunning()) {
			client.close();
			running = false;
			logger.info("zk stopped.");
		}else {
			logger.info("zk is stopped.");
		}
		logger.info("zk state is {}", client.getState());
	}
	
	/**
	 * 判断是否正在运行，默认 false
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}
	
	
	
}
