package com.zaiou.common.db;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Description: Dao代理服务类
 *  不再需要自己定义单独的Mapper来实例化DaoProxy了，作为Service单独使用
 * @auther: LB 2018/9/19 20:11
 * @modify: LB 2018/9/19 20:11
 */
@Service
@ConditionalOnProperty(name = "spring.datasource.enable", havingValue = "true", matchIfMissing = true)
public class DaoProxyService extends DaoProxy {
    public DaoProxyService(){
        super(null);
    }
}
