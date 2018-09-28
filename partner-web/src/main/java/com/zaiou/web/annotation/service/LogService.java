package com.zaiou.web.annotation.service;

import com.alibaba.fastjson.JSONObject;
import com.zaiou.common.utils.StringUtils;
import com.zaiou.web.annotation.Log;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 解释对象log注释，将有log注释的字段整合到json对象中
 * @auther: LB 2018/9/25 16:42
 * @modify: LB 2018/9/25 16:42
 */
@Component
public class LogService {

    public static JSONObject analysis(Object object) throws Exception {
        if(StringUtils.isEmpty(object)){
            return null;
        }
        // 获取object的类型
        Class<? extends Object> clazz = object.getClass();
        Class<? extends Object> superClazz = clazz.getSuperclass();
        List<Field> fieldlist = new ArrayList<Field>();
        // 迭代object对象父类的字段添加到集合中
        while (superClazz != Object.class) {
            Field[] fields = superClazz.getDeclaredFields();
            fieldlist.addAll(Arrays.asList(fields));
            superClazz = superClazz.getSuperclass();
        }
        // 获取该类型声明的成员
        fieldlist.addAll(Arrays.asList(clazz.getDeclaredFields()));
        JSONObject json = new JSONObject();
        // 遍历属性
        for (Field field : fieldlist) {
            // 对于private私有化的成员变量，通过setAccessible来修改器访问权限
            field.setAccessible(true);

            Log logAnno = field.getAnnotation(Log.class);
            if(StringUtils.isNotEmpty(logAnno)){
                Object value = field.get(object);
                String attrValue = StringUtils.isNotEmpty(value) ? String.valueOf(value) : "";
                json.put(logAnno.fileName(), attrValue);
            }

            // 重新设置会私有权限
            field.setAccessible(false);
        }
        return json;
    }
}
