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
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replace("\\.","/"));
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

    //把配置文件中定义的扫描到的每一个配置信息（？不是扫描出来的class吗？）解析成一个个MyBeanDefinition对象，为了之后IOC操作方便
    public List<MyBeanDefinition> loadBeanDefinitions(){
        List<MyBeanDefinition> result = new ArrayList<MyBeanDefinition>();
        for (String className: registryBeanClasses) {
            MyBeanDefinition beanDefinition = doCreateBeanDefinition(className);
            if(null == beanDefinition) {
                continue;
            }
            result.add(beanDefinition);
        }
        return null;
    }

    private MyBeanDefinition doCreateBeanDefinition(String className){
        try {
            Class<?> beanClass = Class.forName(className);
            //有可能是一个接口（接口是不能被初始化的，所以需要用它的实现类作为beanClassName）
            if(beanClass.isInterface()){
                return null;
            }
            MyBeanDefinition beanDefinition = new MyBeanDefinition();
            beanDefinition.setBeanClassName(className);
            //java Class.getSimpleName() 得到类的简写名称，比如对本类而言，获取的就是“MyBeanDefinitionReader”
            beanDefinition.setFactoryBeanName(beanClass.getSimpleName());
            return beanDefinition;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
