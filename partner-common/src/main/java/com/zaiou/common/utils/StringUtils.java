package com.zaiou.common.utils;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;

/**
 * @Description:
 * @auther: LB 2018/8/30 23:09
 * @modify: LB 2018/8/30 23:09
 */
public class StringUtils {
    /**
     *  转换字符串,带默认值
     * @param obj
     * @param def
     * @return
     */
    public static String toString(Object obj, String def) {
        if (obj == null) {
            return def;
        } else {
            return String.valueOf(obj);
        }
    }

    /**
     *  校验对象是否为not null
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     *  数组里任何一个元素为空，返回false。
     * @param array
     * @return
     */
    public static boolean isNotEmpty(Object... array) {
        if (array == null || array.length == 0) {
            return false;
        }

        for (Object o : array) {
            if (isEmpty(o)) {
                return false;
            }
        }

        return true;
    }

    /**
     *  检查对象是否为NULL
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj) == null || "".equals(obj) ? true : false;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Dictionary) {
            return ((Dictionary<?, ?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }

        return false;
    }

    /**
     *  判断是否为正数或者0,最多两位小数
     * @param value
     * @return
     */
    public static boolean isPositiveNumber(String value) {
        if (value == null) {
            return false;
        }
        String reg = "^([1-9]\\d*|0)(\\.\\d{1,2})?$";
        return value.matches(reg);
    }

    /**
     *  判断是否为正整数或0
     * @param value
     * @return
     */
    public static boolean isPositiveNumber2(String value) {
        if (value == null) {
            return false;
        }
        String reg = "^[1-9]\\d*|0$";
        return value.matches(reg);
    }

    /**
     *  判断是否为正整数
     * @param value
     * @return
     */
    public static boolean isPositiveNumber3(String value) {
        if (value == null) {
            return false;
        }
        String reg = "^[1-9]\\d*$";
        return value.matches(reg);
    }

    /**
     *  判断是否为负数或者0,最多两位小数
     * @param value
     * @return
     */
    public static boolean isPositiveNumber4(String value) {
        if (value == null) {
            return false;
        }
        String reg = "^(-[1-9]\\d*|0)(\\.\\d{1,2})?$";
        return value.matches(reg);
    }

    /**
     *  0-100的数字，一位小数
     * @param value
     * @return
     */
    public static boolean isPositiveNumber5(String value) {
        if (value == null) {
            return false;
        }
        String reg = "^((0|[1-9]\\d?)(\\.\\d)?)|100|100.0$";
        return value.matches(reg);
    }

    /**
     *  判断是否为数字,最多两位小数
     * @param value
     * @return
     */
    public static boolean isNumber(String value) {
        if (value == null) {
            return false;
        }
        String reg = "^(-?)([1-9]\\d*|0)(\\.\\d{1,2})?$";
        return value.matches(reg);
    }

    /**
     *  判断电话号码格式
     * @param value
     * @return
     */
    public static boolean isPhone1(String value) {
        if (value == null) {
            return false;
        }
        String reg = "^\\d{4}-\\d+$";
        return value.matches(reg);
    }

    /**
     *  转义sql通配符
     * @param value
     * @return
     */
    public static String escapeSqlWildcard(String value) {
        if (value == null) {
            return null;
        }
        return value
                .replace("/", "//")
                .replace("%", "/%")
                .replace("_", "/_");
    }
}
