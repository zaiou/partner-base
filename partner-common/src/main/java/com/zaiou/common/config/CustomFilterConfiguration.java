package com.zaiou.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zaiou.common.filter.ThreadNameFilter;
import com.zaiou.common.filter.XssFilter;
import com.zaiou.common.filter.wrapper.XssStringJsonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

/**
 * @Description: 设置过滤器的优先级以及json转化
 * @auther: LB 2018/9/8 17:54
 * @modify: LB 2018/9/8 17:54
 */
@Configuration
public class CustomFilterConfiguration {

    @Value("${json.remove.null.value.enable:false}")
    private boolean enableRemoveNullValue;

    @Bean
    public FilterRegistrationBean threadNameFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new ThreadNameFilter());
        registration.addUrlPatterns("/*");
        // registration.addInitParameter("paramName", "paramValue");
        registration.setName("threadNameFilter");
        registration.setOrder(10);
        return registration;
    }

    @Bean
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        // registration.addInitParameter("paramName", "paramValue");
        registration.setName("xssFilter");
        registration.setOrder(11);
        return registration;
    }


    /**
     * 创建XssObjectMapper的bean,替换spring boot原有的实例,用于整个系统的json转换.
     */
    @Bean
    @Primary
    public ObjectMapper xssObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 解析器
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // 注册xss解析器
        SimpleModule xssModule = new SimpleModule("XssStringJsonSerializer");
        xssModule.addSerializer(new XssStringJsonSerializer());
        objectMapper.registerModule(xssModule);
        if (enableRemoveNullValue) {
            objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
                @Override
                public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                        throws IOException, JsonProcessingException {
                    jsonGenerator.writeString("");
                }
            });
        }

        // 返回
        return objectMapper;
    }
}
