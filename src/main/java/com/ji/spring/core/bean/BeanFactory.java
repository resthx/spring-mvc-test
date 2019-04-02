package com.ji.spring.core.bean;

public interface BeanFactory {
    void setBean(Object o);
    Object getBean(String beanName);
    void setBeanByName(String str,Object o);
}
