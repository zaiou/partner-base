package com.zaiou.cleaning.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zaiou 2019-05-28
 * @Description: 任务初始化
 * @modify zaiou 2019-05-28
 */
public class TaskInitConfig {
    public static Map<String,String> parserInitConfig=new HashMap<>();

    static {
        parserInitConfig.put("cleaningCmUserParseJob","解析用户定义表【CmUser】作业");
    }
}
