package com.zaiou.common.redis.config;

import com.zaiou.common.redis.service.impl.AbstractTopicProcessServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @Description: redis topic 消息订阅监听器配置
 * @auther: LB 2018/10/10 11:26
 * @modify: LB 2018/10/10 11:26
 */
//@Configuration  (预留，消息订阅的时候用)
@ConditionalOnProperty(name = "redis.topic.enable", havingValue = "true", matchIfMissing = false)
@Slf4j
public class RedisSubscriptionListenerConfig {

    // 初始化监听器
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            AbstractTopicProcessServiceImpl... tops) {
        log.info("初始化......RedisMessageListenerContainer......");
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        for (AbstractTopicProcessServiceImpl top : tops) {
            log.info("初始化{}", top.key());
            container.addMessageListener(new MessageListenerAdapter(top, "receivedMessage"),
                    new PatternTopic(top.key()));
        }
        return container;
    }

}
