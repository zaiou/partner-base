package com.zaiou.web.controller.common;

import com.zaiou.common.enums.ResultInfo;
import com.zaiou.web.common.bean.RespBody;

/**
 * @Description: 基础controller
 * @auther: LB 2018/9/27 14:49
 * @modify: LB 2018/9/27 14:49
 */
public class BaseController {
    /**
     * 响应结果给前端
     * @param errorEnum
     * @return
     */
    protected RespBody assemble(ResultInfo errorEnum) {
        return assemble(errorEnum.getCode(), errorEnum.getCacheMsg());
    }

    /**
     * 响应结果给前端
     * @param code
     * @param msg
     * @return
     */
    protected RespBody assemble(String code, String msg) {
        return new RespBody(code, msg);
    }
}
