package com.spring.framework.aop.intercept;

import java.lang.reflect.Method;
import java.util.List;

//参考了源码中的ReflectiveMethodInvocation；也可以作为源码中MethodInvocation
public class MyMethodInvocation {
    public MyMethodInvocation(
            Object proxy, Object target, Method method, Object[] arguments,
            Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers){

    }

    public Object proceed() throws Throwable {
        return null;
     }
}
