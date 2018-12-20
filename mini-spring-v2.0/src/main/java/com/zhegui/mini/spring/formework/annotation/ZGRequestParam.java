package com.zhegui.mini.spring.formework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * create by zhegui on 2018/12/16
 */
@Target({ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ZGRequestParam {
    String value() default  "";
}
