package com.zaiou.common.service;

import com.zaiou.common.db.DaoProxyService;
import com.zaiou.common.mybatis.po.SysParams;
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
            //更新错误码到redis
            if (resultInfo != null) {
                jedisService.setBySerialize(key, resultInfo.getMessage(),null);
                return resultInfo.getMessage();
            }
        }
        return sysParaValue;
    }

    /**
     *  获取系统参数表信息
     * @param code
     * @return
     */
    public String getSysParams(String code) {
        //从redis系统参数
        String key = RedisKey.getSyspara(code);
        String sysParaValue = jedisService.getTByUnSerialize(String.class, key);
        //么有的话从数据库获取
        if (StringUtils.isEmpty(sysParaValue)) {
            SysParams sp = new SysParams();
            sp.setCode(code);
            SysParams sysPara = (SysParams) daoProxyService.findOne(sp);
            //更新系统参数到redis
            if (sysPara != null) {
                sysParaValue = sysPara.getValue();
                updateSysParams(sysPara);
            }
        }
        if (StringUtils.isEmpty(sysParaValue)) {
            return null;
        }
        return sysParaValue;
    }

    /**
     * 更新系统参数到缓存
     * @param sysParam
     */
    public void updateSysParams(SysParams sysParam) {
        if (StringUtils.isEmpty(sysParam)) {
            return;
        }
        jedisService.set(RedisKey.getSyspara(sysParam.getCode()), sysParam.getValue(),null);
    }
}
