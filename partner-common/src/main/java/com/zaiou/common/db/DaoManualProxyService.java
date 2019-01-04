package com.zaiou.common.db;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Description:Dao代理服务类-手动事务<br>
 *  不再需要自己定义单独的Mapper来实例化DaoProxy了，作为Service单独使用
 * @auther: LB 2018/11/29 15:07
 * @modify: LB 2018/11/29 15:07
 */
@Service
@ConditionalOnProperty(name = "spring.datasource.enable", havingValue = "true", matchIfMissing = true)
public class DaoManualProxyService extends DaoManualProxy {

}
