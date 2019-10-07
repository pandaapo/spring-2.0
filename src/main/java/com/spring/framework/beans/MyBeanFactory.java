package com.spring.framework.beans;

/**
 * 单例工厂的顶层设计
 */
public interface MyBeanFactory {
    /**
     * 根据beanName从IOC容器中获取一个实例bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName) throws Exception;
}
