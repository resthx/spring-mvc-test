package com.ji.spring.core.bean;

import java.util.HashMap;

/**
 * @BelongsProject: 手写springmvc框架练习
 * @BelongsPackage: com.ji.springmvc.bean
 * @Author: JiLiugang
 * @CreateTime: 2019-03-23 11:27
 * @Description:
 */
public class SpringMvcBeanFactory extends HashMap implements BeanFactory{

    //单例
    private static BeanFactory springMvcBeanFactory;
    private void setSpringMvcBeanFactory(){};
    public synchronized static BeanFactory getBeanFactory(){
        if (springMvcBeanFactory == null){
            springMvcBeanFactory = new SpringMvcBeanFactory();
        }
        return springMvcBeanFactory;
    }
    @Override
    public void setBean(Object object) {
        String name = object.getClass().getSimpleName();
        name = firstWordToLowerCase(name);
        this.put(name, object);
    }
    @Override
    public void setBeanByName(String name,Object object){
        this.put(name, object);
    }


    @Override
    public Object getBean(String beanName) {
        return this.get(beanName);
    }
    //首字母小写
    public  static  String firstWordToLowerCase(String str){
        String str_ = str.substring(0,1).toLowerCase()+str.substring(1);
        return str_;
    }
}
