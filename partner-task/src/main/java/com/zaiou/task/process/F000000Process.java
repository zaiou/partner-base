package com.zaiou.task.process;

import com.zaiou.common.enums.task.JYMEnum;
import com.zaiou.common.service.AbstractQuartzService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.stereotype.Component;

/**
 * @Description: 测试
 * @auther: LB 2018/11/29 18:18
 * @modify: LB 2018/11/29 18:18
 */
@Slf4j
@Component
@DisallowConcurrentExecution
public class F000000Process extends AbstractQuartzService {
    @Override
    protected void execute(SqlSession sqlSession) {
        log.info("定时任务业务执行测试");
    }

    @Override
    public JYMEnum transCode() {
        return JYMEnum.F000000;
    }

    //每10分钟正跑
    @Override
    public String getCronExpression() {
        return "0/5 * * * * ?";
    }
}
