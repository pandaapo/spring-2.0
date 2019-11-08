package com.spring.framework.aop.intercept;

import com.spring.framework.aop.aspect.MyJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//�ο���Դ���е�ReflectiveMethodInvocation��Ҳ������ΪԴ����MethodInvocation
public class MyMethodInvocation implements MyJoinPoint {

    private Object proxy;

    private Object target;

    private Method method;

    private Object[] arguments;

    private Class<?> targetClass;

    //����ִ����
    private List<Object> interceptorsAndDynamicMethodMatchers;

    //�����Զ��������
    private Map<String, Object> userAttributes;

    //����һ����������-1��ʼ����¼��ǰ������/ִ����ִ�е�λ��
    private int currentInterceptorIndex = -1;

    public MyMethodInvocation(
            Object proxy, Object target, Method method, Object[] arguments,
            Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers){
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.targetClass = targetClass;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    //ִ��ִ������ÿ������
    public Object proceed() throws Throwable {
        //���ִ����Interceptorִ�����ˣ���ִ���Լ�
        if(this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1){
            return this.method.invoke(this.target, this.arguments);
        }
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        //�����̬ƥ�䵽joinPoint
        if(interceptorOrInterceptionAdvice instanceof MyMethodInterceptor) {
            MyMethodInterceptor mi = (MyMethodInterceptor)interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        } else {
            //��̬ƥ��ʧ��ʱ���Թ���ǰ��Interceptor��������һ��Interceptor
            return proceed();
        }
     }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if(value != null) {
            if(this.userAttributes == null){
                this.userAttributes = new HashMap<>();
            }
            this.userAttributes.put(key, value);
        } else {
            if(this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }
}
