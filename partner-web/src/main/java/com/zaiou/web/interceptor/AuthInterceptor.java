package com.zaiou.web.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.zaiou.common.constant.SysParamConstant;
import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import com.zaiou.common.redis.constant.RedisKey;
import com.zaiou.common.redis.service.JedisService;
import com.zaiou.common.service.CacheService;
import com.zaiou.common.utils.ResponseUtils;
import com.zaiou.common.utils.StringUtils;
import com.zaiou.web.common.bean.CurrentUser;
import com.zaiou.web.constant.ResultConstant;
import com.zaiou.web.constant.SessionConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 用户会话拦截器，检查用户是否已登陆
 * @auther: LB 2018/10/8 17:55
 * @modify: LB 2018/10/8 17:55
 */
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Value("${login.timeout:30}")
    private Integer loginTimeout;

    @Autowired
    private AuthIgnoreResources authIgnoreResources;
    @Autowired
    private JedisService jedisService;
    @Autowired
    private CacheService cacheService;

    /**
     *用户请求拦截
     * @param request
     * @param response
     * @param arg
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg) throws Exception {
        CurrentUser userSession=null;
        log.info("===============会话拦截器开始=========================");

        try {
            //获取当前访问的接口
            String contextPath = request.getContextPath();
            String requestUri = request.getRequestURI();
            String url = requestUri.substring(contextPath.length());
            log.info("=====request-url:" + url);
            if (url.lastIndexOf("?") > 0) {
                url = url.substring(0, url.lastIndexOf("?"));
            }

            //忽略拦截会话url
            String mehtod = request.getMethod();
            log.info("======method:"+mehtod);
            if (CollectionUtils.contains(authIgnoreResources.getIgnoreSessions().iterator(), url)
                    || "OPTIONS".equals(mehtod)) {
                return true;
            }

            // 获取前端传过来的当前会话token
            String token = request.getHeader(SessionConstant.TOKEN_ID);
            if (StringUtils.isEmpty(token)) {
                token = request.getParameter(SessionConstant.TOKEN_ID);
            }

            if (StringUtils.isNotEmpty(token)) {
                //根据token获取当前用户信息
                String tokenKey = RedisKey.getTokenUserinfo(token);
                userSession = (CurrentUser)jedisService.getByUnSerialize(tokenKey);
                if (StringUtils.isNotEmpty(userSession)) {//登陆未超时，当前会话检查
                    // 首次登陆，请修改密码
                    if(userSession.getStatus() == 2 && !url.equals("/user/updatePwd") && !url.equals("/user/logout")){
                        throw new BussinessException(ResultInfo.WEB_1005);
                    }
                    //根据usercode从redis中获取当前用户的token
                    String userCodeKey = RedisKey.getLoginToken(userSession.getUserCode());
                    String userToken = jedisService.get(userCodeKey);
                    // redis获取不到token,用户状态改变，强制下线
                    if (StringUtils.isEmpty(userToken)){
                        throw new BussinessException(ResultInfo.WEB_1006);
                    }
                    //前端的token和redis的token，用户在别处登陆，强制下线
                    if(!token.equals(userToken)){
                        throw new BussinessException(ResultInfo.WEB_1007,userSession.getUserCode());
                    }
                    // 系统登录自动离线时间配置（单位：分钟）,获取过期时间
                    String loginLeave = cacheService.getSysParams(RedisKey.getSyspara(SysParamConstant.SYS_LOGIN_TIMEOUT_CONFIG));
                    Integer tokenTimeOut = loginTimeout * 60;
                    if (StringUtils.isNotEmpty(loginLeave)){
                        tokenTimeOut = Integer.valueOf(loginTimeout) * 60;
                    }
                    //更新过期时间
                    jedisService.expire(tokenKey, tokenTimeOut);
                    jedisService.expire(userCodeKey, tokenTimeOut);
                    //如果token验证成功，将token对应的用户id存在request中，便于之后CurrentUser对象注入
                    request.setAttribute(SessionConstant.CURRENT_SESSION_ID, tokenKey);
                }else {//登陆超时
                    if (url.equals("/user/logout")) {
                        return true;
                    }
                    throw new BussinessException(ResultInfo.WEB_1008);
                }
            }else {
                // 没有传token，非法请求
                throw new BussinessException(ResultInfo.WEB_1009);
            }
        } catch (BussinessException e) {
            log.error(e.getMessage(),e);
            return errorCheck(response, e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return errorCheck(response, ResultInfo.WEB_SYS_ERROR);
        } finally {
            log.info("===============会话拦截器结束=========================");
        }

        return true;
    }

    /**
     * 捕获拦截器手动抛出的异常，返回信息给前端
     * @param response
     * @param e
     * @return
     */
    private boolean errorCheck(HttpServletResponse response, BussinessException e) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ResultConstant.RESULT_CODE, e.getRespCode());
        map.put(ResultConstant.RESULT_MSG, e.getRespMsg());
        ResponseUtils.renderJson(response, JSONObject.toJSON(map).toString());
        return false;
    }

    /**
     * 捕获拦截器抛出的其他异常，返回信息给前端
     * @param response
     * @param result
     * @return
     */
    private boolean errorCheck(HttpServletResponse response, ResultInfo result) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ResultConstant.RESULT_CODE, result.getCode());
        map.put(ResultConstant.RESULT_MSG, result.getMsg());
        ResponseUtils.renderJson(response, JSONObject.toJSON(map).toString());
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
