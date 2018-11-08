package com.zaiou.common.service;

import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import com.zaiou.common.mybatis.config.CustomSessionFactory;
import com.zaiou.common.vo.CustomResponse;
import com.zaiou.common.vo.Request;
import com.zaiou.common.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * @Description: 相关模块核心service--- 数据模型具体实现
 * @auther: LB 2018/10/30 09:50
 * @modify: LB 2018/10/30 09:50
 */
@Slf4j
@Service
@Primary
public abstract class AbstractDataModelService implements CoreService{
    @Autowired
    protected CustomSessionFactory sessionFactory;

    /**
     * 根据交易码执行相关方法
     * @param request
     * @return
     */
    public CustomResponse execute(Request request) {
        SqlSession sqlSession = sessionFactory.getManualSession();
        try {
            CustomResponse customResponse = new CustomResponse(process(sqlSession, request));
            return customResponse;
        } catch (BussinessException e) {
            log.error(e.getRespMsg(), e);
            sessionFactory.rollbackTransaction(sqlSession);
            return new CustomResponse(e.getRespCode(), e.getRespMsg());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            sessionFactory.rollbackTransaction(sqlSession);
            return new CustomResponse(ResultInfo.SYS_ERROR);
        } finally {
            sessionFactory.commit(sqlSession);
            sessionFactory.releaseConnection(sqlSession);
        }
    }

    protected abstract Response process(SqlSession sqlSession, Request req) throws Exception;
}
