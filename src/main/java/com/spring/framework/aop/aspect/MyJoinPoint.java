package com.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 代表被织入的类/被代理类：即业务代码类
 */
public interface MyJoinPoint {

    //原生对象
    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
