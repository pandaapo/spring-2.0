package com.spring.framework.beans.support;

import com.spring.framework.beans.config.MyBeanDefinition;
import com.spring.framework.context.support.MyAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放IOC容器
 * 疑问：源码中DefaultListableBeanFactory和AbstractApplicationContext并没父子关系。
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext {
    //存储注册信息的BeanDefinition
    protected final Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, MyBeanDefinition>();
}
