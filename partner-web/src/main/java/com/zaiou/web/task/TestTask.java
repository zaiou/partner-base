package com.zaiou.web.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @auther: LB 2019/1/4 09:22
 * @modify: LB 2019/1/4 09:22
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "task.enable", havingValue = "true", matchIfMissing = false)
public class TestTask extends AbstractTask{

    @Scheduled(cron = "${task.cron.test:*/10 * * * * ?}")
    public void job(){
        super.job();
    }


    @Override
    protected void execute() {
        log.info("定时任务测试");
    }

    @Override
    protected String getTaskName() {
        return "TestTask";
    }
}
