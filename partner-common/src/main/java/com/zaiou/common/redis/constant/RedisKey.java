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
        return MessageFormat.format("partner:common:resultcode:{0}:{1}", system, code);
    }

    /**
     * 系统参数
     * @param code
     * @return
     */
    public static String getSyspara(String code) {
        return MessageFormat.format("partner:common:syspara:{0}", StringUtils.toString(code, DEFAULT_PADDING));
    }

    /**
     * 会话-用户信息
     * @param token
     * @return
     */
    public static String getTokenUserinfo(String token) {
        return MessageFormat.format("partner:web:tokenuserinfo:{0}", StringUtils.toString(token, DEFAULT_PADDING));
    }

    /**
     * 会话-用户token
     * @param userCode
     * @return
     */
    public static String getLoginToken(String userCode) {
        return MessageFormat.format("partner:web:logintoken:{0}", StringUtils.toString(userCode, DEFAULT_PADDING));
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
     * 测试队列设值
     * @param userCode
     * @return
     */
    public static String getMqWorkbenchAuccess(String userCode) {
        return MessageFormat.format("partner:mq:workbench:success:{0}",
                StringUtils.toString(userCode, DEFAULT_PADDING));
    }

    /**
     * 测试统计的key
     * @param userCode
     * @return
     */
    public static String getStatisWorkbenchAuccess(String userCode) {
        return MessageFormat.format("partner:statis:workbench:success:{0}",
                StringUtils.toString(userCode, DEFAULT_PADDING));
    }


}
