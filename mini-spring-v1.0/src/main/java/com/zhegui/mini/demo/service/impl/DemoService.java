package com.zhegui.mini.demo.service.impl;

import com.zhegui.mini.demo.service.IDemoService;
import com.zhegui.mini.spring.annotation.ZGService;

/**
 * create by zhegui on 2018/12/16
 */
@ZGService
public class DemoService implements IDemoService{

    @Override
    public void printUserName(String userName) {
        System.out.println("service userName = "+ userName);
    }
}
