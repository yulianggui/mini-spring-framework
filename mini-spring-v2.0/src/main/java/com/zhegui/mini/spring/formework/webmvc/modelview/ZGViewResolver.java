package com.zhegui.mini.spring.formework.webmvc.modelview;

/**
 * create by zhegui on 2018/12/20
 */

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这个类的主要目的
 *  1、将一个静态文件，变为一个动态文件
 *  2、根据用户提供的参数，赋值
 *  3、最终输出字符串，交给Response输出
 */
public class ZGViewResolver {

    private String viewName;

    private File viewFile;

    public  ZGViewResolver(String viewName, File viewFile){
        this.viewName = viewName;
        this.viewFile = viewFile;
    }

    public String viewResolver(ZGModelAndView mv) throws Exception{
        StringBuffer sb = new StringBuffer();
        RandomAccessFile br = new RandomAccessFile(this.viewFile, "r");
        //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.viewFile),"utf-8"));

        String line = null;
        while(null !=(line = br.readLine())){
            line = new String(line.getBytes("ISO-8859-1"), "utf-8");

            Matcher m = matcher(line);
            while (m.find()){
                //System.out.println(m.group(1));
                for (int i = 1; i<= m.groupCount(); i++){
                    //把表达式中间的字符串给取出来
                    String paramName = m.group(i);
                    Object paramValue = mv.getModel().get(paramName);
                    if(null == paramValue){
                        continue;
                    }
                    line = line.replaceAll("\\$\\{" + paramName + "\\}", paramValue.toString());
                    line = new String(line.getBytes("utf-8"), "ISO-8859-1");
                }
            }
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     *
     * @param str
     * @return
     */
    private Matcher matcher(String str){
        Pattern pattern = Pattern.compile("\\$\\{(.*?)}",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public File getViewFile() {
        return viewFile;
    }

    public void setViewFile(File viewFile) {
        this.viewFile = viewFile;
    }
}
