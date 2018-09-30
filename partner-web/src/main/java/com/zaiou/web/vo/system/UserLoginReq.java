package com.zaiou.web.vo.system;

import com.zaiou.common.vo.WebRequest;
import com.zaiou.web.annotation.Log;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @Description: 用户登录请求类
 * @auther: LB 2018/9/29 11:30
 * @modify: LB 2018/9/29 11:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginReq extends WebRequest {
    private static final long serialVersionUID = 1L;
    @Log(fileName = "用户")
    @NotEmpty(message = "0001,登录账号 ")
    private String userCode;
    @NotEmpty(message = "0001,登录密码")
    private String password;
}
