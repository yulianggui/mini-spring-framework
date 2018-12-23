package com.zhegui.mini.spring.formework.webmvc.handler;

import com.zhegui.mini.spring.formework.webmvc.modelview.ZGModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map; /**
 * create by zhegui on 2018/12/20
 */

/**
 * 这里讲类抽出来
 *   专人干专事，解耦
 */
public class ZGHandlerAdapter {

    /**
     * Method参数映射关系
     */
    private Map<String, Integer> paramMapping;

    public ZGHandlerAdapter(Map<String, Integer> paramMapping){
        this.paramMapping = paramMapping;
    }

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
    public ZGModelAndView handle(HttpServletRequest req, HttpServletResponse resp, ZGHandlerMapping handler) throws Exception{
        //根据用户请求的参数信息，跟method中的参数
        //信息进行动态匹配
        //只有当用户传过来的ModelAndView为null时才会创建

        //1、要准备好这个方法的形参列表
        //
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();

        //2、拿到自定义命名参数所在的位置
        Map<String, String[]> reqParameterMap = req.getParameterMap();

        //3、构造实参列表
        Object[] paramValues = new Object[paramTypes.length];
        for (Map.Entry<String, String[]> param : reqParameterMap.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s",",");
            if(!paramMapping.containsKey(param.getKey())){
                continue;
            }
            int index = paramMapping.get(param.getKey());
            paramValues[index] = caseStringValue(value, paramTypes[index]);
        }

        if(this.paramMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex = this.paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }
        if(this.paramMapping.containsKey(HttpServletResponse.class.getName())){
            int respIndex = this.paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        //4、从handler中取出controller、method，然后利用反射机制调用
        Object result = handler.getMethod().invoke(handler.getController(), paramValues);
        if(result == null){ return  null; }
        boolean isModeAndView = handler.getMethod().getReturnType() == ZGModelAndView.class;
        if(isModeAndView){
            return (ZGModelAndView)result;
        }
        if(result instanceof String){
            resp.getWriter().write(result.toString());
        }
        return null;
    }


    private Object caseStringValue(String value, Class<?> clazz){
        if(clazz == String.class){
            return value;
        }else if(clazz == Integer.class){
            return Integer.valueOf(value);
        }else if(clazz == int.class){
            return Integer.valueOf(value).intValue();
        }
        return null;
    }
}
