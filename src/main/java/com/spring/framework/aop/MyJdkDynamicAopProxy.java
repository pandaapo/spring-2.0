package com.spring.framework.aop;

import com.spring.framework.aop.intercept.MyMethodInvocation;
import com.spring.framework.aop.support.MyAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class MyJdkDynamicAopProxy implements MyAopProxy, InvocationHandler {
    private MyAdvisedSupport config;

    public MyJdkDynamicAopProxy(MyAdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return this.getProxy(this.config.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        //返回代理对象。利用classLoader进行字节码重组，生成新的类，对原有类进行增强
        return Proxy.newProxyInstance(classLoader,this.config.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = this.config.getInterceptorsAndDynamicInterceptionAdvice(method, this.config.getTargetClass());
        MyMethodInvocation invocation = new MyMethodInvocation(proxy, this.config.getTarget(), method, args, this.config.getTargetClass(), interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
