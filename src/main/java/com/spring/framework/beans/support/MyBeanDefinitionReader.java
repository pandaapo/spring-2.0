package com.spring.framework.beans.support;

import com.spring.framework.beans.config.MyBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MyBeanDefinitionReader {
    //存放需要注册的bean
    private List<String> registryBeanClasses = new ArrayList<>();

    private Properties config = new Properties();

    public Properties getConfig(){
        return this.config;
    }

    //固定配置文件中key，相当于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";

    public MyBeanDefinitionReader(String... configLocations) {
        //通过url定位找到其所对应的文件，转换为文件流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(configLocations[0].replace("classpath:",""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        //将配置文件中scanpackage转换为文件路径，实际上就是把.替换成/
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            } else {
                if(!file.getName().endsWith(".class")){
                    continue;
                }
                String className = scanPackage + "." + file.getName().replace(".class","");
                registryBeanClasses.add(className);
            }
        }
    }

    public List<MyBeanDefinition> loadBeanDefinitions(){
        List<MyBeanDefinition> result = new ArrayList<MyBeanDefinition>();
        try {
            for (String className: registryBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                //如果是一个接口，是不能实例化的，需要用它的实现类来实例化
                if(beanClass.isInterface()) {continue;}

                //beanName有三种情况：1默认是类名首字母小写，2自定义名字，3接口注入（？？？）
                //java Class.getSimpleName() 得到类的简写名称（不包含包路径），比如对本类而言，获取的就是“MyBeanDefinitionReader”；Class.getName()得到类名称（包含路径）
                MyBeanDefinition beanDefinition = doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName());
                result.add(beanDefinition);
//                //又保存一种BeanDefinition，为了以后既可以根据？？？getBean，也可以根据类名getBean。但是这种写法会导致初始化两次，应该在MyDispatchServlet的initHandlerMapping中做一次去重。
//                result.add(doCreateBeanDefinition(beanClass.getName(), beanClass.getName()));

                Class<?> [] interfaces = beanClass.getInterfaces();
                for (Class<?> i: interfaces) {
                    //如果是多个实现类，只能覆盖。Spring源码也是这样。
                    //这个时候可以自定义名字
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //把配置文件中定义的扫描到的每一个配置信息（？不是扫描出来的class吗？）解析成一个个MyBeanDefinition对象，为了之后IOC操作方便
    private MyBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName){
        MyBeanDefinition beanDefinition = new MyBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    //这里默认了传入参数是字母，且首字母是大写
    private String toLowerFirstCase(String simpleName){
        char[] chars = simpleName.toCharArray();
        //大小写字母的ASCII码相差32
        //而且大写字母的ASCII码要小于小写字母的ASCII码
        //java中，对char做算术运算，实际上就是对ASCII码进行运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
