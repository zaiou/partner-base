package com.zaiou.web.mybatis.mapper;

import com.zaiou.common.mybatis.po.SysUser;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserMapper {
    /**
     * 添加用户
     * @param sysUser
     */
    public void addUser(SysUser sysUser);

    /**
     * 根据userCode查找用户信息
     */
    public SysUser getSysUserByUserCode(String userCode);
}