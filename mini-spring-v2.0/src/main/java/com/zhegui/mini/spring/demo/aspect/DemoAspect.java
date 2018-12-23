package com.zhegui.mini.spring.demo.aspect;

/**
 * create by zhegui on 2018/12/23
 */
public class DemoAspect {

    public void before(){
        System.out.println("Invoker before.....");
    }

    public void after(){
        System.out.println("Invoke after.....");
    }
}
