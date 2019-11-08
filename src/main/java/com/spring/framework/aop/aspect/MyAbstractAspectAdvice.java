package com.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 该抽象类是将MyMethodBeforeAdviceInterceptor、MyAfterReturningAdviceInterceptor、MyAfterThrowingAdviceInterceptor3个子类的共同逻辑抽象出来
 */
public abstract class MyAbstractAspectAdvice {

    private Method aspectMethod;

    private Object aspectTarget;

    public MyAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    //上述3个子类都通过该方法调用对应的织入代码。（织入代码：即要往业务代码中织入的代码）
    public Object invokeAdviceMethod(MyJoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable {
        Class<?> [] paramTypes = this.aspectMethod.getParameterTypes();
        //织入的代码没有参数时
        if(null == paramTypes || paramTypes.length == 0){
            return this.aspectMethod.invoke(aspectTarget);
        }
        //织入的代码有参数时
        else {
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                if(paramTypes[i] == MyJoinPoint.class) {
                    args[i] = joinPoint;
                } else if (paramTypes[i] == Throwable.class) {
                    args[i] = tx;
                } else if (paramTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }
}
