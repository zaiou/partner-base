package com.zaiou.web.service.system;

import com.zaiou.web.common.bean.CurrentUser;
import com.zaiou.web.vo.system.SysUserReq;

/**
 * @Description:  系统用户
 * @auther: LB 2018/9/20 16:59
 * @modify: LB 2018/9/20 16:59
 */
public interface UserService {
    public void addUser(SysUserReq sysUserReq, CurrentUser currentUser);
}
