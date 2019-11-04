package com.spring.demo.aspect;

public class LogAspect {
    public void before(){
        //记录调用的开始时间
    }

    public void after(){
        //记录方法执行的时间：当前时间-开始时间
    }

    public void afterThrowing(){
        //异常监测，获取异常的信息
    }
}
