package com.zaiou.datamodel.redis.listener;

import com.zaiou.datamodel.redis.handler.DispatchMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.Resource;

/**
 * @Description: 正常业务-订阅消息
 * @auther: LB 2018/10/31 17:18
 * @modify: LB 2018/10/31 17:18
 */
@Slf4j
@Service
public class RequestMsgListener extends JedisPubSub {

    @Resource
    private DispatchMessageHandler dispatchMessageHandler;

    @Override
    public void onMessage(String channel, String message) {

        log.info("数据模型开始-消息队列:{}，队列内容:{}",channel,message);
        try {
            dispatchMessageHandler.process();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

}
