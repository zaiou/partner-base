package com.zaiou.common.db;

import com.zaiou.common.mybatis.mapper.BaseDaoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Description: Dao代理服务类
 *  不再需要自己定义单独的Mapper来实例化DaoProxy了，作为Service单独使用
 * @auther: LB 2018/9/19 20:11
 * @modify: LB 2018/9/19 20:11
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "spring.datasource.enable", havingValue = "true", matchIfMissing = true)
public class DaoProxyService extends DaoProxy implements InitializingBean {
    @Autowired
    private BaseDaoMapper baseMapper;

    /**
     * 项目启动时初始化DaoProxy中的dao实例
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.setDao(baseMapper);
    }
}
