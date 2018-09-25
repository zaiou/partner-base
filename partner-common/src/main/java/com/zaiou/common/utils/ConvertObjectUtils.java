package com.zaiou.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 对象转换
 * @auther: LB 2018/9/20 22:14
 * @modify: LB 2018/9/20 22:14
 */
@Slf4j
public class ConvertObjectUtils {

    /**
     *  普通对象转换，必须两个对象字段名称和类型一致
     *
     * @param source
     *            需要转换的源对象值， 已实体化的
     * @param clazz
     *            目标类对象，如"UserInfo.class"
     * @return 转换后的对象
     */
    public static <T> T NormalConvertObject(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        try {
            Method[] methods = source.getClass().getMethods();
            T result = clazz.newInstance();
            for (Method m : methods) {
                try {
                    if (m.getName().startsWith("set")) {
                        String fieldName = m.getName().replaceFirst("set", "");
                        Method method = result.getClass().getMethod(m.getName(), m.getParameterTypes());
                        Method getMethod = source.getClass().getMethod("get" + fieldName, new Class[] {});
                        method.invoke(result, getMethod.invoke(source, new Object[] {}));
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            return result;
        } catch (InstantiationException e) {
            log.error(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 集合转换，将集合里的对象类型转换成指定对象类型
     * @param list
     *             待转换对象集合
     * @param clazz
     *             目标类类型
     * @return
     */
    public static <T> List<T> ConvertObjectList(List<?> list, Class<T> clazz){
        if (list == null) {
            return null;
        }
        List<T> result = new ArrayList<>();
        T t = null;
        for(Object obj : list){
            t = NormalConvertObject(obj,clazz);
            result.add(t);
        }
        return result;
    }

}
