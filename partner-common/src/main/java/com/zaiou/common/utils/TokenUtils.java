package com.zaiou.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Description: token
 * @auther: LB 2018/9/29 15:12
 * @modify: LB 2018/9/29 15:12
 */
public class TokenUtils {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    /**
     * getOnlyPK(利用时间yyyyMMddHHmmssSSS 格式,然后补4为的随机数得到的唯一值)
     * @return
     */
    public static String getOnlyPK() {
        int min = 1000000;
        int max = 9999999;
        int tmp = new Random().nextInt(Integer.MAX_VALUE);
        StringBuilder sb = new StringBuilder();
        sb.append(getSampleDateFormat_yyyyMMddHHmmssSSS().format(new Date()));
        sb.append(tmp % (max - min + 1) + min);
        return sb.toString();
    }

    public static SimpleDateFormat getSampleDateFormat_yyyyMMddHHmmssSSS() {
        return simpleDateFormat;
    }
}
