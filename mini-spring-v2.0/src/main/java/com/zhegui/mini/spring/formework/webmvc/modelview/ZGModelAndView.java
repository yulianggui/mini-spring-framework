package com.zhegui.mini.spring.formework.webmvc.modelview;

import java.util.Map;

/**
 * create by zhegui on 2018/12/20
 */
public class ZGModelAndView {

    private String viewName;

    private Map<String, ?> model;

    public ZGModelAndView(String viewName, Map<String, ?> model){
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
