package com.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * 请求url
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    String value() default "";
}
