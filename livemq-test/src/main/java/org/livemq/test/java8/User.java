package org.livemq.test.java8;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class User {

	String name;
	int age;

	public User(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}
	
	/**
	 * 测试 Java8 函数式编程<br><br>
	 * 
	 * Predicate 和 Consumer 接口的 test() 和 accept() 方法都接受一个泛型参数。
	 * 不同的是 test() 方法进行某些逻辑判断并返回一个 boolean 值，而 accept() 接受并改变某个对象的内部值。
	 * 
	 * @param user
	 * @param predicate
	 * @param consumer
	 * @return
	 */
	public User update(User user, Predicate<User> predicate, Consumer<User> consumer) {
		if(predicate.test(user)) {
			consumer.accept(user);
		}
		return user;
	}
	
	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + "]";
	}

}
