package com.zaiou.common.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 用于PO中定义表名的注解
 * @auther: LB 2018/9/19 17:00
 * @modify: LB 2018/9/19 17:00
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    String value();

}
