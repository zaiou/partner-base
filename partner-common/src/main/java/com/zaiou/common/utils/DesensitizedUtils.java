package com.zaiou.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.zaiou.common.annotation.Desensitized;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;

/**
 * @Description: 脱敏处理帮助类
 * @auther: LB 2018/10/10 09:47
 * @modify: LB 2018/10/10 09:47
 */
public class DesensitizedUtils {

    public static String toJsonString(Object object) {
        return toJsonString(false, object);
    }

    public static String toJsonString(boolean sensitive, Object object) {
        return object != null
                ? sensitive ? JSON.toJSONString(object, getValueFilter(), SerializerFeature.PrettyFormat)
                : JSON.toJSONString(object, true)
                : null;
    }

    /* 日志脱敏 */
    private static final ValueFilter getValueFilter() {
        return new ValueFilter() {
            @Override
            public Object process(Object obj, String key, Object value) {// obj-对象 key-字段名 value-字段值
                try {
                    Field field = obj.getClass().getDeclaredField(key);
                    Desensitized annotation = field.getAnnotation(Desensitized.class);
                    if (null != annotation && value instanceof String) {
                        String strVal = (String) value;
                        if (org.apache.commons.lang.StringUtils.isNotBlank(strVal)) {
                            switch (annotation.type()) {
                                case ID_CARD:
                                    return idCardNum(strVal);
                                case MOBILE_PHONE:
                                    return mobilePhone(strVal);
                                case BANK_CARD:
                                    return bankCard(strVal);
                                default:
                                    return strVal;
                            }
                        }
                    }
                    return value;
                } catch (NoSuchFieldException e) {
                    // 找不到的field对功能没有影响,空处理
                }
                return value;
            }
        };
    }

    /**
     * 【中文姓名】只显示第一个汉字，其他隐藏为2个星号，比如：李**
     *
     * @param fullName
     * @return
     */
    public static String chineseName(String fullName) {
        if (org.apache.commons.lang.StringUtils.isBlank(fullName)) {
            return "";
        }
        String name = org.apache.commons.lang.StringUtils.left(fullName, 1);
        return org.apache.commons.lang.StringUtils.rightPad(name, org.apache.commons.lang.StringUtils.length(fullName), "*");
    }

    /**
     * 【身份证号】显示最后四位，其他隐藏。共计18位或者15位，比如：*************1234
     *
     * @param id
     * @return
     */
    public static String idCardNum(String id) {
        if (org.apache.commons.lang.StringUtils.isBlank(id)) {
            return "";
        }
        String num = org.apache.commons.lang.StringUtils.right(id, 4);
        return org.apache.commons.lang.StringUtils.leftPad(num, org.apache.commons.lang.StringUtils.length(id), "*");
    }

    /**
     * 【手机号码】前三位，后四位，其他隐藏，比如135****6810
     *
     * @param num
     * @return
     */
    public static String mobilePhone(String num) {
        if (org.apache.commons.lang.StringUtils.isBlank(num)) {
            return "";
        }
        return org.apache.commons.lang.StringUtils.left(num, 3).concat(org.apache.commons.lang.StringUtils
                .removeStart(org.apache.commons.lang.StringUtils.leftPad(org.apache.commons.lang.StringUtils.right(num, 4), org.apache.commons.lang.StringUtils.length(num), "*"), "***"));
    }

    /**
     * 【银行卡号】前六位，后四位，其他用星号隐藏每位1个星号，比如：6222600**********1234>
     *
     * @param cardNum
     * @return
     */
    public static String bankCard(String cardNum) {
        if (org.apache.commons.lang.StringUtils.isBlank(cardNum)) {
            return "";
        }
        return org.apache.commons.lang.StringUtils.left(cardNum, 6).concat(org.apache.commons.lang.StringUtils.removeStart(
                org.apache.commons.lang.StringUtils.leftPad(org.apache.commons.lang.StringUtils.right(cardNum, 4), org.apache.commons.lang.StringUtils.length(cardNum), "*"), "******"));
    }

    /**
     * 二个中文（保留最后一个字），三个中或以上中文（保留后两个中文）
     *
     * @Title: userName
     * @Description:脱敏用户名称
     * @throws @date
     *             2018年6月2日
     */
    public static String userName(String name) {
        if (org.apache.commons.lang.StringUtils.isBlank(name)) {
            return "";
        }
        String tmp = (name.length() > 2) ? org.apache.commons.lang.StringUtils.right(name, 2) : org.apache.commons.lang.StringUtils.right(name, 1);
        return org.apache.commons.lang.StringUtils.leftPad(tmp, StringUtils.length(name), "*");
    }
}
