package com.zaiou.common.aspect;

import com.google.gson.JsonIOException;
import com.zaiou.common.utils.DesensitizedUtils;
import com.zaiou.common.vo.Request;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 统一入口及出口日志输出
 * @auther: LB 2018/10/10 09:38
 * @modify: LB 2018/10/10 09:38
 */
@Aspect
@Component
@Slf4j
public class PrinterControllerAspect {

    @Value("${logging.cutout.print:false}")
    private boolean loggingCutoutPrint;

    // 定义切点Pointcut
    @Pointcut("execution(* com.zaiou..*(..)) "
            + "&& (@annotation(org.springframework.web.bind.annotation.GetMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.RequestMapping))")
    public void printerControllerAspect() {
    }

    @Around("printerControllerAspect()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String contentType = request.getContentType();
        String queryString = request.getQueryString();
        log.info("接收数据-AOP: {}, method: {}, url: {}, contentType:{}, params: {}", url, method, uri, contentType,
                queryString);
        try {
            if (HttpMethod.POST.toString().equalsIgnoreCase(method)) {
                Object[] args = pjp.getArgs();
                for (Object arg : args) {
                    if (arg instanceof Request) {
                        log.info("接收数据-AOP: {}", getPrinter(DesensitizedUtils.toJsonString(arg)));
                    }
                    if (arg instanceof String) {
                        log.info("接收数据-AOP: {}", getPrinter(arg.toString()));
                    }
                    if (arg instanceof List) {
                        log.info("接收数据-AOP: {}", getPrinter(DesensitizedUtils.toJsonString(arg)));
                    }
                }
            } else {
                log.info("接收数据-AOP: {}", queryString);
            }
        } catch (JsonIOException e) {
            log.info("接收数据-AOP: {}", queryString);
        }
        long startTime = System.currentTimeMillis();
        Object result = pjp.proceed();
        long cost = System.currentTimeMillis() - startTime;
        try {
            log.info("响应数据-AOP[{}]:{}", cost, getPrinter(DesensitizedUtils.toJsonString(result)));
        } catch (JsonIOException e) {
            log.info("响应数据-AOP[{}]:{}", cost, getPrinter(result.toString()));
        }
        return result;
    }

    private String getPrinter(String beanToJson) {
        return loggingCutoutPrint ? (beanToJson.length() > 2048 ? beanToJson.substring(0, 2048) + "******" : beanToJson)
                : beanToJson;
    }

}
