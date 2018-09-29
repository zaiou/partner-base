package com.zaiou.web.common.utils;

import com.github.pagehelper.PageInfo;
import com.zaiou.common.enums.ResultInfo;
import com.zaiou.web.common.bean.RespBody;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 返回数据
 * @auther: LB 2018/9/28 15:02
 * @modify: LB 2018/9/28 15:02
 */
@Slf4j
public class R {
    /**
     *  响应业务处理成功
     * @param data
     * @return
     */
    public static RespBody success(Object data) {
        return new RespBody(ResultInfo.SUCCESS, data);
    }

    /**
     * 响应处理结果给前端 code/msg
     * @param resultInfo
     * @return
     */
    public static RespBody info(ResultInfo resultInfo) {
        return info(resultInfo.getCode(), resultInfo.getCacheMsg());
    }

    /**
     *  响应处理结果给前端 code/msg
     * @param code
     * @param msg
     * @return
     */
    public static RespBody info(String code, String msg) {
        return new RespBody(code, msg);
    }

    /**
     * 响应带分页的结果给前端
     * @param resultInfo
     * @param pageInfo
     * @return
     */
    public static RespBody pageInfo(ResultInfo resultInfo, PageInfo<?> pageInfo) {
        RespBody respBody = info(resultInfo);
        respBody.setPage(pageInfo);
        respBody.setData(pageInfo.getList());
        return respBody;
    }

}
