package com.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: stream 循环
 * @auther: LB 2018/12/26 16:09
 * @modify: LB 2018/12/26 16:09
 */
public class Stream {

    @Test
    public void classtest(){
        List<Integer> aa= new ArrayList();
        aa.add(1);
        aa.add(2);
        aa.add(3);
        aa.stream().forEach(t -> {
            if (t == 2){
                return;
            }
            System.out.println(t);
        });
    }

}
