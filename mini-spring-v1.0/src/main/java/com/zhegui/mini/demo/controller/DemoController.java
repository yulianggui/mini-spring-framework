package com.zhegui.mini.demo.controller;

import com.zhegui.mini.demo.service.IDemoService;
import com.zhegui.mini.spring.annotation.ZGAutowried;
import com.zhegui.mini.spring.annotation.ZGController;
import com.zhegui.mini.spring.annotation.ZGRequestMapping;
import com.zhegui.mini.spring.annotation.ZGRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create by zhegui on 2018/12/16
 */
@ZGController
@ZGRequestMapping("/mini_demo")
public class DemoController {

    @ZGAutowried
    IDemoService demoService;

    @ZGRequestMapping("/test_demo")
    public void testDemo(HttpServletRequest request,
                         HttpServletResponse response,
                         @ZGRequestParam("name") String userName){
        System.out.println("entry /mini_demo/test_demo");
        System.out.println("param name is :"+userName);
    }

}
