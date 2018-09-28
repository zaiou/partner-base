package com.zaiou.common.service;

import com.zaiou.common.db.DaoProxyService;
import com.zaiou.common.mybatis.po.SysResultInfo;
import com.zaiou.common.redis.constant.RedisKey;
import com.zaiou.common.redis.service.JedisService;
import com.zaiou.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 缓存服务类
 * @auther: LB 2018/9/19 15:15
 * @modify: LB 2018/9/19 15:15
 */
@Service
public class CacheService {

    @Autowired
    private JedisService jedisService;
    @Autowired
    private DaoProxyService daoProxyService;

    /**
     * 根据系统类型、交易码获取错误码信息
     * @param system
     * @param code
     * @return
     */
    public String getResultMsg(String system, String code) {
        String key = RedisKey.getResultCode(system, code);
        String sysParaValue = jedisService.getTByUnSerialize(String.class, key);
        if (StringUtils.isEmpty(sysParaValue)) {
            SysResultInfo resultInfo = new SysResultInfo();
            resultInfo.setSystem(system);
            resultInfo.setResultCode(code);
            resultInfo = (SysResultInfo) daoProxyService.findOne(resultInfo);
            if (resultInfo != null) {
                jedisService.setBySerialize(key, resultInfo.getMessage(),null);
                return resultInfo.getMessage();
            }
        }
        return sysParaValue;
    }
}
