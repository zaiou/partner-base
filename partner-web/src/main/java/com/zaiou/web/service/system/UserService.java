package com.zaiou.web.service.system;

import com.zaiou.web.common.bean.CurrentUser;
import com.zaiou.web.vo.system.SysUserReq;
import com.zaiou.web.vo.system.UserLoginReq;
import com.zaiou.web.vo.system.UserLoginResp;

/**
 * @Description:  系统用户
 * @auther: LB 2018/9/20 16:59
 * @modify: LB 2018/9/20 16:59
 */
public interface UserService {
    /**
     * 添加用户
     * @param sysUserReq
     * @param currentUser
     */
    public void addUser(SysUserReq sysUserReq, CurrentUser currentUser);

    /**
     * 用户登录
     * @param userLoginReq
     * @return
     */
    public UserLoginResp login(UserLoginReq userLoginReq);
}
