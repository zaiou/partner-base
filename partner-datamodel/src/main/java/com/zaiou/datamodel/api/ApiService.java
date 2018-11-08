package com.zaiou.datamodel.api;

import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import com.zaiou.common.utils.SpringContextHolder;
import com.zaiou.common.vo.CustomResponse;
import com.zaiou.common.vo.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 模型统一入口
 * @auther: LB 2018/10/29 16:35
 * @modify: LB 2018/10/29 16:35
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class ApiService {
    /**
     * 统一入口
     * @param httpServletRequest
     * @param jym 交易码
     * @param reqdata 请求参数
     * @return
     */
    @RequestMapping(value = "/{jym}", method = { RequestMethod.POST })
    public @ResponseBody
    CustomResponse service(HttpServletRequest httpServletRequest, @PathVariable("jym") String jym,
                           @RequestBody String reqdata) {
        try {
            Request request = SpringContextHolder.getReflectionObject(jym, reqdata);
            return SpringContextHolder.invokeMethod(jym, request);
        } catch (BussinessException e) {
            log.error(e.getMessage(), e);
            return new CustomResponse(e.getRespCode(), e.getRespMsg());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return new CustomResponse(ResultInfo.SYS_ERROR);
        }
    }
}
