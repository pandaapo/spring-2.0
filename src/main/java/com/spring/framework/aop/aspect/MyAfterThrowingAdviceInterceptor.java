package com.spring.framework.aop.aspect;

import com.spring.framework.aop.intercept.MyMethodInterceptor;
import com.spring.framework.aop.intercept.MyMethodInvocation;

import java.lang.reflect.Method;

public class MyAfterThrowingAdviceInterceptor extends MyAbstractAspectAdvice implements MyAdvice, MyMethodInterceptor {

    // �����쳣����
    private String throwingName;

    public MyAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MyMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (Throwable e) {
            invokeAdviceMethod(mi, null, e.getCause());
            throw e;
        }
    }

    //��ȡ�쳣������
    public void setThrowName(String throwName){
        this.throwingName = throwName;
    }
}
