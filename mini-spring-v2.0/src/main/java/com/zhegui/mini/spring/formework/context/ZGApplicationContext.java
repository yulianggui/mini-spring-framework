package com.zhegui.mini.spring.formework.context;

import com.zhegui.mini.spring.demo.controller.UserController;
import com.zhegui.mini.spring.formework.annotation.ZGAutowired;
import com.zhegui.mini.spring.formework.annotation.ZGController;
import com.zhegui.mini.spring.formework.annotation.ZGService;
import com.zhegui.mini.spring.formework.aop.ZGAopConfig;
import com.zhegui.mini.spring.formework.beans.BeanDefinition;
import com.zhegui.mini.spring.formework.beans.BeanWrapper;
import com.zhegui.mini.spring.formework.beans.ZGBeanPostProcessor;
import com.zhegui.mini.spring.formework.context.support.BeanDefinitionReader;
import com.zhegui.mini.spring.formework.core.BeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * create by zhegui on 2018/12/16
 */

/**
 * 容器 上下文信息
 */
public class ZGApplicationContext extends ZGDefaultListableBeanFactory implements BeanFactory{

    private String[] configLocations;

    private BeanDefinitionReader beanDefinitionReader;

    /**
     * 用来保证注册式单例的容器
     *  spring 中存放单例的bean
     */
    private Map<String, Object> beanCacheMap = new ConcurrentHashMap<>();

    /**
     * 保存包装过的实例
     *    --即被代理过的对象
     */
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();

    public ZGApplicationContext(String ... locations){
        this.configLocations = locations;
        this.refresh();
    }


    /**
     * spring 入口
     */
    public void refresh(){
        //定位
        this.beanDefinitionReader = new BeanDefinitionReader(configLocations);

        //加载
        List<String> registryBeanClass = beanDefinitionReader.loadBeanDefinitions();

        //注册
        doRegistry(registryBeanClass);

        //依赖注入（自动，没有开启懒加载）

        doAutowired();

        //测试注入状况
        //UserController userController = (UserController) beanWrapperMap.get("userController").getOriginalInstance();
        //userController.queryTest("123665", "testt");
    }

    /**
     * 依赖注入
     *   -- 自动化得依赖注入
     */
    private void doAutowired() {
        //变量所有的
        for(Map.Entry<String, BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();

            /**
             * 如果不是懒加载
             *    --这里默认为false
             */
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                //将beanName都包装起来
                Object object = getBean(beanName);
                //拿到的是代理对象
                System.out.println(object.getClass());
            }
        }

        for (Map.Entry<String, BeanWrapper> beanWrapperEntry : this.beanWrapperMap.entrySet()){
            populateBean(beanWrapperEntry.getKey(), beanWrapperEntry.getValue().getOriginalInstance());
        }
    }

    /**
     * 注入的实现
     * @param beanName
     * @param instance
     */
    private void populateBean(String beanName, Object instance){
        Class<?> clazz = instance.getClass();

        /**
         * 如果没有添加ZGController 和 ZGService 两个注解
         *  则不需要注入
         */
        if(!(clazz.isAnnotationPresent(ZGController.class)
                || clazz.isAnnotationPresent(ZGService.class))){
            return ;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields){

            //如果字段没有添加ZGAutowired，则跳过
            if(!field.isAnnotationPresent(ZGAutowired.class)){
                continue;
            }

            ZGAutowired autowired = field.getAnnotation(ZGAutowired.class);

            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            //开启私有属性访问权限
            field.setAccessible(true);
            try {
                //注入的也会是包装类
                field.set(instance, this.beanWrapperMap.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    //真正的将BeanDefinitions注册到BeanDefinitionMap中
    private void doRegistry(List<String> registryBeanClass) {

        for(String className : registryBeanClass){

            //beanName有三种情况
            //1、默认是类名首字母小写
            //2、自定义名字
            //3、接口注入
            try {
                Class<?> beanClass = Class.forName(className);
                if(beanClass.isInterface()){
                    continue;
                }
                BeanDefinition beanDefinition = beanDefinitionReader.registerBean(className);
                if(beanDefinition != null){
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
                }
                //方便后面的接口注入，
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> itemInterface : interfaces) {
                    //如果是多个实现类，这里只做覆盖处理
                    this.beanDefinitionMap.put(itemInterface.getName(), beanDefinition);
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    /**
     * 通过读取BeanDefinition中的信息
     * 然后，通过反射机制创建一个实例并返回
     * Spring做法是，不会把原始的对象放出去，会用一个BeanWrapper来进行一次包装
     * 包装器模式：
     *   1、保留原来的OOP关系
     *   2、对它进行扩展，增强（为AOP打基础）
     * @param beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) {
        /**
         * spring 对 bean只负责创建，不负责销毁
          */
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        if(beanDefinition == null){
            System.out.println("getBean key 【" + beanName + "】 is not exist!");
            return null;
        }
        try{

            //生成通知事件
            ZGBeanPostProcessor beanPostProcessor = new ZGBeanPostProcessor();

            Object instance = createBeanInstance(beanDefinition);
            if(null == instance){
                return null;
            }
            //在 wrapper初始化之前通知
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);


            BeanWrapper beanWrapper = new BeanWrapper(instance);
            //将代理对象的信息保存
            ZGAopConfig aopConfig = createBeanAopConfig(beanDefinition);
            beanWrapper.setAopConfig(aopConfig);
            //把通知事件添加进去
            beanWrapper.setPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName, beanWrapper);

            //在 wrapper初始化之后通知
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);


            /**
             * 在Bean初始化为之后调用
             *   --？？？循环依赖如何解决
             */
            //spring中就是在这一步调用的，但是会出现被依赖的属性还没有实例化和循环依赖的问
            // 递归？ + 缓存
            //populateBean(beanName, instance);

            //通过这样一调用，相当于给我们自己留有了可操作的空间
            return beanWrapper.getWrapperInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 扫描该bean是否需要进行增强，如果需要增强，则将增强的信息标注到 ZGAopConfig
     * @param beanDefinition
     * @return
     */
    private ZGAopConfig createBeanAopConfig(BeanDefinition beanDefinition) throws Exception{
        ZGAopConfig aopConfig = new ZGAopConfig();
        Properties properties = this.beanDefinitionReader.getContextConfigProperties();
        String pointCut = properties.getProperty("pointCut");
        String[] before = properties.getProperty("aspectBefore").split("\\s");
        String[] after = properties.getProperty("aspectAfter").split("\\s");

        String beanClassName = beanDefinition.getBeanClassName();
        Class<?> beanClazz = Class.forName(beanClassName);

        //简单处理获得，代理对象的类对象
        Class<?> aspectClazz = Class.forName(before[0]);

        Pattern pattern = Pattern.compile(pointCut);

        //遍历这些方法，看是否需要增强
        for (Method method : beanClazz.getMethods()){

            //method.toString()
            //public java.lang.String com.zhegui.mini.spring.....StringImpl.add(java.lang.String...)
            Matcher matcher = pattern.matcher(method.toString());
            if(matcher.matches()){
                Method[] points = new Method[]{aspectClazz.getMethod(before[1]),
                                                aspectClazz.getMethod(after[1])};
                Object aspect = aspectClazz.newInstance();

                //将需要增强的方法添加到aopConfig配置中
                aopConfig.put(method, aspect, points);
            }
        }
        return aopConfig;
    }

    /**
     * 创建Bean实例,默认为创建单例
     * @return
     */
    private Object createBeanInstance(BeanDefinition beanDefinition){
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            if(this.beanCacheMap.containsKey(className)){
                instance = this.beanCacheMap.get(className);
            }else{
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.beanCacheMap.put(className, instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
    }

    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionConut(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.beanDefinitionReader.getContextConfigProperties();
    }
}
