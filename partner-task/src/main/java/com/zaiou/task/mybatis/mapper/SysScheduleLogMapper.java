package com.zaiou.task.mybatis.mapper;

import com.zaiou.common.mybatis.po.SysScheduleLog;
import java.util.List;

public interface SysScheduleLogMapper {
    List<SysScheduleLog> selectAll();
}