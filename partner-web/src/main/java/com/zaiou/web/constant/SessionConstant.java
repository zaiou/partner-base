package com.zaiou.web.constant;

import org.springframework.stereotype.Component;

/**
 * @Description: 会话相关定义常量
 * @auther: LB 2018/9/19 10:31
 * @modify: LB 2018/9/19 10:31
 */
@Component
public class SessionConstant {

    // 令牌ID
    public static final String TOKEN_ID = "token";

    //currentID  根据currentSessionId获取用户信息
    public static final String CURRENT_SESSION_ID = "currentSessionId";

}
