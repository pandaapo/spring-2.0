package com.spring.framework.beans;

public class MyBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public MyBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    //如果是单例，直接获取
    public Object getWrappedInstance() {
        return this.wrappedInstance;
    }

    //如果不是单例，可以每次new出来。返回代理以后的Class。
    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }
}
