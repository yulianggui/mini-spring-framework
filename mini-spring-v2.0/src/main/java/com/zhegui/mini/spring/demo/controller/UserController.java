package com.zhegui.mini.spring.demo.controller;

import com.zhegui.mini.spring.demo.service.IUserService;
import com.zhegui.mini.spring.formework.annotation.*;
import com.zhegui.mini.spring.formework.webmvc.modelview.ZGModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * create by zhegui on 2018/12/20
 */
@ZGController
@ZGRequestMapping("/demo")
public class UserController {

    @ZGAutowired
    private IUserService userService;

    @ZGRequestMapping("/test")
    public String queryTest(@ZGRequestParam("userId") String userId,
                            @ZGRequestParam("userName") String userName){
        System.out.println("/test, controller , userContollre .....");
        return userService.queryUser(userId, userName);
    }


    @ZGRequestMapping("/action_01")
    public ZGModelAndView doAction( HttpServletRequest request,
                                    HttpServletResponse response,
                                    @ZGRequestParam("userId") String userId,
                                    @ZGRequestParam("name") String userName){
        System.out.println("/action_01, controller , userContollre .....");
        userService.queryUser(userId, userName);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("nameName", userName);
        model.put("userId", userId);
        model.put("data", "2018-12-22");
        model.put("token", "123456");
        ZGModelAndView modelAndView = new ZGModelAndView("first.html", model);
        return modelAndView;
    }
}
