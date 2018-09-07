package com.zaiou.common.redis.service;

import com.alibaba.fastjson.JSON;
import com.zaiou.common.redis.vo.MQObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Set;

/**
 * @Description: jedis服务
 * @auther: LB 2018/8/28 16:56
 * @modify: LB 2018/8/28 16:56
 */
@Service
@Slf4j
public class JedisService {
    @Autowired(required = false)
    @Lazy
    private JedisPool jedisPool;

    /**
     *  获取redis连接
     * @return
     */
    public Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        log.info("	获取Redis链接:{}", jedis);
        return jedis;
    }

    /**
     * 释放redis连接
     * @param jedis
     */
    public void closeRedis(Jedis jedis) {
        if (jedis != null) {
            log.info("	释放Redis链接:{}", jedis);
            jedis.close();
        }
    }

    /**
     * 根据redis的键取值
     * @param key
     * @return
     */
    public String get(String key) {
        Jedis jedis = getJedis();
        try {
            log.info("get --> {} ", key);
            return jedis.get(key);
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 获取字节数组
     * @param key
     * @return
     */
    public byte[] getByteArray(String key) {
        Jedis jedis = getJedis();
        try {
            log.info("getByteArrayFromRedis --> key:{}", key);
            return jedis.get(key.getBytes());
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 获取被序列化存储的值
     * @param key
     * @return
     */
    public Object getByUnSerialize(String key) {
        Jedis jedis = getJedis();
        try {
            log.info("getFromRedis --> key:{}", key);
            byte[] data = jedis.get(key.getBytes());
            return SerializationUtils.deserialize(data);
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 泛型获取被序列化存储的值
     * @param clz
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getTByUnSerialize(Class<?> clz, String key) {
        Jedis jedis = getJedis();
        try {
            log.info("getTFromRedis --> clz:{},key:{}", clz.getName(), key);
            byte[] data = jedis.get(key.getBytes());
            return deserialize(clz, data);
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 获取list
     * @param key
     * @param targetClass
     * @param <T>
     * @return
     */
    public <T> List<T> getList(String key, Class<T> targetClass) {
        Jedis jedis = getJedis();
        List<T> list = null;
        try {
            String json = jedis.get(key);
            if (null == json || "".equals(json)) {
                return null;
            }
            list = JSON.parseArray(json, targetClass);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeRedis(jedis);
        }
        return list;
    }

    /**
     * 往redis插值，有失效时间
     * @param key
     * @param value
     * @param seconds
     */
    public void set(String key, String value, Integer seconds) {
        Jedis jedis = getJedis();
        try {
            log.info("set key --> {} : value:{} : timeoutSeconds:{}", key, value, seconds);
            jedis.set(key, value);
            if (seconds != null){
                jedis.expire(key, seconds);
            }
        } finally {
            closeRedis(jedis);
        }

    }

    /**
     * 序列化存值
     * @param key
     * @param obj
     * @param seconds
     */
    public void setBySerialize(String key, Object obj, Integer seconds) {
        if(null == obj){
            return;
        }
        Jedis jedis = getJedis();
        try {
            log.info("putObject --> key:{} , obj:{} ,timeout:{}", key, obj, seconds);
            jedis.set(key.getBytes(), SerializationUtils.serialize(obj));
            if (seconds != null) {
                jedis.expire(key, seconds);
            }
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 存list
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void setList(String key, List<T> value, Integer seconds) {
        if(null == value){
            return;
        }
        Jedis jedis = getJedis();
        try {
            jedis.set(key, JSON.toJSONString(value));// 存储JSON，方便redis查看
            if (seconds != null) {
                jedis.expire(key, seconds);
            }
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     *  模糊匹配所有符合条件的redis的key
     * @param str 匹配正则
     * @return
     */
    public Set<String> keys(String str) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            log.info("keys --> {}", str);
            Set<String> set = jedis.keys(str);
            return set;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            closeRedis(jedis);
        }

    }

    /**
     * 消息队列中，在key对应 list的头部添加字符串元素
     * @param key
     * @param lpush
     * @return
     */
    public Long lpush(String key, String... lpush) {
        Jedis jedis = getJedis();
        try {
            log.info("lpush --> key-->{},msgs:-->{}", key, lpush);
            return jedis.lpush(key, lpush);
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 消息队列中，在key对应 list 的尾部添加字符串元素
     * @param key
     * @param lpush
     * @return
     */
    public Long rpush(String key, String... lpush) {
        Jedis jedis = getJedis();
        try {
            log.info("rpush --> key:{},msgs:{}", key, lpush);
            return jedis.rpush(key, lpush);
        } finally {
            closeRedis(jedis);
        }
    }


    /**
     * 在队列中，从 对应key的list 的头部删除元素，并返回删除元素
     * @param key
     * @return
     */
    public String lpop(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            log.info("lpop --> {}", key);
            return jedis.lpop(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 在队列中，从 对应key的list 的尾部删除元素，并返回删除元素
     * @param key
     * @return
     */
    public String rpop(String key) {
        Jedis jedis = getJedis();
        try {
            log.info("rpop --> {}", key);
            return jedis.rpop(key);
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 消息队列中，在key对应 list的头部添加字符串元素
     * @param key
     * @param obj
     * @return
     */
    public Long lpushObj(String key, MQObj obj) {
        Jedis jedis = getJedis();
        try {
            log.info("lpushObj --> key:{} , msg:{}", key, obj);
            return jedis.lpush(key.getBytes(), SerializationUtils.serialize(obj));
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     *  在队列中，从 对应key的list 的尾部删除元素，并返回删除元素
     * @param key
     * @return
     */
    public MQObj rpopObj(String key) {
        Jedis jedis = getJedis();
        try {
            log.info("rpopObj --> key:{}", key);
            byte[] rpop = jedis.rpop(key.getBytes());
            return rpop != null ? (MQObj) SerializationUtils.deserialize(rpop) : null;
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 消息订阅
     * @param jedisPubSub
     * @param channels
     */
    public void subscribe(JedisPubSub jedisPubSub, String channels) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            log.info("subscribe --> channels:{}", channels);
            jedis.subscribe(jedisPubSub, channels);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 消息发布
     * @param channel
     * @param message
     */
    public void publish(String channel, String message) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            log.info("publish -->channel=={} message=={}", channel, message);
            jedis.publish(channel, message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 设置失效
     * @param key
     * @param timeout
     */
    public void expire(String key, Integer timeout) {
        Jedis jedis = getJedis();
        try {
            log.info("expire --> key:{} ,timeout:{}", key, timeout);
            if (timeout != null) {
                jedis.expire(key, timeout);
            }
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 删除对应key的value
     * @param key
     */
    public void del(String key) {
        Jedis jedis = getJedis();
        try {
            log.info("delFromRedis --> key:{}", key);
            jedis.del(key);
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 批量删除
     * @param pattern
     */
    public void deleteKeys(String pattern) {
        Set<String> allKeys = keys(pattern);
        log.info("deleteKeys --> allKeys:{}", allKeys.size());
        for (String key : allKeys) {
            del(key);
        }
    }

    /**
     * 判断对象是否存在
     * @param key
     * @return
     */
    public Boolean exists(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.exists(key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return false;
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 加一
     * @param key
     * @return
     */
    public Long incr(String key) {
        Jedis jedis = getJedis();
        try {
            log.info("incr --> key:{}", key);
            return jedis.incr(key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return 0L;
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 减一
     * @param key
     * @return
     */
    public Long decr(String key) {
        Jedis jedis = getJedis();
        try {
            log.info("decr --> key:{}", key);
            return jedis.decr(key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return 0L;
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     *  累加传递参数value的数据
     * @param key
     * @param value
     * @return
     */
    public Long incrby(String key, Long value) {
        Jedis jedis = getJedis();
        try {
            log.info("incrby --> key:{}", key);
            Long incrBy = jedis.incrBy(key, value);
            return incrBy;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return 0L;
        } finally {
            closeRedis(jedis);
        }
    }

    /**
     * 反序列化
     * @param clz
     * @param in
     * @param <T>
     * @return
     */
    public static <T> T deserialize(Class<?> clz, byte[] in) {
        T t = null;
        try {
            t = (T) clz.newInstance();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (in != null) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                while (true) {
                    t = (T) is.readObject();
                    break;
                }
                is.close();
                bis.close();
            }
        } catch (IOException e) {
            log.error(String.format("Caught IOException decoding %d bytes of data", in == null ? 0 : in.length) + e);
        } catch (ClassNotFoundException e) {
            log.error(String.format("Caught CNFE decoding %d bytes of data", in == null ? 0 : in.length) + e);
        } finally {
        }
        return t;
    }
}
