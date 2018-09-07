package com.zaiou.web.redis.subscribe;

import com.zaiou.common.redis.constant.RedisKey;
import com.zaiou.common.redis.service.JedisService;
import com.zaiou.web.redis.handler.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: Jedis的subscribe操作是阻塞的 ;因此，我们另起一个线程池来进行subscribe操作
 * @auther: LB 2018/9/5 21:18
 * @modify: LB 2018/9/5 21:18
 */
@Service
@Slf4j
public class SubscribeService {

    @Autowired
    private JedisService jedisService;
    @Autowired
    private MessageHandler messageHandler;

    private ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public void subscribe() {
        threadPool.execute(new Runnable() {
            public void run() {
                log.info("业务-服务已订阅频道：{}", RedisKey.getMqWorkbenchAuccess("submq"));
                jedisService.subscribe(messageHandler, RedisKey.getMqWorkbenchAuccess("submq"));
            }
        });

    }
}
