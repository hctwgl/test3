package com.ald.fanbei.api.web.validator.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * default value
 * @author rongbo
 *
 */
@Target({ FIELD})
@Retention(RUNTIME)
@Documented
public @interface Default {
	String value() default "";
}
