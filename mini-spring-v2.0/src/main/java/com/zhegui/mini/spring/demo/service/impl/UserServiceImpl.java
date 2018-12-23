package com.zhegui.mini.spring.demo.service.impl;

import com.zhegui.mini.spring.demo.service.IUserService;

/**
 * create by zhegui on 2018/12/20
 */
public class UserServiceImpl implements IUserService{
    @Override
    public String queryUser(String userId, String userName) {
        System.out.println("service userId = " + userId + ",userName = " + userName);
        return userId + "," + userName;
    }
}
