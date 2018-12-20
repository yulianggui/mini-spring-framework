package com.zhegui.mini.spring.formework.webmvc.handler;

import com.zhegui.mini.spring.formework.webmvc.modelview.ZGModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; /**
 * create by zhegui on 2018/12/20
 */

/**
 * 这里讲类抽出来
 *   专人干专事，解耦
 */
public class ZGHandlerAdapter {

    /**
     *
     * @param req
     * @param resp
     * @param handler  为什么要传入 handler
     *                 handler中包含了controller实例、method、url信息
     *                 注：这里只能处理url后面带参数的形式，哈哈
     *
     *                 resp的目的：
     *                     只有一个，只是为了将其传给用户，给到method方法使用
     *                 因为req和resp只能由容器提供，哈哈
     * @return
     */
    public ZGModelAndView handle(HttpServletRequest req, HttpServletResponse resp, ZGHandlerMapping handler) {
        //根据用户请求的参数信息，跟method中的参数
        //信息进行动态匹配
        //只有当用户传过来的ModelAndView为null时才会创建
        return null;
    }
}
