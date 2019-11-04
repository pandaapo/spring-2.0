package com.spring.framework.aop.support;

import java.lang.reflect.Method;
import java.util.List;

//源码中的AdvisedSupport
public class MyAdvisedSupport {
    private Class<?> targetClass;

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public Object getTarget(){
        return null;
    }

    //获取加入通知/增加后的执行链
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass){
        return null;
    }
}
