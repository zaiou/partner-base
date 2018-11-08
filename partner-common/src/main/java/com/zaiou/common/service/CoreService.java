package com.zaiou.common.service;

import com.zaiou.common.vo.CustomResponse;
import com.zaiou.common.vo.Request;

/**
 * @Description: 相关模块核心service
 * @auther: LB 2018/10/29 17:41
 * @modify: LB 2018/10/29 17:41
 */
public interface CoreService {
    CustomResponse execute(Request request);
}
