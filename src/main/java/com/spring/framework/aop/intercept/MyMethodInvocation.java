package com.spring.framework.aop.intercept;

import java.lang.reflect.Method;
import java.util.List;

//�ο���Դ���е�ReflectiveMethodInvocation��Ҳ������ΪԴ����MethodInvocation
public class MyMethodInvocation {
    public MyMethodInvocation(
            Object proxy, Object target, Method method, Object[] arguments,
            Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers){

    }

    public Object proceed() throws Throwable {
        return null;
     }
}
