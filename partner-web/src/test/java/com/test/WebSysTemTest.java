package com.test;

import com.zaiou.web.WebApplication;
import com.zaiou.web.common.bean.CurrentUser;
import com.zaiou.web.service.system.UserService;
import com.zaiou.web.vo.system.SysUserReq;
import com.zaiou.web.vo.system.UserLoginReq;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @auther: LB 2018/9/21 10:08
 * @modify: LB 2018/9/21 10:08
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = WebApplication.class)
@Slf4j
public class WebSysTemTest {

    @Autowired
    private UserService userService;

    /**
     * 添加用户
     */
    @Test
    public void addUser(){
        SysUserReq sysUserReq = new SysUserReq();
        sysUserReq.setUserCode("admin");
        sysUserReq.setUserName("管理员");
        sysUserReq.setUserPassword("123456");
        sysUserReq.setIdCard("123456789");
        sysUserReq.setMobile("18756921322");
        sysUserReq.setStatus("1");

        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserCode("654321");
        userService.addUser(sysUserReq, currentUser);
    }

    /**
     * 用户登录
     */
    @Test
    public void login(){
        UserLoginReq userLoginReq = new UserLoginReq();
        userLoginReq.setUserCode("admin");
        userLoginReq.setPassword("123456");
        userService.login(userLoginReq);
    }
}
