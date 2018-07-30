package org.livemq.test.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.livemq.api.service.Listener;
import org.livemq.api.service.Service;

public abstract class ZKService implements Service {

	private static AtomicBoolean state = new AtomicBoolean(false);
	
	@Override
	public void start(Listener listener) {
		init();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		state.set(true);
		listener.onSuccess();
	}

	@Override
	public void stop(Listener listener) {
		System.out.println("正在关闭中 ...");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		state.set(false);
		listener.onSuccess();
	}

	@Override
	public void init() {
		System.out.println("init");
	}

	@Override
	public boolean isRunning() {
		return state.get();
	}

}
