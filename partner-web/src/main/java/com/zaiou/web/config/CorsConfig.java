package com.zaiou.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 跨域配置
 * @auther: LB 2018/10/9 15:54
 * @modify: LB 2018/10/9 15:54
 */
@Configuration
public class CorsConfig {

    /**
     * 跨域配置
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", addCorsConfig());
        return new CorsFilter(source);
    }

    private CorsConfiguration addCorsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        List<String> list = new ArrayList<>();
        list.add("*");
        corsConfiguration.setAllowedOrigins(list);
        // 请求常用的三种配置，*代表允许所有，也可以自定义属性（比如header只能带什么，只能是post方式等等）
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setExposedHeaders(Arrays.asList("access-control-allow-headers", "access-control-allow-methods",
                "access-control-allow-origin", "access-control-max-age", "X-Frame-Options", "Content-Disposition"));
        return corsConfiguration;
    }
}
