package org.livemq.zk;

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
public class Zookeeper {

	private static volatile Zookeeper instance;
	
	public static Zookeeper getInstance() {
		if(instance == null) {
			synchronized (ZKServiceRegistryAndDiscovery.class) {
				if(instance == null) {
					instance = new Zookeeper();
				}
			}
		}
		return instance;
	}
	
	private Zookeeper() {}
	
	
}
