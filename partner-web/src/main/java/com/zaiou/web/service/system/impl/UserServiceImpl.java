package com.zaiou.web.service.system.impl;

import com.zaiou.common.mybatis.po.SysUser;
import com.zaiou.common.utils.ConvertObjectUtils;
import com.zaiou.common.utils.MD5Utils;
import com.zaiou.web.common.bean.CurrentUser;
import com.zaiou.web.mybatis.mapper.SysUserMapper;
import com.zaiou.web.service.system.UserService;
import com.zaiou.web.vo.system.SysUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Description: 系统用户
 * @auther: LB 2018/9/20 17:30
 * @modify: LB 2018/9/20 17:30
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 添加用户
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void addUser(SysUserReq sysUserReq, CurrentUser currentUser){
        // vo转mapper实体
        SysUser sysUser = ConvertObjectUtils.NormalConvertObject(sysUserReq, SysUser.class);
        // 盐
        String salt = MD5Utils.generateSalt();
        sysUser.setSalt(salt);
        // 密码
        String userPassword = MD5Utils.hmacMD5(sysUser.getUserPassword(), salt);
        sysUser.setUserPassword(userPassword);
        // 用户状态
        sysUser.setStatus(Integer.valueOf(sysUserReq.getStatus()));
        // 创建人
//        sysUser.setCreateUser(currentUser.getUserCode());
        // 创建时间
        sysUser.setCreateTime(new Date());
        // 添加用户
        sysUserMapper.addUser(sysUser);

    }
}
