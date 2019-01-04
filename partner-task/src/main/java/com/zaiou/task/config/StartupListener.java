package com.zaiou.task.config;

import com.zaiou.common.enums.task.JYMEnum;
import com.zaiou.common.service.AbstractQuartzService;
import com.zaiou.common.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @Description: 应用启动完毕后执行此方法,预加载数据在此处理
 * @auther: LB 2018/11/27 17:27
 * @modify: LB 2018/11/27 17:27
 */
@Service
@Slf4j
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    // 系统属性配置
    @Value("#{systemProperties['user.dir']}")
    private String userdir;
    // 系统属性配置
    @Value("${task.auto-loading:true}")
    private boolean autoLoading;
    //任务调度实例
    @Autowired
    private Scheduler scheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug("---------------------------- ApplicationListener onApplicationEvent " + userdir);
        System.setProperty("WORKDIR", userdir);
        log.info("deploy path:" + userdir);
        // dev不自动加载
        if (autoLoading) {
            // 定时任务Job加载
            Map<String, AbstractQuartzService> map = SpringContextHolder.getApplicationContext()
                    .getBeansOfType(AbstractQuartzService.class);
            Collection<AbstractQuartzService> values = map.values();
            log.debug("QuartzService:----->{}", values);
            for (AbstractQuartzService service : values) {
                startTask(service);
            }
        }
    }

    /**
     * @Title: startTask
     * @Description: 启动定时任务实例
     * @author: ivan 2018年7月31日
     * @modify: ivan 2018年7月31日
     * @param service
     */
    @SuppressWarnings("unchecked")
    private void startTask(AbstractQuartzService service) {
        try {
            JYMEnum jym = service.transCode();
            String jobGroup = service.getGroupId();
            String cronExpression = service.getCronExpression();
            String jobDescription = jym.getMsg();
            String jobCls = service.getClass().getName();
            String createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            log.info("初始化数据: groupId:{}, code:{} ,msg:{} ,cls:{} ,cronExpression:{}", jobGroup, jym.getTrancode(),
                    jym.getMsg(), jobCls, cronExpression);

            TriggerKey triggerKey = TriggerKey.triggerKey(jym.getTrancode(), jobGroup);
            JobKey jobKey = JobKey.jobKey(jym.getTrancode(), jobGroup);
            if (scheduler.checkExists(triggerKey) || scheduler.checkExists(jobKey)) {
                log.info("初始化数据: triggerKey[{}]||jobKey[{}]已经存在!,不在初始化!!!", triggerKey, jobKey);
                return;
            }
            CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(cronExpression)
                    .withMisfireHandlingInstructionDoNothing();
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime)
                    .withSchedule(schedBuilder).build();

            Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(jobCls);
            JobDetail jobDetail = JobBuilder.newJob(clazz).withDescription(jobDescription)
                    .withIdentity(jym.getTrancode(), jobGroup).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException | ClassNotFoundException e) {
            log.error("类名不存在或执行表达式错误,exception:{}", e.getMessage());
        }
    }
}
