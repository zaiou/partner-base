package com.zaiou.task.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * @Description: Job任务工厂代理类
 * @auther: LB 2018/11/26 16:35
 * @modify: LB 2018/11/26 16:35
 */
@Component
@Slf4j
public class QuartzJobFactory extends AdaptableJobFactory {

        @Autowired
        private AutowireCapableBeanFactory capableBeanFactory;

        /**
         * 自动创建Job实例
         * @param bundle
         * @return
         * @throws Exception
         */
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object jobInstance = super.createJobInstance(bundle);
            capableBeanFactory.autowireBean(jobInstance);
            log.info("创建job实例====-======"+jobInstance);
            return jobInstance;
        }
}
