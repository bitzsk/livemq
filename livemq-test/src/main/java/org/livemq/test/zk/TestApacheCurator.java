package org.livemq.test.zk;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.livemq.api.constant.Charsets;

/**
 * http://www.jsondream.com/2017/01/14/netty-cluster.html
 * https://www.zifangsky.cn/1166.html
 */
public class TestApacheCurator {

	/**
	 * 会话超时时间
	 */
	private final int SESSION_TIMEOUT = 30 * 1000;
	
	/**
	 * 连接超时时间
	 */
	private final int CONNECTION_TIMEOUT = 3 * 1000;
	
	/**
	 * Zookeeper 服务地址(集群使用英文逗号分隔)
	 */
	private final String HOSTS = "192.168.10.170:40110";
	
	/**
	 * 连接实例
	 */
	private CuratorFramework client;
	
	/**
	 * baseSleepTimeMs: 初始的重试等待时间
	 * maxRetries: 最多重试次数
	 * maxSleepMs: 每次重试时的最大休眠时间
	 */
	RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3, 1000);
	
	@Before
	public void before() {
		Builder builder = CuratorFrameworkFactory
				.builder()
				.connectString(HOSTS)
				.retryPolicy(retryPolicy)
				.connectionTimeoutMs(CONNECTION_TIMEOUT)
				.sessionTimeoutMs(SESSION_TIMEOUT);
		client = builder.build();
		
		//启动
		client.start();
	}
	
	@After
	public void after() {
		if(client != null) {
			client.close();
		}
	}
	
	/**
	 * 测试创建节点
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testCreate() throws Exception {
		//创建永久节点
		client.create()
			.forPath("/curator", "/curator data".getBytes(Charsets.UTF_8));
		
		//创建永久有序节点
		client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL)
			.forPath("/curator_sequential", "/curator_sequential data".getBytes(Charsets.UTF_8));
		
		//创建临时节点
		client.create().withMode(CreateMode.EPHEMERAL)
			.forPath("/curator/ephemeral", "/curator/ephemeral data".getBytes(Charsets.UTF_8));
		
		//创建临时有序节点
		client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
			.forPath("/curator/ephemeral_path1", "/curator/ephemeral_path1 data".getBytes(Charsets.UTF_8));

		/**
		 * 创建临时有序节点时使用 withProtection() 方法。
		 * 
		 * 作用是在创建的节点名前面添加 GUID 标识，其目的是为了避免出现
		 * "节点创建成功，但是 Zookeeper 服务器在创建的节点名被返回给 client 前就出现了异常，从而导致临时节点没有被立即删除，
		 * 而 client 也没法判断哪些节点被创建成功（注：创建的临时节点名有一定随机性）" 的情况。
		 */
		client.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
			.forPath("/curator/ephemeral_path2", "/curator/ephemeral_path2 data".getBytes(Charsets.UTF_8));
	}
	
	/**
	 * 测试检查某个节点是否存在
	 * @throws Exception 
	 */
	@Test
	public void testCheck() throws Exception {
		Stat stat1 = client.checkExists().forPath("/curator");
		Stat stat2 = client.checkExists().forPath("/curator2");
		Stat stat3 = client.checkExists().forPath("/curator/ephemeral");
		
		System.out.println("'/curator' 是否存在：" + (stat1 != null ? true : false));
		System.out.println("'/curator2' 是否存在：" + (stat2 != null ? true : false));
		System.out.println("'/curator/ephemeral' 是否存在：" + (stat3 != null ? true : false));
	}
	
	/**
	 * 测试获取和设置节点数据
	 * @throws Exception 
	 */
	@Test
	public void testGetAndSet() throws Exception {
		//获取某个节点的所有子节点
		List<String> nodes = client.getChildren().forPath("/");
		System.out.println(nodes);
		
		//获取某个节点数据
		byte[] bytes = client.getData().forPath("/curator");
		System.out.println("'/curator' data is: " + new String(bytes));
		
		//设置某个节点数据
		client.setData().forPath("/curator", "abc".getBytes(Charsets.UTF_8));
		bytes = client.getData().forPath("/curator");
		System.out.println("'/curator' new data is: " + new String(bytes));
	}
	
	/**
	 * 测试异步设置节点数据
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testSetDataAsync() throws Exception {
		//创建监听器
		CuratorListener listener = new CuratorListener() {

			public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
				System.out.println(event.getPath());
			}
		};
		
		//添加监听器
		client.getCuratorListenable().addListener(listener);
		
		//异步设置某个节点数据
		client.setData().inBackground().forPath("/curator", "async data".getBytes(Charsets.UTF_8));
		
		//为了防止单元测试结束从而看不到异步执行结果，因此暂停10s
		Thread.sleep(10000);
	}
	
	/**
	 * 测试另一种异步执行获取通知的方式
	 * @throws Exception
	 */
	@Test
	public void testSetDataAsync2() throws Exception {
		BackgroundCallback callback = new BackgroundCallback() {
			
			public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
				System.out.println(event.getPath());
			}
		};
		
		//异步设置某个节点数据
		client.setData().inBackground(callback).forPath("/curator", "async2 data".getBytes(Charsets.UTF_8));
		
		//为了防止单元测试结束从而看不到异步执行结果，因此暂停10s
		Thread.sleep(10000);
	}
	
	/**
	 * 测试删除节点
	 * 
	 * 说明
	 * orSetData(): 如果节点存在则 Curator 将会使用给出的数据设置这个节点的值，相当与 setData() 方法。
	 * creatingParentContainersIfNeeded(): 如果指定节点的父节点不存在,则 Curator 将会自动级联创建父节点。
	 * guaranteed(): 如果服务端可能删除成功,但是 client 没有接收到删除成功的提示, Curator 将会在后台持续尝试删除该节点。
	 * deletingChildrenIfNeeded(): 如果待删除节点存在子节点，则 Curator 将会级联删除该节点的子节点。
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testDelete() throws Exception {
		//创建测试节点
		client.create().creatingParentContainersIfNeeded()
			.forPath("/curator/del_key1", "del_key1 data".getBytes(Charsets.UTF_8));

//		博客新版本API
//		client.create().orSetData().creatingParentContainersIfNeeded()
//			.forPath("/curator/del_key1", "del_key1 data".getBytes(Charsets.UTF_8));
		
		client.create().creatingParentContainersIfNeeded()
			.forPath("/curator/del_key2", "del_key2 data".getBytes(Charsets.UTF_8));
		
		client.create().forPath("/curator/del_key2/test_key", "test_key data".getBytes(Charsets.UTF_8));
		
		Stat stat1 = client.checkExists().forPath("/curator/del_key1");
		Stat stat2 = client.checkExists().forPath("/curator/del_key2");
		Stat stat3 = client.checkExists().forPath("/curator/del_key2/test_key");
		System.out.println("'/curator/del_key1' 是否存在：" + (stat1 != null ? true : false));
		System.out.println("'/curator/del_key2' 是否存在：" + (stat2 != null ? true : false));
		System.out.println("'/curator/del_key2/test_key' 是否存在：" + (stat3 != null ? true : false));
		System.out.println("开始删除...");
		
		//删除节点
		client.delete().forPath("/curator/del_key1");
		
		//级联删除子节点
		client.delete().guaranteed().deletingChildrenIfNeeded().forPath("/curator/del_key2");
		
		stat1 = client.checkExists().forPath("/curator/del_key1");
		stat2 = client.checkExists().forPath("/curator/del_key2");
		stat3 = client.checkExists().forPath("/curator/del_key2/test_key");
		System.out.println("'/curator/del_key1' 是否存在：" + (stat1 != null ? true : false));
		System.out.println("'/curator/del_key2' 是否存在：" + (stat2 != null ? true : false));
		System.out.println("'/curator/del_key2/test_key' 是否存在：" + (stat3 != null ? true : false));
	}
	
	/**
	 * 测试事务管理：碰到异常，事务会回滚
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testTransaction() throws Exception {
		
	}
	
	/**
	 * 测试命名空间
	 * 
	 * 为了避免多个应用的节点名称冲突的情况， CuratorFramework 提供了命名空间的概念。
	 * 具体做法是：CuratorFramework 会为它的 API 调用的节点路径的前面自动添加上命名空间。
	 * 命名空间本质上是从根节点开始的一个路径。
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testNamespace() throws Exception {
		CuratorFramework client2 = CuratorFrameworkFactory
				.builder()
				.namespace("testnamespace")
				.connectString(HOSTS)
				.sessionTimeoutMs(SESSION_TIMEOUT)
				.connectionTimeoutMs(CONNECTION_TIMEOUT)
				.retryPolicy(retryPolicy)
				.build();
		
		client2.start();
		
		// 在 namespace 下创建节点
		client2.create().creatingParentContainersIfNeeded()
			.forPath("/server1/method1", "some data".getBytes(Charsets.UTF_8));
		
		// 删除 namespace 下的节点
//		client2.delete().guaranteed().forPath("/method1");
//		client2.delete().guaranteed().forPath("/server1");
		
		client2.close();
	}
	
}
