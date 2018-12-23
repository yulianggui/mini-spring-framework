package com.zhegui.mini.spring.formework.beans;

/**
 * create by zhegui on 2018/12/17
 */

/**
 * Bean 的事件监听
 */
public class ZGBeanPostProcessor {

    /**
     * bean wrapper初始化之前
     * @param bean
     * @param beanName
     * @return
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName){
        System.out.println("ZGBeanPostProcessor.postProcessBeforeInitialization ...");
        return bean;
    }

    /**
     * bean wrapper初始化之后
     * @param bean
     * @param beanName
     * @return
     */
    public Object postProcessAfterInitialization(Object bean, String beanName){
        System.out.println("ZGBeanPostProcessor.postProcessAfterInitialization ...");
        return bean;
    }

}
