package com.spring.framework.aop.support;

import java.lang.reflect.Method;
import java.util.List;

//Դ���е�AdvisedSupport
public class MyAdvisedSupport {
    private Class<?> targetClass;

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public Object getTarget(){
        return null;
    }

    //��ȡ����֪ͨ/���Ӻ��ִ����
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass){
        return null;
    }
}
