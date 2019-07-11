package com.zaiou.web.controller.system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @auther: LB 2019/1/17 16:38
 * @modify: LB 2019/1/17 16:38
 */
@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {
    private  int index=0;

    @RequestMapping(value = "/scopeTest")
    public void scopeTest(){
        log.info(""+index++);
    }
}
