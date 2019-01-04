package com.zaiou.common.service;

import com.zaiou.common.constant.task.TaskRedisKey;
import com.zaiou.common.db.DaoManualProxyService;
import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.enums.task.JYMEnum;
import com.zaiou.common.exception.BussinessException;
import com.zaiou.common.mybatis.config.CustomSessionFactory;
import com.zaiou.common.mybatis.po.SysScheduleLog;
import com.zaiou.common.redis.service.JedisService;
import com.zaiou.common.support.CustomRestTemplate;
import com.zaiou.common.utils.DateUtils;
import com.zaiou.common.utils.MonitorUtils;
import com.zaiou.common.utils.StringUtils;
import com.zaiou.common.utils.Tools;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @auther: LB 2018/11/27 18:26
 * @modify: LB 2018/11/27 18:26
 */
@Service
@Primary
@Slf4j
@DisallowConcurrentExecution
public abstract class AbstractQuartzService implements Job {
    @Autowired
    protected CustomSessionFactory sessionFactory;

    @Autowired
    protected CustomRestTemplate restfulClient;

    @Autowired
    protected JedisService jedisService;

    @Value("${monitor.log.path:'${user.dir}/logs/'}")
    protected String monitorLogPath;

    @Autowired
    protected DaoManualProxyService daoManualProxyService;

    /* 逻辑业务处理 */
    protected abstract void execute(SqlSession sqlSession);

    public abstract JYMEnum transCode();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String sourceName = Thread.currentThread().getName();
        Thread.currentThread().setName(String.format("%s:%s", Tools.getOnlyPK(), transCode().getTrancode()));
        MonitorUtils.JobStart(getMonitorPath());
        log.info("执行定时任务[Quartz] {}-task-run-start，开始时间：" + DateUtils.dateToStrWithFormat2(new Date()), transCode());
        long start = System.currentTimeMillis();
        SqlSession sqlSession = sessionFactory.getManualSession();
        Long id = 0L;
        try {
            checkRepeatExecuted(sqlSession, transCode());
            SysScheduleLog po = insertSysScheduleLog(sqlSession);
            id = po.getId();
            execute(sqlSession);
            updateStatus(sqlSession, id, 1, "Success!");// 0失败 1成功 2执行中
        } catch (BussinessException e) {
            log.info("	======={}=======", e.getRespMsg());
            // 正常异常流程
            updateStatus(sqlSession, id, 0, e.getRespMsg());// 0失败 1成功 2执行中
        } catch (Throwable e) {
            log.error(transCode() + "-task-run-fail " + e.getMessage(), e);
            MonitorUtils.JobException(getMonitorPath(), e.getMessage());
            sessionFactory.rollbackTransaction(sqlSession);
            updateStatus(sqlSession, id, 0, e.getMessage());// 0失败 1成功 2执行中
        } finally {
            sessionFactory.commit(sqlSession);
            sessionFactory.releaseConnection(sqlSession);
            long end = System.currentTimeMillis();
            log.info(
                    "执行定时任务完毕[Quartz] {}-task-run-success cost:{}ms，结束时间：" + DateUtils.dateToStrWithFormat2(new Date()),
                    transCode(), end - start);
            MonitorUtils.JobEnd(getMonitorPath());
            Thread.currentThread().setName(sourceName);
        }

    }

    protected SysScheduleLog insertSysScheduleLog(SqlSession sqlSession) {
        SysScheduleLog po = new SysScheduleLog();
        po.setTaskKey(transCode().getTrancode());
        po.setTaskName(transCode().getMsg());
        po.setCreateTime(new Date());
        po.setExeDate(DateUtils.getCurZeroDate(new Date()));
        po.setStartTime(new Date());
        po.setResult(2);// 执行中
        daoManualProxyService.save(sqlSession, po);
        sessionFactory.commit(sqlSession);
        return po;
    }

    private void updateStatus(SqlSession sqlSession, Long id, Integer result, String remarks) {
        SysScheduleLog po = new SysScheduleLog();
        po.setId(id);
        po.setResult(result);// 0失败 1成功 2执行中
        po.setEndTime(new Date());
        if (remarks != null) {
            po.setRemark(remarks.length() > 500 ? remarks.substring(0, 500) : remarks);
        }
        daoManualProxyService.update(sqlSession, po);
    }

    // 默认为false，不执行检查当日定时任务是否执行过
    protected boolean executeOnceTimeInEveryDay() {
        return false;
    }

    protected String getMonitorPath() {
        log.debug(monitorLogPath);
        return String.format("%s/%s_%s.log", monitorLogPath, transCode().getTrancode(), DateUtils.getCurDate(DateUtils.format1));
    }

    /** 群组ID */
    public String getGroupId() {
        return "QuartzTask";
    }

    /** 默认执行时间每30分钟执行一次 */
    public String getCronExpression() {
        return "0 0/30 * * * ?";
    }

    /** 判断今天是否已经跑成功一次，若跑成功不执行 */
    protected void checkRepeatExecuted(SqlSession sqlSession, JYMEnum jym) {
        if (executeOnceTimeInEveryDay()) {
            SysScheduleLog sysScheduleLog = new SysScheduleLog();
            sysScheduleLog.setExeDate(DateUtils.getCurZeroDate(new Date()));
            sysScheduleLog.setResult(1);// 1、执行成功
            sysScheduleLog.setTaskKey(jym.getTrancode());
            List<?> list = daoManualProxyService.findList(sqlSession, sysScheduleLog);
            if (list != null && !list.isEmpty()) {
                throw new BussinessException(ResultInfo.TASK_9000, jym.getTrancode());
            }
        }
    }

    /** 判断今天是否已经跑成功一次，若跑成功一次当天则不执行 */
    protected void checkRepeatExecutedInRedis() {
        // 判断今天是否已经跑成功一次，若跑成功一次当天则不执行
        String taskKey = TaskRedisKey.getTaskKey(DateUtils.getCurDate(DateUtils.format1), transCode());
        String taskKeyVal = jedisService.get(taskKey);
        if (StringUtils.isNotEmpty(taskKeyVal)) {
            throw new BussinessException(ResultInfo.TASK_9000, taskKey);
        }
    }

    /** 判断今天是否已经跑成功一次，若跑成功一次当天则不执行 */
    protected void checkRepeatExecutedInRedis(JYMEnum jym) {
        // 判断今天是否已经跑成功一次，若跑成功一次当天则不执行
        String taskKey = TaskRedisKey.getTaskKey(DateUtils.getCurDate(DateUtils.format1), jym);
        String taskKeyVal = jedisService.get(taskKey);
        if (StringUtils.isNotEmpty(taskKeyVal)) {
            throw new BussinessException(ResultInfo.TASK_9000, taskKey);
        }
    }

    /** 判断今天是否已经跑成功一次，若跑不成功不执行-依赖的任务 */
    protected void checkFinishedTaskInRedis(JYMEnum jym) {
        // 判断今天是否已经跑成功一次，若跑成功一次当天则不执行
        String taskKey = TaskRedisKey.getTaskKey(DateUtils.getCurDate(DateUtils.format1), jym);
        String taskKeyVal = jedisService.get(taskKey);
        if (StringUtils.isEmpty(taskKeyVal)) {
            throw new BussinessException(ResultInfo.TASK_9001, taskKey);
        }
    }

    /** 判断今天是否已经跑成功一次，若跑不成功不执行-依赖的任务 */
    protected void checkFinishedTaskInRedis(String taskKey) {
        // 判断今天是否已经跑成功一次，若跑成功一次当天则不执行
        String taskKeyVal = jedisService.get(taskKey);
        if (StringUtils.isEmpty(taskKeyVal)) {
            throw new BussinessException(ResultInfo.TASK_9001, taskKey);
        }
    }

    /** 插入执行记录 */
    protected void insertFinishedTaskInRedis() {
        String taskKey = TaskRedisKey.getTaskKey(DateUtils.getCurDate(DateUtils.format1), transCode());
        jedisService.set(taskKey, "true", 86400 * 7);// 设置有效期7天
    }

    /** 插入执行记录 */
    protected void insertFinishedTaskInRedis(JYMEnum jym) {
        String taskKey = TaskRedisKey.getTaskKey(DateUtils.getCurDate(DateUtils.format1), jym);
        jedisService.set(taskKey, "true", 86400 * 7);// 设置有效期7天
    }
}