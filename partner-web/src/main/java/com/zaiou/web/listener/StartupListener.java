package com.zaiou.web.listener;

import com.zaiou.web.redis.subscribe.SubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * @Description: 应用启动完毕所有bean处理完成后执行此方法,预加载数据在此处理
 * @auther: LB 2018/9/6 09:52
 * @modify: LB 2018/9/6 09:52
 */
@Service
@Slf4j
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * 项目所在文件夹
     */
    @Value("#{systemProperties['user.dir']}")
    private String userdir;

    @Autowired
    private SubscribeService subscribeService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("初始化web监听器---------------------------- ApplicationListener onApplicationEvent " + userdir);

        // 启动模型监听队列
        subscribeService.subscribe();

    }
}
