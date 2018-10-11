package com.zaiou.web.config;

import com.zaiou.web.interceptor.AuthInterceptor;
import com.zaiou.web.interceptor.CurrentUserSessionResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @Description: 启用拦截器解析器等
 * @auther: LB 2018/10/9 14:58
 * @modify: LB 2018/10/9 14:58
 */
@Configuration
public class CustomWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
    /**
     * 添加静态资源文件
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAuthInterceptor()).excludePathPatterns("/").addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    /**
     * 这样写AuthInterceptor中@Autowired注入的类才不会为null
     * @return
     */
    @Bean
    public HandlerInterceptor getAuthInterceptor() {
        return new AuthInterceptor();
    }

    /**
     * 添加解析器
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(getCurrentSessionResolver());
        super.addArgumentResolvers(argumentResolvers);
    }

    @Bean
    public HandlerMethodArgumentResolver getCurrentSessionResolver() {
        return new CurrentUserSessionResolver();
    }


}
