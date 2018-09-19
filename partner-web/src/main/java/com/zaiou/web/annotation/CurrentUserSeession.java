package com.zaiou.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: controller的方法中使用此注解，该注解在映射时会注入当前登录的用户的session对象
 * @auther: LB 2018/9/18 16:44
 * @modify: LB 2018/9/18 16:44
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUserSeession {
}
