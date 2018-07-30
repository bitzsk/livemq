package org.livemq.test.service;

import org.livemq.api.service.Listener;

public class Registry extends ZKService {

	@Override
	public void start(Listener listener) {
		if(isRunning()) {
			listener.onSuccess();
		}else {
			super.start(listener);
		}
	}
	
	@Override
	public void stop(Listener listener) {
		if(isRunning()) {
			super.stop(listener);
		}else {
			listener.onSuccess();
		}
	}
}
