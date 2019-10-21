package com.zaiou.cleaning.mapper;

import com.zaiou.cleaning.db.MysqlService;
import com.zaiou.cleaning.utils.PLog;
import com.zaiou.cleaning.utils.PropertiesUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;

/**
 * @author zaiou 2019-05-23
 * @Description: 下载任务插入到定时任务日志表 -- 任务下载中
 * @modify zaiou 2019-05-23
 */
public class InsertSysScheduleLog {
    public static void main(String[] args) {
        if (args.length!=1){
            System.err.println("use:<appname> ");
            System.exit(0);
        }

        String appname=args[0];
        System.out.println("--InsertSysScheduleLog插入数据--状态下载中");
        MysqlService mysqlService=new MysqlService();
        //任务日志表插入当前状态--下载中
        long id=mysqlService.insertSysScheduleLog(appname);
        System.out.println("当前任务id："+id);
        //在hdfs中创建一个id文件
        Configuration conf=new Configuration();
        String path= PropertiesUtil.propertiesMap.get("spark.hdfs_path")+"/user/cleaning/staging"+"/jobid/"+appname+"/"+id;
        try {
            PLog.logger.info("向hdfs中插入状态下载中【appname】:"+appname +"jobid="+id);
            FileSystem fs=FileSystem.get(URI.create(path),conf);
            FSDataOutputStream fsDataOutputStream=fs.create(new Path(path));
            fsDataOutputStream.close();
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
