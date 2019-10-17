package test;

import java.math.BigDecimal;

/**
 * @author zaiou 2019-09-16
 * @Description:
 * @modify zaiou 2019-09-16
 */
public class Test {
    @org.junit.Test
    public void test(){
        BigDecimal a=new BigDecimal(4.1200);
        BigDecimal b=new BigDecimal(400.00);
        System.out.println(a.
                add(b.divide(new BigDecimal(100),4, BigDecimal.ROUND_HALF_UP)).setScale(5,   BigDecimal.ROUND_HALF_UP));
    }
}
