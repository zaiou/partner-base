package com.zaiou.common.support;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @Description: 内部服务调用
 * @auther: LB 2018/11/29 14:54
 * @modify: LB 2018/11/29 14:54
 */
@Component
@Slf4j
public class CustomRestTemplate {

    @Value("${web.http.request.url:http://127.0.0.1:8888}")
    private String webBaseUrl;

    @Autowired
    private RestTemplate restTemplate;
    @Resource(name = "customHttpHeaders")
    private HttpHeaders customHttpHeaders;
    @Autowired
    private Gson gson;

    public <T> T post(String uri, Object requestBody, Class<T> responseType) {
        return process("web", assemble(webBaseUrl, uri), requestBody,responseType);
    }

    private <T> T process(String type, String reqUrl, Object requestBody,Class<T> responseType) {
        log.info("===== Request {} =====>{}\n{}", type, reqUrl);
        HttpEntity<Object> formEntity = new HttpEntity<Object>(requestBody, customHttpHeaders);
        ResponseEntity<String> entity = restTemplate.postForEntity(reqUrl, formEntity, String.class);
        String result = entity.getBody();
        log.info("===== Response {} [{}:{}] =====>{}\n{}", type, reqUrl, result);
        // JSON对象转换
        JsonObject jsonResult = gson.fromJson(result, JsonObject.class);
        // JSON 提取第一层的响应码
        String respCode = jsonResult.get("respCode").getAsString();
        // JSON 提取第一层的响应信息(可能为空)
        JsonElement rMsg = jsonResult.get("respMsg");
        // JSON 提取第一层的数据内容(可能为空)
        JsonElement data = jsonResult.get("data");

        return (T) jsonResult;
    }

    /**
     * 组装请求url
     * @param baseUrl
     * @param trancode
     * @return
     */
    private String assemble(String baseUrl, String trancode) {
        return String.format("%s/%s", baseUrl, trancode);
    }

}
