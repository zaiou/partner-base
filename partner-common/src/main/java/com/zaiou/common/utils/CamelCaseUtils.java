package com.zaiou.common.utils;

/**
 * @Description: 驼峰标识处理
 * @auther: LB 2018/9/19 17:13
 * @modify: LB 2018/9/19 17:13
 */
public class CamelCaseUtils {

    private static final char SEPARATOR = '_';

    /**
     * 驼峰标识字符串转写成下划线模式
     * @param s
     * @return
     */
    public static String toUnderlineName(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i >= 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    if (i > 0) sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 下划线字符串转驼峰标识
     * @param s
     * @return
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }


    public static void main(String[] args) {
        System.out.println(CamelCaseUtils.toUnderlineName("ISOCertifiedStaff"));
        System.out.println(CamelCaseUtils.toUnderlineName("CertifiedStaff"));
        System.out.println(CamelCaseUtils.toUnderlineName("UserID"));
        System.out.println(CamelCaseUtils.toUnderlineName("userID"));
        System.out.println(CamelCaseUtils.toCamelCase("iso_certified_staff"));
        System.out.println(CamelCaseUtils.toCamelCase("certified_staff"));
        System.out.println(CamelCaseUtils.toCamelCase("user_id"));
    }

}
