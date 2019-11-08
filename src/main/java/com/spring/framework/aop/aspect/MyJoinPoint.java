package com.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * ����֯�����/�������ࣺ��ҵ�������
 */
public interface MyJoinPoint {

    //ԭ������
    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
