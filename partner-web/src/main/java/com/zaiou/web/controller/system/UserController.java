package com.zaiou.web.controller.system;

import com.zaiou.common.enums.ResultInfo;
import com.zaiou.web.annotation.CurrentUserSeession;
import com.zaiou.web.common.bean.CurrentUser;
import com.zaiou.web.common.bean.RespBody;
import com.zaiou.web.common.utils.R;
import com.zaiou.web.service.system.UserService;
import com.zaiou.web.validate.group.SaveValidate;
import com.zaiou.web.vo.system.SysUserReq;
import com.zaiou.web.vo.system.UserLoginReq;
import com.zaiou.web.vo.system.UserLoginResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 用户管理
 * @auther: LB 2018/9/18 16:37
 * @modify: LB 2018/9/18 16:37
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 添加用户
     * @param user
     * @param httpServletRequest
     * @param sysUserReq
     * @param result
     * @return
     */
    @RequestMapping(value = "/addUser", method = { RequestMethod.POST })
    public @ResponseBody
    RespBody addUser(@CurrentUserSeession CurrentUser user,HttpServletRequest httpServletRequest,
                     @RequestBody @Validated(value = { SaveValidate.class }) SysUserReq sysUserReq,
                     BindingResult result) {
        try {
            log.info("========用户添加开始========");
//            req.setRoleName(SysRoleEnum.getMsgByCode(req.getRoleCode()));
            userService.addUser(sysUserReq, user);
            return R.info(ResultInfo.SUCCESS);
        } finally {
            log.info("========用户添加结束========");
        }
    }

    /**
     * 用户登录
     * @param httpServletRequest
     * @param result
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = { RequestMethod.POST })
    public @ResponseBody RespBody login(HttpServletRequest httpServletRequest,
                                        @RequestBody @Validated UserLoginReq userLoginReq, BindingResult result) {
        try {
            log.info("========用户登录开始========");
            UserLoginResp loginResp = userService.login(userLoginReq);
            httpServletRequest.setAttribute("token", loginResp.getToken());
            return R.success(loginResp);
        } finally {
            log.info("========用户登录结束========");
        }
    }
}
