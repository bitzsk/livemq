package org.livemq.internal.stream;

import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream extends InputStream {

	private InputStream in;
	private int counter;

	/**
	 * Constructs a new <code>CountingInputStream</code> wrapping the supplied
	 * input stream.
	 */
	public CountingInputStream(InputStream in) {
		this.in = in;
		this.counter = 0;
	}
	
	public int read() throws IOException {
		int i = in.read();
		if (i != -1) {
			counter++;
		}
		return i;
	}

	/**
	 * Returns the number of bytes read since the last reset.
	 */
	public int getCounter() {
		return counter;
	}
	
	/**
	 * Resets the counter to zero.
	 */
	public void resetCounter() {
		counter = 0;
	}
	
}
