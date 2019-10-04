package com.spring.framework.context;

import com.spring.framework.beans.MyBeanFactory;
import com.spring.framework.beans.MyBeanWrapper;
import com.spring.framework.beans.config.MyBeanDefinition;
import com.spring.framework.beans.support.MyBeanDefinitionReader;
import com.spring.framework.beans.support.MyDefaultListableBeanFactory;

import java.util.List;
import java.util.Map;

/**
 * IOC、DI、AOP、MVC
 */
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory {
    private String[] configLocations;
    private MyBeanDefinitionReader reader;

    public MyApplicationContext(String... configLocations){
        this.configLocations = configLocations;
        refresh();
    }

    @Override
    public void refresh() {
        //1、定位，定位配置文件
        reader = new MyBeanDefinitionReader(this.configLocations);

        //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<MyBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        
        //3、注册、把配置信息放到容器里面（“伪IOC容器”）
        doRegistryBeanDefinition(beanDefinitions);

        //4、注入：把不是延时加载的类，提前初始化
        doAutowrited();
    }

    //只处理非延时加载的类
    private void doAutowrited() {
        for (Map.Entry<String, MyBeanDefinition> beanDefinitionEntry:super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }
    }

    private void doRegistryBeanDefinition(List<MyBeanDefinition> beanDefinitions) {
        for (MyBeanDefinition beanDefinition :beanDefinitions) {
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    /**
     * 是依赖注入的一个入口
     * @param beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) {
        //分成初始化、注入前后两步的原因，因为要防止以下循环依赖的情形
        //class A {B b;}
        //class B {A a;}

        //1、初始化
        instantiateBean(beanName, new MyBeanDefinition());

        //2、注入
        populateBean(beanName, new MyBeanDefinition(), new MyBeanWrapper());
        return null;
    }

    private void populateBean(String beanName, MyBeanDefinition myBeanDefinition, MyBeanWrapper myBeanWrapper) {
    }

    private void instantiateBean(String beanName, MyBeanDefinition myBeanDefinition) {
    }
}
