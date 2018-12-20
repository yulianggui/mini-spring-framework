package com.zhegui.mini.spring.formework.webmvc.handler;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * create by zhegui on 2018/12/20
 */
public class ZGHandlerMapping {

    private Object controller;

    private Method method;

    private Pattern pattern;

    public ZGHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
