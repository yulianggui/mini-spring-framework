package com.zhegui.mini.spring.formework.core;

/**
 * create by zhegui on 2018/12/16
 */
public interface BeanFactory {

    /**
     * 从IOC容器中获得一个实例Bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName);

}


