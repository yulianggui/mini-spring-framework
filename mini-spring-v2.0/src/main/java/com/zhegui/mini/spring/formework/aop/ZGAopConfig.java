package com.zhegui.mini.spring.formework.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * create by zhegui on 2018/12/23
 */
//只是对application中的expression的封装
//目标代理对象的一个方法要增强
//由用自己实现的业务逻辑去增强
//配置文件的目的：告诉Spring，哪些类的哪些方法需要增强，增强的内容是什么
//对配置文件中所体现的内容进行封装
public class ZGAopConfig {

    //以目标对象需要增强的Method作为key，需要增强的代码内容作为value
    private Map<Method, ZGAspect> aspectMap = new HashMap<>();

    public void put(Method target, Object aspect, Method[] points){
        this.aspectMap.put(target, new ZGAspect(aspect, points));
    }

    public ZGAspect get(Method target){
        return this.aspectMap.get(target);
    }

    public boolean contains(Method method){
        return this.aspectMap.containsKey(method);
    }

    public class ZGAspect{
        private Object aspect;
        private Method[] points;

        public ZGAspect(Object aspect, Method[] points) {
            this.aspect = aspect;
            this.points = points;
        }

        public Object getAspect() {
            return aspect;
        }

        public Method[] getPonits() {
            return points;
        }
    }
}
