package org.livemq.test.spi;

import org.livemq.api.spi.SpiLoader;
import org.livemq.test.spi.entity.Person;

public class TestSpi {

	public static void main(String[] args) {
		Person person = SpiLoader.load(Person.class);
		person.eat();
	}
}
