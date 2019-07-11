package com.zaiou.cleaning.redis;

/**
 * @author zaiou 2019-06-05
 * @Description: jedis设置
 * @modify zaiou 2019-06-05
 */
public class JedisSet {
    public static void main(String[] args) {
        System.out.println("c参数数量："+args.length);
        if (args.length!=3){
            System.err.println("use:<key> <value> <seconds>");
            System.exit(0);
        }
        String key=args[0];
        String value=args[1];
        int seconds=Integer.parseInt(args[2]);

        System.out.println("redis塞值");
    }
}
