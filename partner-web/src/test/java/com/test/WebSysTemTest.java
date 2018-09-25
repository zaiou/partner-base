package com.test;

import com.zaiou.web.WebApplication;
import com.zaiou.web.common.bean.CurrentUser;
import com.zaiou.web.service.system.UserService;
import com.zaiou.web.vo.system.SysUserVo;
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
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.setUserCode("admin");
        sysUserVo.setUserName("管理员");
        sysUserVo.setUserPassword("123456");
        sysUserVo.setIdCard("123456789");
        sysUserVo.setMobile("18756921322");
        sysUserVo.setStatus("1");

        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserCode("654321");
        userService.addUser(sysUserVo, currentUser);
    }
}
