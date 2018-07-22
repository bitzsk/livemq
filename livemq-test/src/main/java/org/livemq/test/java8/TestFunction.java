package org.livemq.test.java8;

import java.util.function.Function;

import org.junit.Test;

/**
 * 测试 Java 8 函数式编程
 * @Title TestConsumer
 * @Package org.livemq.test.java8
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-22 10:29
 * @version 1.0.0
 * @see https://blog.csdn.net/wangnan537/article/details/49102877
 * @see java.util.function.Function<T, R>: https://blog.csdn.net/huo065000/article/details/78964382
 * @see java.util.function.Predicate<T>: https://blog.csdn.net/huo065000/article/details/78964875
 */
public class TestFunction {

	@Test
	public void testFunction() {
		User u = new User("Jack", 20);

		Function<User, Integer> f1 = e -> e.age;
		Integer age = f1.apply(u);
		System.out.println(age);
		
		MyFunction<User> f2 = new MyFunction<>();
		String str = f2.apply(u);
		System.out.println(str);
		
		Function<User, String> f3 = user -> user.name + ":" + user.age;
		f3.andThen(user -> user.name)
		
	}
	
	@Test
	public void testPredicateAndConsumer() {
		User u = new User("Jack", 20);
		System.out.println(u); // User [name=Jack, age=20]
		u = u.update(u,
				// Lambda expression for Predicate interface
				user -> user.age > 18, 
				// Lambda expression for Consumer interface
				user -> user.age = 18);
		System.out.println(u); // User [name=Jack, age=18]
	}
}
