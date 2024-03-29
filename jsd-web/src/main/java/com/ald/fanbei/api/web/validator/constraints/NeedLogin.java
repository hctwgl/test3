package com.ald.fanbei.api.web.validator.constraints;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * need login
 * @author rongbo
 *
 */
@Target({ TYPE})
@Retention(RUNTIME)
@Documented
public @interface NeedLogin {
}
