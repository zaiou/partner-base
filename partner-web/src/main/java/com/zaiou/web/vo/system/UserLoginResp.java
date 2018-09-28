package com.zaiou.web.vo.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 用户登录vo
 * @auther: LB 2018/9/25 17:18
 * @modify: LB 2018/9/25 17:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResp implements Serializable {
    private static final long serialVersionUID = 4599578461548866251L;

    private String token;// 会话id
    private String message;
    private Long userId;// 用户ID
    private String userCode;// 登录账号
    private String userName;// 用户名
    private Integer status;// 用户状态
    private Long roleId;// 角色ID
    private String roleCode;//用户角色
}
