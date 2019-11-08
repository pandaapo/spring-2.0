package com.spring.demo.aspect;

import com.spring.framework.aop.aspect.MyJoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class LogAspect {

    //�ڵ���һ������֮ǰ��ִ��before����
    public void before(MyJoinPoint joinPoint){
        //�Զ�������߼��������¼���õĿ�ʼʱ��
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());
        log.info("Invoker Before Method!!!" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    //�ڵ���һ������֮��ִ��after����
    public void after(MyJoinPoint joinPoint){
        //��¼����ִ�е�ʱ�䣺��ǰʱ��-��ʼʱ��
        log.info("Invoker After Method!!!" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long)joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));
    }

    public void afterThrowing(MyJoinPoint joinPoint, Throwable ex){
        //�쳣��⣬��ȡ�쳣����Ϣ
        log.info("�����쳣" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());
    }
}
