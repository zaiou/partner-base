package com.zaiou.datamodel.redis.handler;

import com.alibaba.fastjson.JSONObject;
import com.zaiou.common.redis.constant.RedisKey;
import com.zaiou.common.redis.service.JedisService;
import com.zaiou.common.utils.StringUtils;
import com.zaiou.common.vo.datamodel.ProcessMessageReq;
import com.zaiou.datamodel.redis.handler.impl.MessageThreadImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 消息订阅消费 通知处理
 * @auther: LB 2018/10/31 17:43
 * @modify: LB 2018/10/31 17:43
 */
@Service
@Slf4j
public class DispatchMessageHandler {

    @Autowired
    private JedisService jedisService;
    @Autowired
    private MessageHandler messageHandler;

    @Value("${msgThread.initThreadNum:100}")
    public int initThreadNum;

    @Value("${msgThread.maxThreadNum:10000}")
    public int maxThreadNum;

    private ThreadPoolExecutor exe;

    @PostConstruct
    private void init() {
        log.debug("======初始化线程数 initThreadNum:{}", initThreadNum);
        log.debug("======最大化线程数 maxThreadNum:{}", maxThreadNum);
        exe = new ThreadPoolExecutor(initThreadNum, maxThreadNum, 60L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
        exe.allowCoreThreadTimeOut(true);
    }

    public void process() {
        while (true) {
            try {
                while (maxThreadNum/2 - exe.getActiveCount() == 0) {
                    log.info("初始化线程数{}活动线程数{},等待处理，开始睡眠....", initThreadNum, exe.getActiveCount());
                    Thread.sleep(3000);
                }
                // 获取发布的消息并删除redis中相关缓存
                String message = jedisService.lpop(RedisKey.getMMmodelNormal());
                if (StringUtils.isEmpty(message)) {
                    break;
                }
                log.debug("============message:{}", message);

                ProcessMessageReq req = JSONObject.parseObject(message, ProcessMessageReq.class);
                exe.execute(new MessageThreadImpl(Thread.currentThread().getName(), messageHandler, req, 1));

            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
