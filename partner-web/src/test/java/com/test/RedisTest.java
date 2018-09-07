package com.test;

import com.zaiou.common.redis.constant.RedisKey;
import com.zaiou.common.redis.service.JedisService;
import com.zaiou.web.WebApplication;
import com.zaiou.web.redis.subscribe.SubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @auther: LB 2018/8/28 19:21
 * @modify: LB 2018/8/28 19:21
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = WebApplication.class)
@Slf4j
public class RedisTest {
    @LocalServerPort
    private int port;

    @Autowired
    private JedisService jedisService;

    @Autowired
    private SubscribeService subscribeService;

    /**
     * rediskey
     */
    private String redisKey= RedisKey.getWorkbenchAuccess("key");

    private String redisSeriableKey= RedisKey.getSeriableWorkbenchAuccess("seriable");

    private String redisMqKey = RedisKey.getMqWorkbenchAuccess("mq");

    private String redisStatisKey = RedisKey.getStatisWorkbenchAuccess("statis");

    /**
     * 插值
     */
    @Test
    public void set(){
        log.info("---"+port);
        log.info("--------"+jedisService);
        jedisService.set(redisKey, "redis的值", null);
    }

    /**
     * 序列化插值
     */
    @Test
    public void setObj(){
        jedisService.setBySerialize(redisSeriableKey, "redis序列化的值", null);
    }

    /**
     * 存list
     */
    @Test
    public void setList(){
        List<String> list = new ArrayList<>();
        list.add("你好");
        jedisService.setList(redisKey, list, null);
    }

    /**
     * 取值
     */
    @Test
    public void getRedis(){
        String redisValue = jedisService.get(redisKey);
        log.info("redis取值======="+redisValue);
    }

    /**
     * 获取字节数组
     */
    @Test
    public void getByteArray(){
        byte[] bytes = jedisService.getByteArray(redisKey);
        log.info("获取redis字节数组---》"+bytes);
    }

    /**
     * redis序列化取值
     */
    @Test
    public void getByUnSerialize(){
        Object redisSeriableValue = jedisService.getByUnSerialize(redisSeriableKey);
        log.info("redis反序列化取值--->"+redisSeriableValue);
    }

    /**
     * 泛型获取被序列化存储的值
     */
    @Test
    public void getTByUnSerialize(){
        String redisT = jedisService.getTByUnSerialize(String.class, redisSeriableKey);
        log.info("redis泛型获取被序列化存储的值--->"+redisT);
    }

    /**
     * 获取list
     */
    @Test
    public void getList(){
        List<String> list = jedisService.getList(redisKey, String.class);
        log.info("list为---》"+list);
    }

    /**
     * 模糊匹配所有的redis的key
     */
    @Test
    public void keys(){
        Set<String> set = jedisService.keys("*");
        log.info("匹配的redisValue==========--"+set.toString());
    }

    /**
     *  list的头部添加字符串元素
     */
    @Test
    public void lPush(){
        jedisService.lpush(redisMqKey, "头队列");
    }

    /**
     * list 的尾部添加字符串元素
     */
    @Test
    public void rpush(){
        jedisService.rpush(redisMqKey, "尾队列");
    }

    /**
     * list 的头部删除元素
     */
    @Test
    public void lpop(){
        String mqkey = jedisService.lpop(redisMqKey);
        log.info("头部删除元素---》"+mqkey);
    }

    /**
     * list 的尾部删除元素
     */
    @Test
    public void rpop(){
        String mqkey = jedisService.rpop(redisMqKey);
        log.info("尾部删除元素---》"+mqkey);
    }

    /**
     * 删除
     */
    @Test
    public void del(){
        jedisService.del(redisMqKey);
    }

    /**
     * 加一
     */
    @Test
    public void incr(){
        Long statis = jedisService.incr(redisStatisKey);
        log.info("加一===》"+ statis);
    }

    /**
     * 消息订阅
     */
    @Test
    public void subscribe() {
        subscribeService.subscribe();
    }

    /**
     * 消息发布
     */
    @Test
    public void publish(){
        jedisService.publish(RedisKey.getMqWorkbenchAuccess("submq"),"消息订阅发布");
    }
}
