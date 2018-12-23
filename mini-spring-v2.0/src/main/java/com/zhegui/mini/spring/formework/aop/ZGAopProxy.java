package com.zhegui.mini.spring.formework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * create by zhegui on 2018/12/23
 */

//Spring中获得Bean的代理对象

//简化处理，默认使用JDK动态代理
public class ZGAopProxy implements InvocationHandler{

    private ZGAopConfig config;

    private Object target;

    public Object getProxy(Object instance){
        this.target = instance;
        Class<?> clazz = instance.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    public void setConfig(ZGAopConfig config){
        this.config = config;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //拿到原始类的方法，用原始类的方法去匹配
        Method m = this.target.getClass().getMethod(method.getName(),method.getParameterTypes());

        //ZGAopConfig 存入的是原始类的Method方法， invoke中的method是代理类的方法
        //在代理类中method方法又调用了 target 类的 目标方法
        //因此 m 与 Method的Method信息不等同
        if(config.contains(m)){
            //拿到增强对象的实例
            //ZGAopConfig.ZGAspect 保存的是aspect的实例，和需要调用的方法
            //ZGAopConfig中，原始类相应的Method为map的Key
            ZGAopConfig.ZGAspect aspect = config.get(m);
            //拿到aspect的第一个方法
            //这里写死为before,
            aspect.getPonits()[0].invoke(aspect.getAspect());
        }
        //调用原始方法
        Object object = method.invoke(this.target, args);

        if(config.contains(m)){
            ZGAopConfig.ZGAspect aspect = config.get(m);
            aspect.getPonits()[1].invoke(aspect.getAspect());
        }
        return object;
    }
}
