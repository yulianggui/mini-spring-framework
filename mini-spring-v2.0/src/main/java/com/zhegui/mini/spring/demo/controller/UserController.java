package com.zhegui.mini.spring.demo.controller;

import com.zhegui.mini.spring.demo.service.IUserService;
import com.zhegui.mini.spring.formework.annotation.ZGAutowired;
import com.zhegui.mini.spring.formework.annotation.ZGController;
import com.zhegui.mini.spring.formework.annotation.ZGService;

/**
 * create by zhegui on 2018/12/20
 */
@ZGController
public class UserController {

    @ZGAutowired
    private IUserService userService;

    public String queryTest(String userId, String userName){
        System.out.println("controller , userContollre .....");
        return userService.queryUser(userId, userName);
    }
}
