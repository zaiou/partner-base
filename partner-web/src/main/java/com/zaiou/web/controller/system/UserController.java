package com.zaiou.web.controller.system;

import com.alibaba.druid.util.StringUtils;
import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import com.zaiou.web.annotation.CurrentUserSeession;
import com.zaiou.web.common.bean.CurrentUser;
import com.zaiou.web.common.bean.RespBody;
import com.zaiou.web.controller.common.BaseController;
import com.zaiou.web.service.system.UserService;
import com.zaiou.web.validate.group.UpdateValidate;
import com.zaiou.web.vo.system.SysUserReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
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
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/addUser", method = { RequestMethod.POST })
    public @ResponseBody
    RespBody addUser(@CurrentUserSeession CurrentUser user,
                     @RequestBody @Validated(value = { UpdateValidate.class }) SysUserReq req, BindingResult result,
                     HttpServletRequest httpServletRequest) {
        try {
            log.info("========用户添加开始========");
            checkBean(result);
//            req.setRoleName(SysRoleEnum.getMsgByCode(req.getRoleCode()));
            userService.addUser(req, user);
            return assemble(ResultInfo.SUCCESS);
        } finally {
            log.info("========用户添加结束========");
        }
    }

    protected void checkBean(BindingResult result) {
        if (result.hasErrors()) {
            String[] errorMsgs = result.getFieldError().getDefaultMessage().split(",");
            ResultInfo resultInfo = ResultInfo.getResultCode("01", errorMsgs[0]);
            assert resultInfo != null;
            throw new BussinessException(resultInfo, ArrayUtils.remove(errorMsgs, 0));
        }
    }
}
