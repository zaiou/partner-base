package com.zaiou.datamodel.process;

import com.alibaba.fastjson.JSON;
import com.zaiou.common.redis.constant.RedisKey;
import com.zaiou.common.redis.service.JedisService;
import com.zaiou.common.service.AbstractDataModelService;
import com.zaiou.common.utils.SpringContextHolder;
import com.zaiou.common.vo.Request;
import com.zaiou.common.vo.Response;
import com.zaiou.common.vo.datamodel.ProcessMessageReq;
import com.zaiou.common.vo.datamodel.TestReq;
import com.zaiou.common.vo.datamodel.TestResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 测试接口
 * @auther: LB 2018/10/29 17:50
 * @modify: LB 2018/10/29 17:50
 */
@Service
@Slf4j
public class TestProcess extends AbstractDataModelService {

    @Autowired
    private JedisService jedisService;

    @Override
    protected Response process(SqlSession sqlSession, Request req) {
        log.info("数据模型测试=======================");

        TestReq testReq = (TestReq) req;
        TestResp testResp = SpringContextHolder.getBean(TestResp.class);

        try {
            ProcessMessageReq messageDto = new ProcessMessageReq();
            messageDto.setTestId("lb");
            //redis添加消息
            jedisService.lpush(RedisKey.getMMmodelNormal(), JSON.toJSONString(messageDto));
            // 发布消息
            jedisService.publish(RedisKey.getMsgModelNormal(), JSON.toJSONString(messageDto));

            testResp.setName("liubin");

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        return testResp;
    }
}
