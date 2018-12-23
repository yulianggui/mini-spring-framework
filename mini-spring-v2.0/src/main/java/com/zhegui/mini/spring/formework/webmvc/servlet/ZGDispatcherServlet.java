package com.zhegui.mini.spring.formework.webmvc.servlet;

import com.zhegui.mini.spring.formework.annotation.ZGController;
import com.zhegui.mini.spring.formework.annotation.ZGRequestMapping;
import com.zhegui.mini.spring.formework.annotation.ZGRequestParam;
import com.zhegui.mini.spring.formework.aop.ZGAopProxyUtils;
import com.zhegui.mini.spring.formework.context.ZGApplicationContext;
import com.zhegui.mini.spring.formework.webmvc.handler.ZGHandlerAdapter;
import com.zhegui.mini.spring.formework.webmvc.handler.ZGHandlerMapping;
import com.zhegui.mini.spring.formework.webmvc.modelview.ZGModelAndView;
import com.zhegui.mini.spring.formework.webmvc.modelview.ZGViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Method 参数映射
     */
    private Map<ZGHandlerMapping,ZGHandlerAdapter> handlerAdapterMap = new HashMap<>();

    private List<ZGViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //委派、分发请求
        String url = req.getRequestURI();
        System.out.println("接收到请求： url==>" + url);
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("<font size='25' color='blue'>500 Exception</font><br/>Details:<br/>" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","")
                    .replaceAll("\\s","\r\n") +  "<font color='green'><i>Copyright</i></font>");
            e.printStackTrace();
        }
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
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        //1、从List<ZGHandlerMapping> 拿到对应的ZGHandlerMapping
        ZGHandlerMapping handler = getHandler(req);
        if(handler == null){
            resp.getWriter().write("404 not found!");
            return;
        }

        //2、从ZGHandlerMapping中拿到对应的ZGHandlerAdapter
        ZGHandlerAdapter ha = getHandlerAdapter(handler);

        if(ha == null){
            resp.getWriter().write("500 handlerAdapter is null");
            return ;
        }
        //3、处理并返回ZGModelAndView
        ZGModelAndView mv = ha.handle(req, resp, handler);

        if(mv == null){
            return;
        }
        //4、对ZGModelAndView进行模板渲染,或者说返回
        processDispatchResult(resp, mv);



    }

    /**
     * 处理请求
     * @param resp
     * @param mv
     * @throws Exception
     */
    private void processDispatchResult(HttpServletResponse resp, ZGModelAndView mv) throws Exception{
        //调用ZGViewResolver的resolver方法
        if(null == mv){
            return;
        }
        if(this.viewResolvers.isEmpty()){
            return;
        }
        for (ZGViewResolver viewResolver : this.viewResolvers){
            if(!mv.getViewName().equals(viewResolver.getViewName())){
                continue;
            }
            String out = viewResolver.viewResolver(mv);
            if(out != null){
                resp.getWriter().write(out);
                break;
            }
        }
        return;
    }

    /**
     * 通过handlerMapping 从里边获取到对应的Method解析器
     * @param handler
     * @return
     */
    private ZGHandlerAdapter getHandlerAdapter(ZGHandlerMapping handler) {
        if(this.handlerAdapterMap.isEmpty()){
            return null;
        }
        return this.handlerAdapterMap.get(handler);
    }

    /**
     * 拿到URI -- Method 对应的封装类
     *    HandlerMapping
     * @param req
     * @return
     */
    private ZGHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (ZGHandlerMapping handler : this.handlerMappings){
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){
                continue;
            }
            return handler;
        }
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
        //模板保存
        //例如，访问一个页面 http://localhost/text.html
        //解决页面名字和模板文件关联的问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        //将模板文件名称和模板文件关联
        for (File template : templateRootDir.listFiles()){
            this.viewResolvers.add(new ZGViewResolver(template.getName(), template));
        }
    }

    /**
     * 通过HandlerAdapter 进行多类型的参数动态匹配
     *  用来动态匹配Method参数，包括类转换、动态赋值
     * @param context
     */
    private void initHandlerAdapters(ZGApplicationContext context) {
        //这里简单处理：通过记录参数的index，这样就可以绑定到具体的位置了
        for(ZGHandlerMapping handlerMapping : this.handlerMappings){

            //每一个方法有一个参数列表，那么这里保存的是形参列表
            Map<String, Integer> paramMapping = new HashMap<>();
            //拿到Method 方法级别的所有注解
            //二维数组，因为一个参数可以添加多个主键
            Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
            for (int i = 0; i< pa.length; i++){
                for (Annotation a : pa[i]){
                    if(a instanceof ZGRequestParam){
                        //注解的值
                        String paramName = ((ZGRequestParam)a).value();
                        if(!"".equals(paramName)){
                            paramMapping.put(paramName, i);
                        }
                    }
                }
            }

            //接下来，要处理非命名参数
            //只处理request和Response
            Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
            for (int i =0; i<paramTypes.length; i++){
                Class<?> type = paramTypes[i];
                if(type == HttpServletRequest.class
                        || type == HttpServletResponse.class){
                    paramMapping.put(type.getName(), i);
                }
            }
            this.handlerAdapterMap.put(handlerMapping, new ZGHandlerAdapter(paramMapping));
        }
    }

    /**
     * 同过HandlerMapping，将请求映射到处理器
     *  用来保存Controller中配置的RequestMapping
     *  和Method的一个对应关系
     * @param context
     */
    private void initHandlerMappings(ZGApplicationContext context){
        String[] beanNames = context.getBeanDefinitionNames();
        try{
            for (String beanName : beanNames){
                //在SpringMVC层，并没有直接获得BeanWrapper
                //只能通过getBean拿到被代理过后的Bean
                //但是此时拿到的是代理对象，ZGController相当于已经失效
                //此时要想办法拿到最初的bean类信息
                Object proxyInstance = context.getBean(beanName);
                Object controller = ZGAopProxyUtils.getTargetObject(proxyInstance);
                Class<?> clazz = controller.getClass();  //判断时要使用代理对象
                if(!clazz.isAnnotationPresent(ZGController.class)){
                    continue;
                }
                String baseUrl = "";
                if(clazz.isAnnotationPresent(ZGRequestMapping.class)){
                    ZGRequestMapping requestMapping = clazz.getAnnotation(ZGRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //扫描所有的public方法
                Method[] methods = clazz.getMethods();
                for (Method method : methods){
                    if(!method.isAnnotationPresent(ZGRequestMapping.class)){
                        continue;
                    }
                    ZGRequestMapping methodMapping = method.getAnnotation(ZGRequestMapping.class);
                    String regex = (("/" + baseUrl + methodMapping.value())).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    //将原始对象传进去,这里要被代理的只是Service层
                    //没有实现controller层的增强，因为这里是只支持JDK增强
                    this.handlerMappings.add(new ZGHandlerMapping(pattern, controller, method));
                    System.out.println("HandlerMapper: " + regex + ", " + method);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
