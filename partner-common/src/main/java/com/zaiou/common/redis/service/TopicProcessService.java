package com.zaiou.common.redis.service;

/**
 * @Description: JedisPool执行Redis的常用命令
 * @auther: LB 2018/10/10 11:39
 * @modify: LB 2018/10/10 11:39
 */
public interface TopicProcessService {

    /*
     * 接收消息之后执行的方法
     */
    public void receivedMessage(String msg);

    /*
     * 监听KEY
     */
    public String key();

}
