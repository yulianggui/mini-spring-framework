package com.zhegui.mini.spring.formework.context;

/**
 * create by zhegui on 2018/12/23
 */
public abstract class ZGAbstractApplicationContext {

    //提供给子类重新的
    protected void onRefresh(){
    }

    protected abstract void refreshBeanFactory();
}
