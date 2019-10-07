package com.spring;

import com.spring.framework.context.MyApplicationContext;

public class Test {
    public static void main(String[] args) {
        //创建容器
        MyApplicationContext applicationContext = new MyApplicationContext("classpath:application.properties");
        try {
            Object object = applicationContext.getBean("myAction");
            System.out.println(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(applicationContext);
    }
}
