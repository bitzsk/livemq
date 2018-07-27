package org.livemq.test.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Node {
	private static final Logger logger = LoggerFactory.getLogger(Node.class);
	
	// 节点名称
	private String name;
	// 下一个节点的引用
	private Node next;

	public Node(String name) {
		if(name == null) {
			name = this.getClass().getSimpleName();
		}
		this.name = name;
	}

	/**
	 * First do something and then do start next
	 */
	protected abstract void start();
	
	/**
	 * First do stop next and then do something
	 */
	protected abstract void stop();

	public void startNext() {
		if (next != null) {
			logger.info("server [{}] startting...", getNextName());
			next.start();
		}
	}

	public void stopNext() {
		if (next != null) {
			next.stop();
			logger.info("server [{}] stopped.", getNextName());
		}
	}

	/**
	 * 增加一个节点
	 * 
	 * @param node
	 */
	public void add(Node node) {
		if (next == null) { // 如果下个节点不存在，则就添加到这里
			next = node;
		} else { // 反之则继续进行递归的添加
			next.add(node);
		}
	}

	/**
	 * 是否包含指定节点
	 * 
	 * @param data
	 * @return
	 */
	public boolean contains(String name) {
		if (next == null) {
			return false;
		} else {
			if (next.name.equals(name)) {
				return true;
			} else {
				return next.contains(name);
			}
		}
	}

	/**
	 * 删除一个节点
	 * 
	 * @param data
	 */
	public void remove(String name) {
		if (next == null)
			return;

		if (next.name.equals(name)) { // 如果下一个节点是要删除的节点
			next = next.next; // 那么则将下一个节点的引用指向下下一个节点，next.next 有可能为 null
		} else {
			next.remove(name);
		}
	}
	
	public Node getNext() {
		return next;
	}
	
	public String getName() {
		return name;
	}
	
	public String getNextName(){
		return next == null ? null : next.getName();
	}

}
