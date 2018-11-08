package com.zaiou.datamodel.config;

import com.zaiou.datamodel.redis.subscribe.SubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * @Description: 应用启动后，预加载数据
 * 应用启动完毕后执行此方法,预加载数据在此处理 不然会出现依赖问题
 * @auther: LB 2018/10/31 10:20
 * @modify: LB 2018/10/31 10:20
 */
@Service
@Slf4j
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    // 系统属性配置
    @Value("#{systemProperties['user.dir']}")
    private String userdir;

    @Autowired
    private SubscribeService subscribeService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.debug("---------------------------- ApplicationListener onApplicationEvent " + userdir);
        System.setProperty("WORKDIR", userdir);
        // 启动模型监听队列
        subscribeService.subscribe();
    }
}
