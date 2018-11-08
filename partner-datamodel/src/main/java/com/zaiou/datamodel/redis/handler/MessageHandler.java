package com.zaiou.datamodel.redis.handler;

import com.zaiou.common.vo.datamodel.ProcessMessageReq;

/**
 * @Description: 消息业务逻辑处理
 * @auther: LB 2018/11/1 10:21
 * @modify: LB 2018/11/1 10:21
 */
public interface MessageHandler {
    /**
     * 正常业务，消息处理
     * @param req
     */
    void handler(ProcessMessageReq req);
}
