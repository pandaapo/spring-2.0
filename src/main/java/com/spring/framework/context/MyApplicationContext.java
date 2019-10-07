package com.spring.framework.context;

import com.spring.framework.annotation.MyAutowired;
import com.spring.framework.annotation.MyController;
import com.spring.framework.annotation.MyService;
import com.spring.framework.beans.MyBeanFactory;
import com.spring.framework.beans.MyBeanWrapper;
import com.spring.framework.beans.config.MyBeanDefinition;
import com.spring.framework.beans.support.MyBeanDefinitionReader;
import com.spring.framework.beans.support.MyDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOC、DI、MVC、AOP
 */
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory {
    private String[] configLocations;
    private MyBeanDefinitionReader reader;
    //单例对象的IOC容器缓存
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    //通用的IOC容器
    private Map<String, MyBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

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
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    public Object getBean(String beanName) throws Exception {
        //分成初始化、注入前后两步的原因，因为要防止以下循环依赖的情形
        //class A {B b;}
        //class B {A a;}

        //1、初始化
        MyBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        MyBeanWrapper beanWrapper = instantiateBean(beanName, beanDefinition);

        //2、将创建出来的BeanWrapper存放到通用IOC容器中
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        //3、注入
        populateBean(beanName, new MyBeanDefinition(), beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, MyBeanDefinition myBeanDefinition, MyBeanWrapper myBeanWrapper) {
        Object instance = myBeanWrapper.getWrappedInstance();

        Class<?> clazz = myBeanWrapper.getWrappedClass();
        //判断：只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(MyController.class) || clazz.isAnnotationPresent(MyService.class))){
            return;
        }

        //获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(MyAutowired.class)) {continue;}
            //该项目关于注入的注解只定义了MyAutowired
            MyAutowired autowired = field.getAnnotation(MyAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            //注解没写value时，用类型注入
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                //下面这行代码是为了临时解决NullPointerException，原因有待排查？？？
                if(null == this.factoryBeanInstanceCache.get(autowiredBeanName)) {continue;}
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private MyBeanWrapper instantiateBean(String beanName, MyBeanDefinition myBeanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = myBeanDefinition.getBeanClassName();
        //2、反射实例化，得到一个对象
        Object intance = null;
        try {
            if(this.singletonObjects.containsKey(className)){
                intance = this.singletonObjects.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                intance = clazz.newInstance();
                //保存了两种：这样可以根据className注入，也可以根据beanName注入
                this.singletonObjects.put(className, intance);
                this.singletonObjects.put(myBeanDefinition.getFactoryBeanName(), intance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //3、把这个对象封装到BeanWrapper中
        //源码中，单例对象存放在DefaultSingletonBeanRegistry的singletonObjects（不确定是否是这个）
        //源码中，BeanWrapper存放在AbstractAutowireCapableBeanFactory的factoryBeanInstanceCache中
        MyBeanWrapper beanWrapper = new MyBeanWrapper(intance);

        //4、把BeanWrapper存放到IOC容器中
        return beanWrapper;
    }
}
