package com.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * �ó������ǽ�MyMethodBeforeAdviceInterceptor��MyAfterReturningAdviceInterceptor��MyAfterThrowingAdviceInterceptor3������Ĺ�ͬ�߼��������
 */
public abstract class MyAbstractAspectAdvice {

    private Method aspectMethod;

    private Object aspectTarget;

    public MyAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    //����3�����඼ͨ���÷������ö�Ӧ��֯����롣��֯����룺��Ҫ��ҵ�������֯��Ĵ��룩
    public Object invokeAdviceMethod(MyJoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable {
        Class<?> [] paramTypes = this.aspectMethod.getParameterTypes();
        //֯��Ĵ���û�в���ʱ
        if(null == paramTypes || paramTypes.length == 0){
            return this.aspectMethod.invoke(aspectTarget);
        }
        //֯��Ĵ����в���ʱ
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
