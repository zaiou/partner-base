package com.zaiou.cleaning.db;

import com.zaiou.cleaning.config.TaskInitConfig;
import com.zaiou.cleaning.utils.PropertiesUtil;
import scala.Serializable;

import java.sql.*;

/**
 * @author zaiou 2019-05-27
 * @Description: mysql数据操作
 * @modify zaiou 2019-05-27
 */
public class MysqlService implements Serializable {

    public static Connection getConnection() {
        Connection dbConn = null;
        try {
            Class.forName(PropertiesUtil.propertiesMap.get("spark.mysql_driver"));
            dbConn = DriverManager.getConnection(
                    PropertiesUtil.propertiesMap.get("spark.mysql_url"),
                    PropertiesUtil.propertiesMap.get("spark.mysql_user"),
                    PropertiesUtil.propertiesMap.get("spark.mysql_pass"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbConn;
    }

    public Long insertSysScheduleLog(String appname){
        Connection connection=getConnection();
        PreparedStatement pstmt=null;
        try {
            pstmt=connection.prepareStatement("INSERT INTO SYS_SCHEDULE_LOG(TASK_KEY,TASK_NAME,EXE_DATE,RESULT,START_TIME) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1,appname);
            if (TaskInitConfig.parserInitConfig.containsKey(appname)){
                pstmt.setString(2,TaskInitConfig.parserInitConfig.get(appname).toString());
            }else {
                pstmt.setString(2,appname);
            }
            pstmt.setDate(3,new Date(System.currentTimeMillis()));
            pstmt.setInt(4,2); //0:失败 1：成功 2：执行中
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            int u=pstmt.executeUpdate();
            ResultSet rs=pstmt.getGeneratedKeys();
            if (rs.next()){
                return rs.getLong(1);
            }else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally {
            MysqlService.release(connection,pstmt,null);
        }
    }

    public int updateSysScheduleLog(Long id, int result,String remark) {
        Connection connection=getConnection();
        PreparedStatement pstmt=null;
        try {
            pstmt=connection.prepareStatement("UPDATE SYS_SCHEDULE_LOG SET RESULT=?,END_TIME=?,REMARK=? WHERE ID=?");
            pstmt.setInt(1,result); //2.执行中 1.成功 0.失败
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(3, remark);
            pstmt.setLong(4,id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }finally {
            release(connection,pstmt,null);
        }
    }

    /**
     * 释放连接
     * @param connection
     * @param pstmt
     * @param resultSet
     */
    public static void release(Connection connection, Statement pstmt, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
