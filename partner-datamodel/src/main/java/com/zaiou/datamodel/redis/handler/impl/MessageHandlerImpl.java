package com.zaiou.datamodel.redis.handler.impl;

import com.zaiou.common.vo.datamodel.ProcessMessageReq;
import com.zaiou.datamodel.redis.handler.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @Description: 消息业务逻辑处理
 * @auther: LB 2018/11/1 10:26
 * @modify: LB 2018/11/1 10:26
 */
@Service
@Slf4j
public class MessageHandlerImpl implements MessageHandler, InitializingBean {
    /**
     *  数据模型-正常业务流程入口
     * @param req
     */
    @Override
    public void handler(ProcessMessageReq req) {
        log.info("订阅的渠道有消息发布，进行处理======================"+req.getTestId());
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
