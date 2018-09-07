package com.test;

import com.zaiou.web.WebApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.MessageFormat;
import java.util.Date;

/**
 * @Description: MessageFormat
 * @auther: LB 2018/9/3 21:42
 * @modify: LB 2018/9/3 21:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = WebApplication.class)
@Slf4j
public class MessageFormatTest {
    @Test
  public void formatTest(){
        String pattern = "ErrorMessage=This is Error Message : {0}.";
        String returnStr = MessageFormat.format(pattern, "hello world");
        System.out.println(returnStr);

        String pattern1="this message :数字；{0,number,#.##}, 日期；{1,date}";
        Date date = new Date();
        Object[] objects={10.123,date,"晴朗"};
        String returnStr1= MessageFormat.format(pattern1 , objects );
        log.info(returnStr1);

  }
}
