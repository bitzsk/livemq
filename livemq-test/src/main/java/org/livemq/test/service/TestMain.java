package org.livemq.test.service;

import org.junit.Test;
import org.livemq.api.service.Listener;

public class TestMain {

	@Test
	public void test() {
		new Registry().start(new Listener() {
			
			@Override
			public void onSuccess(Object... args) {
				System.out.println("Registry started.");
			}
			
			@Override
			public void onFailure(Throwable cause) {
				// TODO Auto-generated method stub
				
			}
		});
		
		new Discovery().start(new Listener() {
			
			@Override
			public void onSuccess(Object... args) {
				System.out.println("Discovery started.");
			}
			
			@Override
			public void onFailure(Throwable cause) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
