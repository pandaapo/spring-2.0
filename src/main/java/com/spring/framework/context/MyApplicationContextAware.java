package com.spring.framework.context;

public interface MyApplicationContextAware {

    /**
     * 通过解耦方式获得IOC容器的顶层设计
     * 后面将通过一个监听器去扫描所有类，只要实现了此接口，将自动调用setApplicationContext方法，把IOC容器注入到目标类中
     * @param applicationContext
     */
    void setApplicationContext(MyApplicationContext applicationContext);
}
