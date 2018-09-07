package com.zaiou.web.redis.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPubSub;

/**
 * @Description: 有订阅消息时，执行逻辑
 * @auther: LB 2018/9/5 22:06
 * @modify: LB 2018/9/5 22:06
 */
@Slf4j
@Service
public class MessageHandler extends JedisPubSub {
    @Override
    public void onMessage(String channel, String message) {
        log.info("发布的message:"+message);
        log.info(channel+"发来消息；请处理！！！");
    }
}
