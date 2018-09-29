package com.zaiou.web.mybatis.mapper;

import com.zaiou.common.mybatis.po.SysOperateLog;
import java.util.List;

public interface SysOperateLogMapper {
    List<SysOperateLog> selectAll();
}