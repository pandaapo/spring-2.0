package com.spring.framework.aop.intercept;

//组成执行链的标准
public interface MyMethodInterceptor {

    Object invoke(MyMethodInvocation invocation) throws Throwable;
}
