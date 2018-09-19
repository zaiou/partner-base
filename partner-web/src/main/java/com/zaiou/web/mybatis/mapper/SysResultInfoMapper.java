package com.zaiou.web.mybatis.mapper;

import com.zaiou.common.mybatis.po.SysResultInfo;
import java.util.List;

public interface SysResultInfoMapper {
    List<SysResultInfo> selectAll();
}