package com.test;

import org.junit.Test;

/**
 * @Description: Class类
 * @auther: LB 2018/9/19 09:37
 * @modify: LB 2018/9/19 09:37
 */
public class ClassTest {

    @Test
    public void classtest(){
        System.out.printf(ClassTest.class+"");
        System.out.printf(ClassTest.class.equals(new ClassTest().getClass())+"is");
    }
}
