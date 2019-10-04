package com.spring.framework.beans;

public class MyBeanWrapper {

    //如果是单例，直接获取
    Object getWrappedInstance() {
        return null;
    }

    //如果不是单例，可以每次new出来
    Class<?> getWrappedClass() {
        return null;
    }
}
