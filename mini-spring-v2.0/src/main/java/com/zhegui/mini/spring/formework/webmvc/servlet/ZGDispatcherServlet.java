package com.zhegui.mini.spring.formework.webmvc.servlet;

import com.zhegui.mini.spring.formework.context.ZGApplicationContext;
import com.zhegui.mini.spring.formework.webmvc.handler.ZGHandlerAdapter;
import com.zhegui.mini.spring.formework.webmvc.handler.ZGHandlerMapping;
import com.zhegui.mini.spring.formework.webmvc.modelview.ZGModelAndView;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * create by zhegui on 2018/12/16
 */
//作为一个MVC的启动入口
public class ZGDispatcherServlet extends HttpServlet{

    private final String LOCATION = "contextConfigLocation";

    /**
     * 这里，为什么不用Map，而是List？？？
     *   1、ZGHandlerMapping里边封装了url
     *   2、springMVC中，
     *    同时url支持正则
     */
    //private Map<String, ZGHandlerMapping> handlerMappingMap = new HashMap<>();

    private List<ZGHandlerMapping> handlerMappings = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //委派、分发请求
        doDispatch(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        ZGApplicationContext context = new ZGApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }

    /**
     * 通过该方法将请求分发到controller中
     *  对应的Method
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {

        //1、从List<ZGHandlerMapping> 拿到对应的ZGHandlerMapping
        ZGHandlerMapping handler = getHandler(req);

        //2、从ZGHandlerMapping中拿到对应的ZGHandlerAdapter
        ZGHandlerAdapter ha = getHandlerAdapter(handler);

        //3、处理并返回ZGModelAndView
        ZGModelAndView mv = ha.handle(req, resp, handler);

        //4、对ZGModelAndView进行模板渲染,或者说返回
        processDispatchResult(resp, mv);

    }

    private void processDispatchResult(HttpServletResponse resp, ZGModelAndView mv) {
        //调用ZGViewResolver的resolver方法
        return;
    }

    /**
     * 通过handlerMapping 从里边获取到对应的Method解析器
     * @param handler
     * @return
     */
    private ZGHandlerAdapter getHandlerAdapter(ZGHandlerMapping handler) {
        return null;
    }

    /**
     * 拿到URI -- Method 对应的封装类
     *    HandlerMapping
     * @param req
     * @return
     */
    private ZGHandlerMapping getHandler(HttpServletRequest req) {
        return null;
    }

    /**
     * 相当于Spring MVC 中 DispatchService初始化九大组件
     */
    private void initStrategies(ZGApplicationContext context){
        //文件上传解析，如果请求类型是multipart将通过
        //MultipartResolver进行文件上传
        //initMultipartResolver(context);

        //initLocaleResolver(context);  //本地化解析

        //initThemeResolver(context);  //主题解析

        initHandlerMappings(context); //映射处理器

        initHandlerAdapters(context); //通过HandlerAdapter 进行多类型的参数动态匹配
        
        initViewResolvers(context); //解析请求到视图名，比如JSP页面
    }

    /**
     * 通过ViewResolvers实现动态模板解析
     * @param context
     */
    private void initViewResolvers(ZGApplicationContext context) {
    }

    /**
     * 通过HandlerAdapter 进行多类型的参数动态匹配
     *  用来动态匹配Method参数，包括类转换、动态赋值
     * @param context
     */
    private void initHandlerAdapters(ZGApplicationContext context) {
    }

    /**
     * 同过HandlerMapping，将请求映射到处理器
     *  用来保存Controller中配置的RequestMapping
     *  和Method的一个对应关系
     * @param context
     */
    private void initHandlerMappings(ZGApplicationContext context) {

    }


}
