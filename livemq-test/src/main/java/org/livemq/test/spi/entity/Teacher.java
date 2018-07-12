package org.livemq.test.spi.entity;

import org.livemq.api.spi.Spi;

@Spi(order = 1)
public class Teacher implements Person {

	@Override
	public void eat() {
		System.out.println("teacher eat");
	}

}
