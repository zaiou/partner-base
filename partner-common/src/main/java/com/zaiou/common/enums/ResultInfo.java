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
    NOT_NULL("00", "9001", "{0}参数不能为空"),

    /** web端 **/
    //BOSS系统异常
    WEB_SYS_ERROR("01", "9999", "管理端系统异常"),
    //登录权限错误码
    WEB_1000	("01","1000", "登录超时,请重新登录"),
    WEB_1005	("01","1005","登录账号或密码错误，错误6次账号将被锁定"),
    ;

    public String getCode() {
        return system + code;
    }

    public String getCacheMsg(Object... params) {
        String msg = SpringContextHolder.getBean(CacheService.class).getResultMsg(system, code);
        return MessageFormat.format(msg, params);
    }

    public static ResultInfo getResultCode(String system, String code) {
        for (ResultInfo resultInfo : ResultInfo.values()) {
            if (resultInfo.system.equals(system) && resultInfo.code.equals(code)) {
                return resultInfo;
            }
        }
        return null;
    }

    private String system;
    private String code;
    private String msg;
}
