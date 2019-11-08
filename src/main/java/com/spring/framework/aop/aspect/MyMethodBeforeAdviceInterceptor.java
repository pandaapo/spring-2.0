package com.spring.framework.aop.aspect;

import com.spring.framework.aop.intercept.MyMethodInterceptor;
import com.spring.framework.aop.intercept.MyMethodInvocation;

import java.lang.reflect.Method;

public class MyMethodBeforeAdviceInterceptor  extends MyAbstractAspectAdvice implements MyAdvice, MyMethodInterceptor {

    //����MyJoinPoint
    private MyJoinPoint joinPoint;

    public MyMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object[] args, Object target) throws Throwable {
        //��ִ��LogAspect�е�before����
        super.invokeAdviceMethod(this.joinPoint, null, null);
    }

    @Override
    public Object invoke(MyMethodInvocation mi) throws Throwable {
        //�ӱ�֯��Ĵ����в����õ�before����Σ���֯��Ĵ��뼴JoinPoint������֯��Ĵ��룺��ҵ����롣��
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
