package org.livemq.api.service;

/**
 * 
 * @Title Listener
 * @Package org.livemq.api.service
 * @Description TODO
 * @author xinxisimple@163.com
 * @date 2018-07-30 11:30
 * @version 1.0.0
 */
public interface Listener {

	void onSuccess(Object ... args);

    void onFailure(Throwable cause);
}
