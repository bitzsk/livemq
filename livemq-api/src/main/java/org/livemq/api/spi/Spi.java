package org.livemq.api.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Spi {

	/**
	 * SPI name
	 * 
	 * @return
	 */
	String value() default "";
	
	/**
	 * SPI 排序
	 * 
	 * @return
	 */
	int order() default 0;
}
