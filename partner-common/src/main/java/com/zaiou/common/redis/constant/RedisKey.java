package com.zaiou.common.redis.constant;


import com.zaiou.common.utils.StringUtils;

import java.text.MessageFormat;

/**
 * @Description: 储存redis的key
 * @auther: LB 2018/8/30 22:28
 * @modify: LB 2018/8/30 22:28
 */
public class RedisKey {

    private static final String DEFAULT_PADDING = "*";

    /**
     * 获取错误码 key
     * @param system
     * @param code
     * @return
     */
    public static String getResultCode(String system, String code) {
        return MessageFormat.format("credit:common:resultcode:{0}:{1}", system, code);
    }

    /**
     * 测试redis的key
     * @param userCode
     * @return
     */
    public static String getWorkbenchAuccess(String userCode) {
        return MessageFormat.format("partner:workbench:success:{0}",
                StringUtils.toString(userCode, DEFAULT_PADDING));
    }

    /**
     * 测试序列化redis的key
     * @param userCode
     * @return
     */
    public static String getSeriableWorkbenchAuccess(String userCode) {
        return MessageFormat.format("partner:seriable:workbench:success:{0}",
                StringUtils.toString(userCode, DEFAULT_PADDING));
    }

    /**
     * 队列设值
     * @param userCode
     * @return
     */
    public static String getMqWorkbenchAuccess(String userCode) {
        return MessageFormat.format("partner:mq:workbench:success:{0}",
                StringUtils.toString(userCode, DEFAULT_PADDING));
    }

    /**
     * 统计的key
     * @param userCode
     * @return
     */
    public static String getStatisWorkbenchAuccess(String userCode) {
        return MessageFormat.format("partner:statis:workbench:success:{0}",
                StringUtils.toString(userCode, DEFAULT_PADDING));
    }


}
