package com.zaiou.web.aspect;

import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

/**
 * @Description: web端控制器请求报文验证切面
 * @auther: LB 2018/9/28 17:45
 * @modify: LB 2018/9/28 17:45
 */
@Aspect
@Component
@Slf4j
public class ControllerValidatorAspect {

    /**
     * 配置切入点
     * com.zaiou.web.controller 子包所有函数，且函数的最后一个参数为BindingResult
     */
    @Pointcut("execution(public * com.zaiou.web.controller..*.*(..)) && args(..,bindingResult) ")
    public void doit(BindingResult bindingResult) {
        log.info("参数校验切入点----------------========================");
    }

    /**
     * 参数验证
     * @param pjp
     * @param bindingResult
     * @return
     * @throws Throwable
     */
    @Around("doit(bindingResult)")
    public Object doAround(ProceedingJoinPoint pjp, BindingResult bindingResult) throws Throwable {
        if (bindingResult.hasErrors()) {
            String[] errorMsgs = bindingResult.getFieldError().getDefaultMessage().split(",");
            ResultInfo resultInfo = ResultInfo.getResultCode("01", errorMsgs[0]);
            assert resultInfo != null;
            throw new BussinessException(resultInfo, ArrayUtils.remove(errorMsgs, 0));
        }
        return pjp.proceed();
    }
}
