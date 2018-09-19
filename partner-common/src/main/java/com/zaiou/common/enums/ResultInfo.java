package com.zaiou.common.enums;

import com.zaiou.common.service.CacheService;
import com.zaiou.common.utils.SpringContextHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.MessageFormat;

/**
 * @Description: 系统结果信息
 * @auther: LB 2018/9/19 12:09
 * @modify: LB 2018/9/19 12:09
 */
@Getter
@AllArgsConstructor
public enum  ResultInfo {
    SUCCESS("00", "0000", "操作成功"),

    /**登录权限错误码**/
    WEB_1000	("01","1000", "登录超时,请重新登录")
    ;



    public String getCacheMsg(Object... params) {
        String msg = SpringContextHolder.getBean(CacheService.class).getResultMsg(system, code);
        return MessageFormat.format(msg, params);

    }

    private String system;
    private String code;
    private String msg;
}
