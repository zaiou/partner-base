package com.zaiou.web.interceptor;

import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import com.zaiou.common.redis.service.JedisService;
import com.zaiou.common.utils.StringUtils;
import com.zaiou.web.constant.SessionConstant;
import com.zaiou.web.annotation.CurrentUserSeession;
import com.zaiou.web.common.bean.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @Description: 给controller层中添加@CurrentUserSession CurrentUser 注解的对象封装数据
 * @auther: LB 2018/9/18 17:42
 * @modify: LB 2018/9/18 17:42
 */
@Component
@Slf4j
public class CurrentUserSessionResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private JedisService jedisService;

    /**
     * 判断controller的参数是否满足条件，满足条件则执行resolveArgument方法
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("参数是否满足用户条件------->"+parameter);
        if (parameter.getParameterType().isAssignableFrom(CurrentUser.class)
            && parameter.hasParameterAnnotation(CurrentUserSeession.class)){
            return true;
        }
        return false;
    }

    /**
     * supportsParameter返回true执行，将返回值返回给controller中的参数
     * @param parameter
     * @param modelAndViewContainer
     * @param webRequest
     * @param binderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 取出鉴权时存入的登录用户Id
        String sessionKey = (String) webRequest.getAttribute(SessionConstant.CURRENT_SESSION_ID,
                RequestAttributes.SCOPE_REQUEST);
        if (StringUtils.isNotEmpty(sessionKey)){
            // 从缓存中取出当前用户并返回
            CurrentUser currentUser = (CurrentUser) jedisService.getByUnSerialize(sessionKey);
            if (StringUtils.isNotEmpty(currentUser)){
                return currentUser;
            }
        }
        throw new BussinessException(ResultInfo.WEB_1000);
    }
}
