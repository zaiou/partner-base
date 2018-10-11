package com.zaiou.web.interceptor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 封装忽略拦截会话数据
 * @auther: LB 2018/10/8 19:30
 * @modify: LB 2018/10/8 19:30
 */
@Component
@ConfigurationProperties(prefix = "auth")
@PropertySource("classpath:authIgnore.properties")
public class AuthIgnoreResources {
    // 忽略会话
    private List<String> ignoreSessions = new ArrayList<String>();
    // 忽略权限，但是需要会话信息
    private List<String> ignoreAuth = new ArrayList<String>();

    public List<String> getIgnoreSessions() {
        return ignoreSessions;
    }

    public List<String> getIgnoreAuth() {
        return ignoreAuth;
    }
}
