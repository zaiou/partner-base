package com.zaiou.common.constant.task;

import com.zaiou.common.enums.task.JYMEnum;

import java.text.MessageFormat;

/**
 * @Description: 定时任务key
 * @auther: LB 2018/11/29 17:35
 * @modify: LB 2018/11/29 17:35
 */
public class TaskRedisKey {
    /* 获取当前是否已经有执行成功过的任务 */
    public static String getTaskKey(String yyyyMMdd, JYMEnum jym) {
        return MessageFormat.format("credit:task:running:state:{0}:{1}", jym.getTrancode(), yyyyMMdd);
    }
}
