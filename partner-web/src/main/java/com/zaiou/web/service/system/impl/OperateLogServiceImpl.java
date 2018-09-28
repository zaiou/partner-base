package com.zaiou.web.service.system.impl;

import com.zaiou.common.db.DaoProxyService;
import com.zaiou.common.mybatis.po.SysOperateLog;
import com.zaiou.web.service.system.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @Description: 系统操作日志
 * @auther: LB 2018/9/27 10:41
 * @modify: LB 2018/9/27 10:41
 */
@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    private DaoProxyService daoProxyService;

    /**
     * 添加日志
     * @param operateLog
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public void addLog(SysOperateLog operateLog) {
        daoProxyService.save(operateLog);
    }
}
