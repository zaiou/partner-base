package com.zaiou.common.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 用于PO中定义表字段的注解
 * @auther: LB 2018/9/19 17:34
 * @modify: LB 2018/9/19 17:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD })
public @interface Column {
    String value();
}
