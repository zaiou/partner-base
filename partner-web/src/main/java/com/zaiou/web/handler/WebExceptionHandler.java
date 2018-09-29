package com.zaiou.web.handler;

import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import com.zaiou.web.common.bean.RespBody;
import com.zaiou.web.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: web 统一异常处理
 * @auther: LB 2018/9/28 11:56
 * @modify: LB 2018/9/28 11:56
 */
@Slf4j
@ControllerAdvice
public class WebExceptionHandler {

    /**
     * 文件大小限制
     */
    @Value("${upload.file.size}")
    private String fileSize;

    /**
     *  web 统一异常处理
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {Exception.class, Throwable.class})
    public @ResponseBody
    RespBody handleException(HttpServletRequest request, Throwable ex) {
        log.error(ex.getMessage(), ex);
        // 自定义异常类型
        if (ex instanceof BussinessException) {
            BussinessException be = (BussinessException) ex;
            return R.info(be.getRespCode(), be.getRespMsg());
        }
        // 上传文件大小异常
        if (ex instanceof MultipartException) {
            return R.info(ResultInfo.WEB_COMMON_FILASIZE_LIMIT_0003.getCode(),
                    ResultInfo.WEB_COMMON_FILASIZE_LIMIT_0003.getCacheMsg(fileSize));
        }
        // 通用错误类型
        return R.info(ResultInfo.WEB_SYS_ERROR);
    }

}
