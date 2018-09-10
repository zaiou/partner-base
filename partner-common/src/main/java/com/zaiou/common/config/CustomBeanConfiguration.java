package com.zaiou.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description: 自定义bean对象
 * @auther: LB 2018/9/8 10:02
 * @modify: LB 2018/9/8 10:02
 */
@Configuration
@EnableAsync
@Slf4j
public class CustomBeanConfiguration {

    @Value("${rest.template.connect.timeout:20000}")
    private int connectionTime;

    @Value("${rest.template.read.timeout:80000}")
    private int readTimeout;

    /**
     * RestTemplate 请求
     * @return
     */
    @Bean
    @Primary
    public RestTemplate restTemplate(){
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // 标志是否使用缓存流的形式，默认是true，缺点是当发送大量数据时，
        // 比如put/post的保存和修改，那么可能内存消耗严重。设置为false
        requestFactory.setBufferRequestBody(false);
        requestFactory.setConnectTimeout(connectionTime);// 设置连接超时时间 milliseconds 10s
        requestFactory.setReadTimeout(readTimeout);// 读取内容超时时间 milliseconds 60s
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : messageConverters) {
            if (httpMessageConverter.getClass() == MappingJackson2HttpMessageConverter.class) {
                MappingJackson2HttpMessageConverter c = (MappingJackson2HttpMessageConverter) httpMessageConverter;
                c.getObjectMapper().enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            }
        }
        log.info("定义-------------->RestTemplate");
        return restTemplate;
    }

    /**
     * http请求头和响应头
     * @return
     */
    @Bean("customHttpHeaders")
    @Primary
    public HttpHeaders customHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json;charset=utf-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return headers;
    }

    /**
     *  配置 Gson
     *  Gson序列化的时候null也被输出
     * @return
     */
    @Bean
    @Primary
    public Gson gson() {
        return new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }


    /**
     * 自定义 Executor 配置
     * 用于执行异步任务
     * @return
     */
    @Bean
    public Executor getExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(30);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("自定义 Executor 配置--------------->Executor ");
        return executor;
    }
}
