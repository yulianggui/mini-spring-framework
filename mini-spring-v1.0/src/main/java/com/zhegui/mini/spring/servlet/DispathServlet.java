package com.zhegui.mini.spring.servlet;

import com.zhegui.mini.demo.controller.DemoController;
import com.zhegui.mini.spring.annotation.ZGAutowried;
import com.zhegui.mini.spring.annotation.ZGController;
import com.zhegui.mini.spring.annotation.ZGService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * create by zhegui on 2018/12/16
 */
public class DispathServlet extends HttpServlet {

    /**
     * 解析properties配置信息
     */
    private Properties contextConfig = new Properties();

    /**
     * 持有所有的Bean，bean初始化之后都放到这里来持有
     * 相当于Spring 中的IOC容器
     */
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 保存所有的bean的名字
     */
    private List<String> classNames = new ArrayList<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("------------------调用doPost start ------------");

        System.out.println("------------------调用doPost end  --------------");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //定位、加载、注册、依赖注入

        //定位

        //在servlet中配置的init-param中找到名字为contextConfigLocation的值
        doLoadConfig(config.getInitParameter("contextConfigLocation"));


        //加载
        doScanner(contextConfig.getProperty("scanPackage"));

        //注册
        doRegistry();

        //自动依赖注入
        doAutowired();


        //测试是个已经注册到IOC容器，并且依赖注入成功
        //DemoController demoController = (DemoController) beanMap.get("demoController");
        //demoController.testDemo(null, null, "zg-test");
        //如果是SpringMVC，多设计一个HandlerMapper

        //将@RequestMappering中配置的url和一个Method 关联上
        //以便于从浏览器获得用户输入的URL之后，能够找到具体的method
        //并通过反射的方式调用
        initHandlerMapping();

    }

    private void initHandlerMapping() {
    }

    private void doRegistry() {
        if(classNames.isEmpty()){
            return;
        }

        try {

            //扫描类，初始化，并归入beanMap容器中
            for (String className : classNames){
                Class<?> clazz = Class.forName(className);

                //在Spring中，用多个类处理的
                if(clazz.isAnnotationPresent(ZGController.class)){
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    //在spring中，这个阶段不会直接put instance,而是
                    //会put的是BeanDefinition
                    beanMap.put(beanName, clazz.newInstance());
                }else if(clazz.isAnnotationPresent(ZGService.class)){
                    ZGService zgService = clazz.getAnnotation(ZGService.class);
                    //默认用类名首字母注入
                    //如果自己定义了BeanName，那么优先使用自己定义的beanName
                    //如果是一个接口，使用接口的类型去自动注入

                    if(clazz.isInterface()){
                        //如果是个接口，则不处理
                        continue;
                    }

                    //在Spring中统一会分别调用不同的方法 autowriedByName

                    String beanName = zgService.value();
                    if("".equals(beanName.trim())){
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }

                    //这里假设 @ZGService这个注解不加在接口上
                    Object instance = clazz.newInstance();
                    beanMap.put(beanName, instance);

                    //方便后面的接口注入，
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> itemInterface : interfaces) {
                        beanMap.put(itemInterface.getName(), instance);
                    }
                     
                }else{

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void doAutowired() {
        //模拟依赖注入
        if(beanMap.isEmpty()){
            return;
        }
        //遍历beanMap 容器
        for (Map.Entry<String, Object> entry : beanMap.entrySet()){
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields){
                if(!field.isAnnotationPresent(ZGAutowried.class)){
                    continue;
                }
                ZGAutowried autowried = field.getAnnotation(ZGAutowried.class);
                String beanName = autowried.value().trim();
                if("".equals(beanName)){
                    //如果不填写，则为字段类型名称
                    beanName = field.getType().getName();
                    System.out.println("value is null, filed beanName = " + beanName);
                }
                //设置强制访问
                field.setAccessible(true);
                try {
                    //表示要给具体的对象的 field 字段 赋值
                    //第一个表示 具体的对象，第二个参数表示 要个 第一个对象的 field 属性 赋哪个值
                    field.set(entry.getValue(), beanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 传入要扫描的包名
     * 扫描 scanPackName 下的所有的class文件，类全限定名添加到list中
     * @param scanPackName
     */
    private void doScanner(String scanPackName) {
        //把 . 替换成为 /
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackName + "." + file.getName());
            }else{
                classNames.add(scanPackName + "." + file.getName().replace(".class",""));
            }
        }
    }

    private void doLoadConfig(String location) {
        //在spring中通过Reader去查找和定位，此处通过Properties模拟
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:","/"));
        try {
            //加载文件
            contextConfig.load (is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
