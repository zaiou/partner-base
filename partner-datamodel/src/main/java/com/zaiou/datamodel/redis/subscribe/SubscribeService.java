package com.zaiou.datamodel.redis.subscribe;

import com.zaiou.common.redis.constant.RedisKey;
import com.zaiou.common.redis.service.JedisService;
import com.zaiou.datamodel.redis.listener.RequestMsgListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: redis消息订阅
 * @auther: LB 2018/10/31 15:49
 * @modify: LB 2018/10/31 15:49
 */
@Service
@Slf4j
public class SubscribeService {

    public ExecutorService threadPool = Executors.newFixedThreadPool(5);

    @Autowired
    private JedisService jedisService;
    @Resource
    private RequestMsgListener listener;

    public void subscribe() {
        threadPool.execute(new Runnable() {
            public void run() {
                log.info("业务-服务已订阅频道：{}", RedisKey.getMMmodelNormal());
                jedisService.subscribe(listener, RedisKey.getMsgModelNormal());
            }
        });
    }
}
