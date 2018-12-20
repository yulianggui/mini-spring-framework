package com.zhegui.mini.spring.formework.beans;

/**
 * create by zhegui on 2018/12/16
 */

/**
 * 用来存储配置文件中的信息
 *  相当于是保存在内存中的配置
 */
public class BeanDefinition {

    private String beanClassName;

    private boolean lazyInit = false;

    private String factoryBeanName;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
