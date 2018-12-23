package com.zhegui.mini.spring.formework.context;

import com.zhegui.mini.spring.formework.beans.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * create by zhegui on 2018/12/23
 */
public class ZGDefaultListableBeanFactory extends ZGAbstractApplicationContext{

    /**
     * 用来保存配置信息
     */
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    protected void refreshBeanFactory() {

    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
    }
}
