package com.zaiou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 日志操作枚举
 * @auther: LB 2018/9/25 15:51
 * @modify: LB 2018/9/25 15:51
 */
@Getter
@AllArgsConstructor
public enum LogOperateEnum {

    user_add("用户", "新增用户", "/web/user/addUser", false),

    user_login("登录", "登录成功", "/web/user/login", false),
    user_login_err("登录", "密码错误", "", false),

    user_logout("退出", "退出成功", "/web/user/logout", true)
    ;

    private String type;//操作类型
    private String func;//操作功能
    private String uri;//访问uri
    private Boolean isDel;//是否是删除或取消操作，如果是删除或取消操作，控制层需要压入数据到RespBody对象data中

    public static LogOperateEnum getEnum(String uri) {
        for (LogOperateEnum loe : values()) {
            if (loe.uri.equals(uri)) {
                return loe;
            }
        }
        return null;
    }
}
