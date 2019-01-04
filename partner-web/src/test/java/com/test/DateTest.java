package com.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * @Description: 日期时间测试
 * @auther: LB 2018/11/30 11:55
 * @modify: LB 2018/11/30 11:55
 */
@Slf4j
public class DateTest {

    @Test
    public void dateTest(){}{
        Date d = new Date();
        log.info(d.getTime()+"");
    }

}
