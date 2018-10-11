package com.zaiou.web.service.system.impl;

import com.zaiou.common.constant.SysParamConstant;
import com.zaiou.common.db.DaoProxyService;
import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import com.zaiou.common.mybatis.po.SysUser;
import com.zaiou.common.redis.constant.RedisKey;
import com.zaiou.common.redis.service.JedisService;
import com.zaiou.common.service.CacheService;
import com.zaiou.common.utils.ConvertObjectUtils;
import com.zaiou.common.utils.MD5Utils;
import com.zaiou.common.utils.StringUtils;
import com.zaiou.common.utils.TokenUtils;
import com.zaiou.web.common.bean.CurrentUser;
import com.zaiou.web.mybatis.mapper.SysUserMapper;
import com.zaiou.web.service.system.UserService;
import com.zaiou.web.vo.system.SysUserReq;
import com.zaiou.web.vo.system.UserLoginReq;
import com.zaiou.web.vo.system.UserLoginResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Description: 系统用户
 * @auther: LB 2018/9/20 17:30
 * @modify: LB 2018/9/20 17:30
 */
@Service
public class UserServiceImpl implements UserService {

    @Value("${login.timeout:30}")
    private Integer loginTimeout;

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private JedisService jedisService;
    @Autowired
    private DaoProxyService daoProxyService;

    /**
     * 添加用户
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void addUser(SysUserReq sysUserReq, CurrentUser currentUser){
        // vo转mapper实体
        SysUser sysUser = ConvertObjectUtils.NormalConvertObject(sysUserReq, SysUser.class);
        // 盐
        String salt = MD5Utils.generateSalt();
        sysUser.setSalt(salt);
        // 密码
        String userPassword = MD5Utils.hmacMD5(sysUser.getUserPassword(), salt);
        sysUser.setUserPassword(userPassword);
        // 用户状态  新增用户，需要强制修改密码
        sysUser.setStatus(2);
        // 创建人
//        sysUser.setCreateUser(currentUser.getUserCode());
        // 创建时间
        sysUser.setCreateTime(new Date());
        // 添加用户
        sysUserMapper.addUser(sysUser);
    }

    /**
     * 用户登录
     * @param userLoginReq
     * @return
     */
    @Override
    public UserLoginResp login(UserLoginReq userLoginReq){
        // 根据usercode查找用户信息
        SysUser sysUser = sysUserMapper.getSysUserByUserCode(userLoginReq.getUserCode());
        //用户账号不存在
        if (StringUtils.isEmpty(sysUser)){
            throw new BussinessException(ResultInfo.WEB_1002);
        }
        //用户账号异常
        if (sysUser.getStatus().equals(2)){
            throw new BussinessException(ResultInfo.WEB_2000);
        }
        //用户账号被注销
        if (sysUser.getStatus().equals(3)){
            throw new BussinessException(ResultInfo.WEB_2001);
        }
        // 解密
        String decryptPwd = MD5Utils.hmacMD5(userLoginReq.getPassword(), sysUser.getSalt());
        if (decryptPwd.equals(sysUser.getUserPassword())){
            // 将当前用户信息缓存到redis
            CurrentUser currentUser = getSession(sysUser);
            //设置返回数据
            UserLoginResp userLoginResp = ConvertObjectUtils.NormalConvertObject(currentUser, UserLoginResp.class);
            //更新用户数据
            sysUser.setErrorTimes(0);
            sysUser.setLastLoginTime(new Date());
            daoProxyService.update(sysUser);
            return userLoginResp;
        }else {
            int errorTimes = null == sysUser.getErrorTimes() ? 1 : sysUser.getErrorTimes() + 1;
            sysUser.setErrorTimes(errorTimes);
            if (errorTimes == 6) {
                sysUser.setStatus(5);// 账号锁定
            }
            sysUser.setLastLoginTime(new Date());
            daoProxyService.update(sysUser);
            throw new BussinessException(ResultInfo.WEB_1001);
        }
    }

    /**
     * 将当前用户信息缓存到redis
     * @param sysUser
     * @return
     */
    private CurrentUser getSession(SysUser sysUser){
        // 获取token
        String token = TokenUtils.getOnlyPK();
        // 复制数据到 currentuser
        CurrentUser currentUser = ConvertObjectUtils.NormalConvertObject(sysUser, CurrentUser.class);
        // 设置currentUser
        currentUser.setToken(token);
        currentUser.setUserId(sysUser.getId());

        // 系统登录自动离线时间配置（单位：分钟）
        String loginLeave = cacheService.getSysParams(RedisKey.getSyspara(SysParamConstant.SYS_LOGIN_TIMEOUT_CONFIG));
        Integer tokenTimeOut = loginTimeout * 60;
        if (StringUtils.isNotEmpty(loginLeave)){
            tokenTimeOut = Integer.valueOf(loginTimeout) * 60;
        }
        //添加token刷新时间,用于session判断
        String tokenKey = RedisKey.getTokenUserinfo(token);
        jedisService.setBySerialize(tokenKey, currentUser, tokenTimeOut);
        //添加usercode缓存，用户判断是否在其他地方登陆
        String userCodeKey = RedisKey.getLoginToken(currentUser.getUserCode());
        jedisService.set(userCodeKey, token, tokenTimeOut);
        return currentUser;
    }
}
