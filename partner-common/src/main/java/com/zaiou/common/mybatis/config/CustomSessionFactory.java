package com.zaiou.common.mybatis.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * @Description: sqlsession配置
 * @auther: LB 2018/9/25 11:20
 * @modify: LB 2018/9/25 11:20
 */
@Slf4j
@Component
public class CustomSessionFactory {

    @Autowired(required = false)
    @Qualifier("writeSqlSessionFactory")
    @Lazy
    private SqlSessionFactory writeSqlSessionFactory;

    @Autowired(required = false)
    @Qualifier("readSqlSessionFactory")
    @Lazy
    private SqlSessionFactory readSqlSessionFactory;

    public SqlSession getSession() {
        SqlSession sqlSession = writeSqlSessionFactory.openSession(true);
        try {
            sqlSession.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        log.info("SqlSession Create AutoCommit=[TRUE] [Write]===>{}||{}", sqlSession, sqlSession.getConnection());

        return sqlSession;
    }

    /**
     * 自动提交事务 write session
     */
    public SqlSession getManualSession() {
        SqlSession sqlSession = writeSqlSessionFactory.openSession(false);
        try {
            sqlSession.getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        log.info("SqlSession Create AutoCommit=[FALSE] [Write]===>{}||{}", sqlSession, sqlSession.getConnection());
        return sqlSession;
    }

    /**
     * @description 暂时关闭这个方法 预留的
     * @deprecated
     */
    public SqlSession getReadOnlySession() {
        SqlSession sqlSession = readSqlSessionFactory.openSession();
        try {
            sqlSession.getConnection().setReadOnly(true);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        log.info("SqlSession Create [Read Only]===>{}||{}", sqlSession, sqlSession.getConnection());
        return sqlSession;
    }

    /**
     * 失败回滚事务
     *
     * @return
     * @throws Exception
     */
    public void rollbackTransaction(SqlSession sqlSession) {
        try {
            log.info("SqlSession rollback===>{}||{}", sqlSession, sqlSession.getConnection());
            sqlSession.getConnection().rollback();
            sqlSession.rollback();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            log.info("SqlSession rollback exception===>{}||{}", sqlSession, sqlSession.getConnection());
        }
    }

    public void commit(SqlSession sqlSession) {
        if (sqlSession != null) {
            try {
                sqlSession.getConnection().commit();
                sqlSession.commit();
                log.info("SqlSession commited===>{}||{}", sqlSession, sqlSession.getConnection());
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                log.info("SqlSession commited exception-->{}||{}", sqlSession, sqlSession.getConnection());
            }
        }
    }

    public void releaseConnection(SqlSession sqlSession) {
        if (sqlSession != null) {
            log.info("SqlSession close ===>{}||{}", sqlSession, sqlSession.getConnection());
            sqlSession.close();
        }
    }
}
