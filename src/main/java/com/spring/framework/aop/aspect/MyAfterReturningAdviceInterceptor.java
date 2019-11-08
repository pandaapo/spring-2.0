package com.spring.framework.aop.aspect;

import com.spring.framework.aop.intercept.MyMethodInterceptor;
import com.spring.framework.aop.intercept.MyMethodInvocation;

import java.lang.reflect.Method;

public class MyAfterReturningAdviceInterceptor extends
        MyAbstractAspectAdvice implements MyAdvice, MyMethodInterceptor {

    private MyJoinPoint joinPoint;

    public MyAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MyMethodInvocation mi) throws Throwable {
        Object returnVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(returnVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return returnVal;
    }

    //调用织入的代码
    private void afterReturning(Object returnVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, returnVal, null);
    }
}
