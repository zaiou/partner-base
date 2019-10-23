package com.zaiou.cleaning.redis;

import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;

/**
 * @author zaiou 2019-06-05
 * @Description: jedis设置
 * @modify zaiou 2019-06-05
 */
public class JedisSet {
    public static void main(String[] args) {
        if (args.length!=3){
            throw new BussinessException(ResultInfo.CLEANING_3000.getCode(), ResultInfo.CLEANING_3000.getMsg());
        }
        String key=args[0];
        String value=args[1];
        int seconds=Integer.parseInt(args[2]);

        System.out.println("redis塞值");
    }
}
