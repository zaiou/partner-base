package com.zaiou.web.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zaiou.common.enums.LogOperateEnum;
import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import com.zaiou.common.mybatis.po.SysOperateLog;
import com.zaiou.common.redis.constant.RedisKey;
import com.zaiou.common.redis.service.JedisService;
import com.zaiou.common.utils.StringUtils;
import com.zaiou.web.annotation.service.LogService;
import com.zaiou.web.common.bean.CurrentUser;
import com.zaiou.web.common.bean.RespBody;
import com.zaiou.web.constant.SessionConstant;
import com.zaiou.web.service.system.OperateLogService;
import com.zaiou.web.vo.system.UserLoginResp;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Description: web端日志切面
 * @auther: LB 2018/9/25 15:24
 * @modify: LB 2018/9/25 15:24
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private JedisService jedisService;
    @Autowired
    private OperateLogService operateLogService;

    /**
     * 配置切入点
     * com.zaiou.web.controller 包及其子包所有函数
     */
    @Pointcut("execution(public * com.zaiou.web.controller..*.*(..))")
    public void doit() {
        log.info("配置日志切入点----------------========================");
    }

    /**
     * 对相应的url进行拦截，组装操作成功日志保存
     * @param pjp 获得通知的签名信息，如目标方法名、目标方法参数信息等
     * @param returnValue
     */
    @AfterReturning(pointcut = "doit()", returning = "returnValue")
    public void afterReturning(JoinPoint pjp, Object returnValue) {
        try {
            // 获取RequestAttributes
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            // 从获取RequestAttributes中获取HttpServletRequest的信息
            HttpServletRequest request = (HttpServletRequest) requestAttributes
                    .resolveReference(RequestAttributes.REFERENCE_REQUEST);
            // 获取目标方法
            String method = request.getMethod();
            // 获取目标 uri
            String uri = request.getRequestURI();
            log.info("目标方法为----------------================"+uri);
            Object vo = null;
            // 获取post函数中的参数，过滤其中的vo类
            if (method.equalsIgnoreCase("POST")) {
                Object[] obj = pjp.getArgs();
                for (Object object : obj) {
                    if (StringUtils.isNotEmpty(obj) && object.getClass().getName().contains("com.zaiou.web.vo")) {
                        vo = object;
                        break;
                    }
                }
            }

            LogOperateEnum operateEnum = LogOperateEnum.getEnum(uri);

            // 判断是否是需要记录的操作
            if (StringUtils.isNotEmpty(operateEnum)) {
                // 解析请求参数有Log注解的字段
                JSONObject jsonRequest = LogService.analysis(vo);
                RespBody respBody = (RespBody) returnValue;

                //如果是删除状况，获取控制层压入的data数据
                if (StringUtils.isNotEmpty(operateEnum) && operateEnum.getIsDel()) {
                    jsonRequest = LogService.analysis(respBody.getData());
                    respBody.setData(null);
                    returnValue = respBody;
                }

                // 只记录成功的操作
                String code = respBody.getCode();
                if (ResultInfo.SUCCESS.getCode().equals(code)) {
                    // 获取token
                    String token;
                    if (LogOperateEnum.user_login.equals(operateEnum)) {
                        // 如果是登录操作，则取响应的token
                        UserLoginResp data = (UserLoginResp) respBody.getData();
                        token = data.getToken();
                    } else if (LogOperateEnum.user_logout.equals(operateEnum) && jsonRequest == null) {
                        // 如果是退出操作，并且token已失效的，不记录日志
                        return;
                    } else {
                        // 其他操作获取请求带过来的token
                        token = request.getHeader(SessionConstant.TOKEN_ID);
                        if (StringUtils.isEmpty(token)) {
                            token = request.getParameter(SessionConstant.TOKEN_ID);
                        }
                        if (StringUtils.isEmpty(token)) {
                            Object attribute = request.getAttribute(SessionConstant.TOKEN_ID);
                            token = attribute != null ? (String) attribute : null;
                        }
                    }

                    // 获取用户信息
                    String key = RedisKey.getTokenUserinfo(token);
                    CurrentUser currentUser = (CurrentUser) jedisService.getByUnSerialize(key);
                    JSONObject jsonResponse = (JSONObject) JSONObject.toJSON(returnValue);
                    addLog(request, currentUser, operateEnum, jsonRequest, jsonResponse);
                }
            }
        } catch (Throwable e) {
            log.error("成功日志拦截出错", e);
        }
    }

    /**
     *  对相应的url进行拦截，组装操作异常日志保存
     * @param pjp
     * @param e
     */
    @AfterThrowing(pointcut = "doit()", throwing = "e")
    public void afterThrowing(JoinPoint pjp, Throwable e) {
        try {
            // 获取RequestAttributes
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            // 从获取RequestAttributes中获取HttpServletRequest的信息
            HttpServletRequest request = (HttpServletRequest) requestAttributes
                    .resolveReference(RequestAttributes.REFERENCE_REQUEST);
            String method = request.getMethod();
            String uri = request.getRequestURI();
            Object vo = null;
            //获取拦截的vo类
            if ("POST".equalsIgnoreCase(method)) {
                Object[] obj = pjp.getArgs();
                for (Object object : obj) {
                    if (StringUtils.isNotEmpty(obj) && object.getClass().getName().contains("com.zaiou.web.vo")) {
                        vo = object;
                        break;
                    }
                }
            }
            //result的值就是被拦截方法的返回值
            RespBody result = new RespBody(ResultInfo.WEB_SYS_ERROR);
            if (e instanceof BussinessException) {
                BussinessException bs = (BussinessException) e;
                result.setCode(bs.getRespCode());
                result.setMessage(bs.getRespMsg());
            }

            //当前接口的请求uri与操作日志枚举定义的相同，则拦截，在日志表形成记录
            LogOperateEnum operateEnum = LogOperateEnum.getEnum(uri);

            if (StringUtils.isNotEmpty(operateEnum)) {
                JSONObject jsonRequest = LogService.analysis(vo);
                String code = String.valueOf(result.getCode());
                if ((LogOperateEnum.user_login.equals(operateEnum) && ResultInfo.WEB_1001.getCode().equals(code))) {
                    operateEnum = LogOperateEnum.user_login_err;
                    addLog(request, null, operateEnum, jsonRequest, null);
                }
            }
        } catch (Throwable e1) {
            log.error("异常日志拦截出错", e1);
        }

    }

    /**
     * 保存日志到数据库
     * @param request
     * @param currentUser
     * @param operateEnum
     * @param jsonRequest
     * @param jsonResponse
     */
    public void addLog(HttpServletRequest request, CurrentUser currentUser, LogOperateEnum operateEnum, Object jsonRequest, JSONObject jsonResponse) {
        try {
            log.info("========日志操作保存开始========");
            SysOperateLog operateLog = new SysOperateLog();
            operateLog.setType(operateEnum.getType());
            operateLog.setFunc(operateEnum.getFunc());
            operateLog.setIp(getIpAddr(request));
            operateLog.setOperateTime(new Date());
            //登录需要特殊处理，因为header没有传token，currentUser对象抓取为空
            if (StringUtils.isNotEmpty(currentUser)) {
                operateLog.setOperateUser(currentUser.getUserCode());
                operateLog.setOperateName(currentUser.getUserName());
            } else {
                operateLog.setOperateUser(StringUtils.toString(((JSONObject) jsonRequest).get("用户"), ""));
                operateLog.setOperateName(StringUtils.toString(((JSONObject) jsonRequest).get("用户姓名"), ""));
            }
            if (StringUtils.isNotEmpty(jsonRequest)) {
                String text = JSON.toJSONString(jsonRequest);
                if (text.length() >= 300) {
                    text = text.substring(0, 300) + "......";
                }
                operateLog.setText(text);
            } else {
                operateLog.setText(jsonResponse.get("message") + "");
            }
            operateLogService.addLog(operateLog);
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("========日志操作保存结束========");
        }
    }

    /**
     *  获取IP
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            if (ip.indexOf(",") > 0) {
                return ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }
}
