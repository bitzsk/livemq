package org.livemq.internal.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface NIOHandle {

	public void accept(SelectionKey key) throws IOException;
	
	public void connect(SelectionKey key);
	
	public void read(SelectionKey key);

	public void write(SelectionKey key);
}
