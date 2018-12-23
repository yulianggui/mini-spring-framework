package com.zhegui.mini.spring.formework.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * create by zhegui on 2018/12/23
 */
public class ZGAopProxyUtils {

    public static Object getTargetObject(Object proxy) throws Exception {

        //如果不是一个代理对象，直接返回
        if(!isAopProxy(proxy)){
            return proxy;
        }
        return getProxyTargetObject(proxy);
    }

    private static boolean isAopProxy(Object object){
        return Proxy.isProxyClass(object.getClass());
    }

    private static Object getProxyTargetObject(Object object) throws Exception{
        Class<?> superClass = object.getClass().getSuperclass();
        //java.lang.reflect.Proxy 中的h
        Field h = object.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        ZGAopProxy aopProxy = (ZGAopProxy)h.get(object);
        Field target = aopProxy.getClass().getDeclaredField("target");
        target.setAccessible(true);
        return target.get(aopProxy);
    }
}
