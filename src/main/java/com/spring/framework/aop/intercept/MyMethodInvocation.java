package com.spring.framework.aop.intercept;

import com.spring.framework.aop.aspect.MyJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//参考了源码中的ReflectiveMethodInvocation；也可以作为源码中MethodInvocation
public class MyMethodInvocation implements MyJoinPoint {

    private Object proxy;

    private Object target;

    private Method method;

    private Object[] arguments;

    private Class<?> targetClass;

    //保存执行链
    private List<Object> interceptorsAndDynamicMethodMatchers;

    //保存自定义的属性
    private Map<String, Object> userAttributes;

    //定义一个索引，从-1开始来记录当前拦截器/执行链执行的位置
    private int currentInterceptorIndex = -1;

    public MyMethodInvocation(
            Object proxy, Object target, Method method, Object[] arguments,
            Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers){
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.targetClass = targetClass;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    //执行执行链中每个方法
    public Object proceed() throws Throwable {
        //如果执行链Interceptor执行完了，则执行自己
        if(this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1){
            return this.method.invoke(this.target, this.arguments);
        }
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        //如果动态匹配到joinPoint
        if(interceptorOrInterceptionAdvice instanceof MyMethodInterceptor) {
            MyMethodInterceptor mi = (MyMethodInterceptor)interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        } else {
            //动态匹配失败时，略过当前的Interceptor，调用下一个Interceptor
            return proceed();
        }
     }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if(value != null) {
            if(this.userAttributes == null){
                this.userAttributes = new HashMap<>();
            }
            this.userAttributes.put(key, value);
        } else {
            if(this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }
}
