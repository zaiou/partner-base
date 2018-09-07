package com.zaiou.common.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;

/**
 * @Description: reids配置
 * @auther: LB 2018/8/24 11:39
 * @modify: LB 2018/8/24 11:39
 */
@Configuration
@ConditionalOnProperty(name = "spring.redis.enable", havingValue = "true", matchIfMissing = true)
@EnableCaching
@Slf4j
public class RedisConfiguration extends CachingConfigurerSupport {

    @Value("${spring.redis.host:127.0.0.1}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.timeout:2000}")
    private int timeout;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.database:0}")
    private int database;

    @Value("${spring.redis.pool.max-active:12}")
    private int maxActive;
    @Value("${spring.redis.pool.max-wait:5}")
    private int maxWait;
    @Value("${spring.redis.pool.max-idle:12}")
    private int maxIdle;
    @Value("${spring.redis.pool.min-idle:5}")
    private int minIdle;

    @Value("${spring.redis.pool.testOnReturn:true}")
    private boolean testOnReturn;
    @Value("${spring.redis.pool.testOnBorrow:true}")
    private boolean testOnBorrow;

    @Value("${spring.redis.pool.testWhileIdle:true}")
    private boolean testWhileIdle;

    @Value("${spring.redis.pool.defaultexpiration:60}") // 单位秒
    private int defaultExpiration;

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisPoolConfig getRedisConfig() {
        return new JedisPoolConfig();
    }

    /***
     * 初始化jedis连接池
     * @return
     */
    @Bean
    public JedisPool redisPoolFactory() {
        log.info("初始化jedis连接池");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setBlockWhenExhausted(true);// 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时,
        // 默认true
        config.setMaxTotal(maxActive);// # 连接池最大连接数（使用负值表示没有限制）
        config.setMaxIdle(maxIdle);// 最大空闲连接数, 默认8个
        config.setMinIdle(minIdle);// 最小空闲连接数, 默认0
        // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,
        // 默认-1
        config.setMaxWaitMillis(maxWait);// # 连接池最大阻塞等待时间（使用负值表示没有限制）
        config.setTestOnReturn(testOnReturn);
        config.setTestOnBorrow(testOnBorrow);// /在获取连接的时候检查有效性, 默认false
        config.setTestWhileIdle(testWhileIdle);// 在空闲时检查有效性, 默认false
        config.setTestOnCreate(true);
        JedisPool jedisPool = null;
        if (StringUtils.isNotBlank(password)) {
            jedisPool = new JedisPool(config, host, port, timeout, password, database);
        } else {
            jedisPool = new JedisPool(config, host, port, timeout, null, database);
        }
        log.info("JedisPool注入成功！！---===>"+host + ":" + port);

        log.info("JedisPool获取jedis，测试连接!!!");
        Jedis jedis = jedisPool.getResource();
        log.info("JedisPool 测试连接！" + jedis + "," + jedis.getDB());
        jedis.close();
        log.info("JedisPool 关闭连接！" + jedis);
        return jedisPool;
    }

    /**
     * jedis连接工厂
     * spring-data-redis方式
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(host);
        factory.setPort(port);
        factory.setPassword(password);
        factory.setTimeout(timeout);
        factory.setPoolConfig(getRedisConfig());
        factory.setUsePool(true);
        factory.setDatabase(database);
        factory.afterPropertiesSet();
        log.info("jedis连接工厂连接 redis");
        return factory;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        // redisTemplate.afterPropertiesSet();

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate.setKeySerializer(jackson2JsonRedisSerializer);
        // redisTemplate.setStringSerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new RedisObjectSerializer());
        redisTemplate.afterPropertiesSet();
        log.info("redis模板==="+redisTemplate);
        return redisTemplate;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public CacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
        redisCacheManager.setDefaultExpiration(defaultExpiration);
        return redisCacheManager;
    }

    /**
     * 生成key的策略
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public KeyGenerator keyGenerator() {
        log.info("==============生成key的策略====================");
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                log.info("key的值为--------------" + sb.toString());
                return sb.toString();
            }
        };
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(String.format("redis://%s:%s", host, port)).setDatabase(database)
                .setConnectionPoolSize(maxActive).setConnectionMinimumIdleSize(minIdle).setTimeout(timeout)
                .setKeepAlive(true).setConnectTimeout(timeout * 10000)
                .setPassword(StringUtils.isNotBlank(password) ? password : null).setTcpNoDelay(true);
        log.info("====------------redission设置----------======");
        return Redisson.create(config);
    }


}
