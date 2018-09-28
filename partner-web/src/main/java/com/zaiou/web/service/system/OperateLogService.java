package com.zaiou.web.service.system;

import com.zaiou.common.mybatis.po.SysOperateLog;

/**
 * @Description: 系统操作日志
 * @auther: LB 2018/9/27 10:41
 * @modify: LB 2018/9/27 10:41
 */
public interface OperateLogService {
    public void addLog(SysOperateLog operateLog);
}
