package com.zaiou.cleaning.mapper;

import com.zaiou.cleaning.db.MysqlService;

/**
 * @author zaiou 2019-05-27
 * @Description: 下载任务插入到定时任务日志表 -- 任务下载结束
 * @modify zaiou 2019-05-27
 */
public class UpdateSysScheduleLog {
    public static void main(String[] args) {
        if (args.length != 2){
            System.err.println("use:<id><result>");
            System.exit(0);
        }
        Long id=Long.valueOf(args[0]);
        int result=Integer.parseInt(args[1]);
        MysqlService mysqlService=new MysqlService();
        mysqlService.updateSysScheduleLog(id,result,"");
    }
}
