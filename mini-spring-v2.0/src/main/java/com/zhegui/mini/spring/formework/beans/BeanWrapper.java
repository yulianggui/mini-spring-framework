package com.zhegui.mini.spring.formework.beans;

import com.zhegui.mini.spring.formework.aop.ZGAopConfig;
import com.zhegui.mini.spring.formework.aop.ZGAopProxy;
import com.zhegui.mini.spring.formework.core.FactoryBean;

/**
 * create by zhegui on 2018/12/16
 */
public class BeanWrapper extends FactoryBean{

    private ZGAopProxy aopProxy = new ZGAopProxy();

    /**
     * 用作事件响应
     */
    private ZGBeanPostProcessor postProcessor;

    /**
     * 被保包装过的对象
     */
    private Object wrapperInstance;

    /**
     * 原始的通过反射new出来，保存原始的
     */
    private Object originalInstance;

    public BeanWrapper(Object object){
        //通过aopProxy产生一个代理对象
        this.wrapperInstance = aopProxy.getProxy(object);
        this.originalInstance = object;
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public Object getOriginalInstance() {
        return originalInstance;
    }

    /**
     * 获取被包装过的class
     *    -- 即代理后的Class
     *    $Proxy0
     * @return
     */
    public Class<?> getWrapperClass(){
        return this.wrapperInstance.getClass();
    }

    public ZGBeanPostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(ZGBeanPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    //将aopConfig往下传
    public void setAopConfig(ZGAopConfig aopConfig){
        this.aopProxy.setConfig(aopConfig);
    }
}
