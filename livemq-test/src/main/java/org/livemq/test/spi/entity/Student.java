package org.livemq.test.spi.entity;

import org.livemq.api.spi.Spi;

@Spi(order = 2)
public class Student implements Person {

	@Override
	public void eat() {
		System.out.println("student eat");
	}

}
