package com.zaiou.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @Description: 监控帮助类
 * @auther: LB 2018/11/29 15:27
 * @modify: LB 2018/11/29 15:27
 */
@Slf4j
public class MonitorUtils {

    public static void JobStart(String filePath) {
        JobStart(filePath, null);
    }

    public static void JobException(String filePath, String errorMsg) {
        JobException(filePath, errorMsg, null);
    }

    public static void JobEnd(String filePath) {
        JobEnd(filePath, null);
    }

    public static void JobStart(String filePath, Date date) {
        write(filePath, "JobStart", date);
    }

    public static void JobException(String filePath, String errorMsg, Date date) {
        write(filePath, String.format("Exception,%s", errorMsg), date);
    }

    public static void JobEnd(String filePath, Date date) {
        write(filePath, "JobEnd", date);
    }

    private static void write(String filePath, String msg, Date date) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath, true));
            if(date != null){
                writer.write(String.format("%s, INFO :%s\n", DateUtils.dateToStrWithFormat2(date), msg));
            } else {
                writer.write(String.format("%s, INFO :%s\n", DateUtils.getDate(), msg));
            }
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if(writer!=null){writer.close();}
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
