package com.zhegui.mini.spring.formework.context.support;

import com.zhegui.mini.spring.formework.beans.BeanDefinition;
import com.zhegui.mini.spring.formework.utils.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * create by zhegui on 2018/12/16
 */

/**
 * 对配置文件进行读取解析
 */
public class BeanDefinitionReader {

    private Properties contextConfig = new Properties();

    private List<String> registryBeanClass = new ArrayList<>();

    //在配置文件中，用来获取自动扫描的包名的key
    private final String SCAN_PACKAGE = "scanPackage";

    public BeanDefinitionReader(String ... locations){
        //在spring中通过Reader去查找和定位，此处通过Properties模拟
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:","/"));
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
        doScanner(contextConfig.getProperty(SCAN_PACKAGE));
    }

    public List<String> loadBeanDefinitions(){
        return registryBeanClass;
    }

    //没注册一个className，就返回一个BeanDefinition
    //只是为了对配置信息进行一个包装，相当于存在于内存中的配置（依赖属（特）性等）
    public BeanDefinition registerBean(String className){
        if(this.registryBeanClass.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setLazyInit(false); //默认也是false
            String factoryBeanName = className.substring(className.lastIndexOf(".") + 1);
            //在Spring 中 BeanDefinition 的 factoryBeanName 就是 className 的simpleName
            beanDefinition.setFactoryBeanName(CommonUtil.lowerFirstCase(factoryBeanName));
            return beanDefinition;
        }
        return null;
    }

    public Properties getContextConfigProperties(){
        return this.contextConfig;
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
                registryBeanClass.add(scanPackName + "." + file.getName().replace(".class",""));
            }
        }
    }

}
