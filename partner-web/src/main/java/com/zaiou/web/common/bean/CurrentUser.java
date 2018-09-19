package com.zaiou.web.common.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Description: 缓存当前用户数据
 * @auther: LB 2018/9/18 19:29
 * @modify: LB 2018/9/18 19:29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Component
@Scope("prototype")
public class CurrentUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String token;// 会话id
    private Long userId;// 用户ID
    private String userCode;// 登录账号
    private String userName;// 用户名
    private Integer status;// 用户状态
    private Long roleId;// 角色ID
    private String roleCode;//用户角色
}
