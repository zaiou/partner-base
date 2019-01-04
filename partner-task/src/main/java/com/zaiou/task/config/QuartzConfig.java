package com.zaiou.task.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * @Description: Quartz定时任务配置(集群)
 * @auther: LB 2018/11/26 15:49
 * @modify: LB 2018/11/26 15:49
 */
@Configuration
@Slf4j
public class QuartzConfig {

    /**调度标识名,区分特定调度器实例*/
    @Value("${quartz.scheduler.instanceName:partner-quartz-scheduler}")
    private String quartzInstanceName;
    @Autowired
    private DataSource dataSource;

    /**
     * 调度器属性配置
     * @return
     */
    private Properties quartzProperties() {
        Properties prop = new Properties();
        /**调度器属性*/
        // #调度标识名 集群中每一个实例都必须使用相同的名称
        prop.put("org.quartz.scheduler.instanceName", quartzInstanceName);
        // #ID设置为自动获取 每一个必须不同
        prop.put("org.quartz.scheduler.instanceId", "AUTO");
        // prop.put("org.quartz.scheduler.skipUpdateCheck", "true");
        // prop.put("org.quartz.scheduler.jmx.export", "true");

        /**作业存储设置*/
        // #数据保存方式为数据库持久化
        prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        // #数据库代理类，一般org.quartz.impl.jdbcjobstore.StdJDBCDelegate可以满足大部分数据库
        prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        // #表的前缀，默认QRTZ_
        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        // #是否加入集群
        prop.put("org.quartz.jobStore.isClustered", true);
        // #调度实例失效的检查时间间隔
        prop.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
        // #数据库别名 随便取
        prop.put("org.quartz.jobStore.dataSource", "quartz");
        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");
        // # 信息保存时间 默认值60秒
        //	prop.put("org.quartz.jobStore.misfireThreshold", "600000");
        prop.put("org.quartz.jobStore.txIsolationLevelSerializable", "true");
        prop.put("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS WHERE LOCK_NAME = ? FOR UPDATE");
        // JobDataMaps是否都为String类型
        prop.put("org.quartz.jobStore.useProperties", false);

        /**线程配置*/
        // 线程池的实现类（一般使用SimpleThreadPool即可满足几乎所有用户的需求）
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        // #指定线程数，至少为1（无默认值）(一般设置为1-100直接的整数合适)
        prop.put("org.quartz.threadPool.threadCount", "30");
        // #设置线程的优先级（最大为java.lang.Thread.MAX_PRIORITY
        prop.put("org.quartz.threadPool.threadPriority", "5");
        // 最小为Thread.MIN_PRIORITY 1，默认为5）
        prop.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");

        prop.put("org.quartz.plugin.triggHistory.class", "org.quartz.plugins.history.LoggingJobHistoryPlugin");
        prop.put("org.quartz.plugin.shutdownhook.class", "org.quartz.plugins.management.ShutdownHookPlugin");
        prop.put("org.quartz.plugin.shutdownhook.cleanShutdown", "true");
        return prop;
    }

    /**
     * 自定义定时任务工厂类
     * @param myJobFactory
     * @return
     * @throws Exception
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(QuartzJobFactory myJobFactory) throws Exception {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        // 用于quartz集群,加载quartz数据源
        schedulerFactoryBean.setDataSource(dataSource);
        // 设置是否任意一个已定义的Job会覆盖现在的Job。默认为false，即已定义的Job不会覆盖现有的Job。
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setJobFactory(myJobFactory);
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        // QuartzScheduler 延时启动，应用启动完5秒后 QuartzScheduler 再启动
        schedulerFactoryBean.setStartupDelay(5);
        return schedulerFactoryBean;
    }

    /**
     * 自定义定时任务调度器
     * @param schedulerFactoryBean
     * @return
     * @throws IOException
     * @throws SchedulerException
     */
    @Bean(name = "scheduler")
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws IOException, SchedulerException {
        log.info("定时任务调度器=================------------========== start");
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.start();
        return scheduler;
    }

}
