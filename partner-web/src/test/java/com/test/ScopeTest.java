package com.test;

import com.zaiou.common.support.CustomRestTemplate;
import com.zaiou.web.WebApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: 单例/多例测试
 * @auther: LB 2019/1/17 16:42
 * @modify: LB 2019/1/17 16:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = WebApplication.class)
@Slf4j
public class ScopeTest {
    @Autowired
    protected CustomRestTemplate restfulClient;

    @Test
    public void test(){
        restfulClient.post("web/test/scopeTest","",String.class);
    }

}
