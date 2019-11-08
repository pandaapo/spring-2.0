package com.spring.framework.aop.aspect;

import com.spring.framework.aop.intercept.MyMethodInterceptor;
import com.spring.framework.aop.intercept.MyMethodInvocation;

import java.lang.reflect.Method;

public class MyMethodBeforeAdviceInterceptor  extends MyAbstractAspectAdvice implements MyAdvice, MyMethodInterceptor {

    //保存MyJoinPoint
    private MyJoinPoint joinPoint;

    public MyMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object[] args, Object target) throws Throwable {
        //即执行LogAspect中的before方法
        super.invokeAdviceMethod(this.joinPoint, null, null);
    }

    @Override
    public Object invoke(MyMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到before的入参，被织入的代码即JoinPoint。（被织入的代码：即业务代码。）
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
