package com.zaiou.web.task;

import com.zaiou.common.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @auther: LB 2019/1/3 09:48
 * @modify: LB 2019/1/3 09:48
 */
@Slf4j
@Component
public abstract class AbstractTask {

    protected abstract String getTaskName();

    protected abstract void execute();

    public void job(){
        String sourceName = Thread.currentThread().getName();
        Thread.currentThread().setName(String.format("%s:%s", TokenUtils.getOnlyPK(), getTaskName()));
        long time1 = System.currentTimeMillis();
        log.info("start "+getTaskName()+"!--------------");

        execute();

        long time2 = System.currentTimeMillis();
        log.info("end "+getTaskName()+"! TIME="+(time2-time1)+"----------------");
        Thread.currentThread().setName(sourceName);
    }


}
