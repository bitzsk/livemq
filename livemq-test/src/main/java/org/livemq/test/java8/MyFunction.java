package org.livemq.test.java8;

import java.util.function.Function;

public class MyFunction<T> implements Function<T, String> {

	@Override
	public String apply(T t) {
		return t.toString();
	}

}
