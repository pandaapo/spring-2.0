package com.spring.framework.aop.intercept;

//���ִ�����ı�׼
public interface MyMethodInterceptor {

    Object invoke(MyMethodInvocation invocation) throws Throwable;
}
