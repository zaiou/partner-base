package com.zaiou.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 日志字段添加注释
 * @auther: LB 2018/9/25 16:46
 * @modify: LB 2018/9/25 16:46
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.METHOD})
public @interface Log {

    String fileName();

}
