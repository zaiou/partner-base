package com.zaiou.web.mybatis.mapper;

import com.zaiou.common.mybatis.po.SysParams;
import java.util.List;

public interface SysParamsMapper {
    List<SysParams> selectAll();
}