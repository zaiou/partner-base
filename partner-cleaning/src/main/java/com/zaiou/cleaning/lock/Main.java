package com.zaiou.cleaning.lock;

import com.zaiou.cleaning.utils.PLog;
import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author zaiou 2019-05-21
 * @Description:任务入口
 * @modify zaiou 2019-05-21
 */
public class Main {

    /**
     * 任务入口
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 6) {
            throw new BussinessException(ResultInfo.CLEANING_3000.getCode(), ResultInfo.CLEANING_3000.getMsg());
//            System.exit(1);
        }

        //设置script参数
        String scriptFile = args[0];
        String scriptYear = args[1];
        String scriptMonth = args[2];
        String scriptDay = args[3];
        String scriptHour = args[4];
        String scriptSysName = args[5];
        String scriptBusinessName = "";
        if (args.length >= 7) {
            scriptBusinessName = args[6];
        }

        // zk客户端cutator连接zookeeper
        String zookeeperConnectString = "hdp1:2181,hdp2:2181";
        //baseSleepTimeMs：初始sleep时间  maxRetries：最大重试次数
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                zookeeperConnectString, retryPolicy);
        client.start();

        PLog.logger.info("scriptFile=" + scriptFile + " scriptParam=" + scriptYear + " " + scriptMonth + " " + scriptDay + " " + scriptHour + " " + scriptSysName + " " + scriptBusinessName + " 当前业务执行start");
        //执行业务脚本
        processShell(client, null, scriptFile, scriptYear, scriptMonth, scriptDay, scriptHour, scriptSysName, scriptBusinessName);

        PLog.logger.info("scriptFile=" + scriptFile + " scriptParama=" + scriptYear + " " + scriptMonth + " " + scriptDay + " " + scriptHour + " " + scriptSysName + " " + scriptBusinessName + " 当前业务执行end");

        client.close();
    }

    /**
     * 设置zk分布式锁curator,执行业务脚本
     *
     * @Param client
     * @Param latch 同步工具类，允许一个或多个线程一直等待，直到其他线程的操作执行完后再执行
     * @Param scriptFile
     * @Param scriptYear
     * @Param scriptMOnth
     * @Param scriptDay
     * @Param scriptHour
     * @Param scriptSysName
     * @Param scriptBusinessName
     * @Author:zaiou
     * @Date:17:39 2019-05-21
     */
    private static void processShell(CuratorFramework client, CountDownLatch latch,
                                     String scriptFile, String scriptYear, String scriptMonth,
                                     String scriptDay, String scriptHour, String scriptSysName,
                                     String scriptBusinessName) {
        String lockPath = "/locker:" + scriptSysName;
        if (!"".equals(scriptBusinessName)) {
            lockPath = lockPath + "/" + scriptBusinessName;
        }
        //设置zk分布式可重入分布式锁
        InterProcessMutex lock = new InterProcessMutex(client, lockPath);
        //获取锁，设置时长为5秒，获取不到继续获取
        try {
            if (lock.acquire(5, TimeUnit.SECONDS)) {
                PLog.logger.info("-------" + scriptFile + "[" + scriptSysName + "]" + "获得zk锁");
                //执行服务器命令脚本
                Process p = Runtime.getRuntime().exec(new String[]{"sh", scriptFile, scriptYear, scriptMonth, scriptDay, scriptHour, scriptSysName, scriptBusinessName});
                // 在runtime执行大点的命令中，输入流和错误流会不断有流进入存储在JVM的缓冲区中，如果缓冲区的流不被读取被填满时，就会造成runtime的阻塞
                //需要不断的去读取JVM中的缓冲区的流，来防止Runtime的死锁阻塞
                new Mythread(p.getInputStream()).start();
                new Mythread(p.getErrorStream()).start();
                //导致当前线程等待，如有必要，一直要等到由该 Process 对象表示的进程已经终止。如果已终止该子进程，此方法立即返回。如果没有终止该子进程，调用的线程将被阻塞，直到退出子进程，根据惯例，0 表示正常终止
                p.waitFor();
                PLog.logger.info("-------" + scriptFile + "[" + scriptSysName + "]" + "zk锁使用完毕");
            } else {
                PLog.logger.info("----------" + scriptFile + "[" + scriptSysName + "]" + "没有获得zk锁----------");
            }
        } catch (Exception e) {
            PLog.logger.error(e.getMessage(),e);
        } finally {
            try {
                PLog.logger.info("-----" + scriptFile + "[" + scriptSysName + "]" + " 是否获取锁: " + lock.isAcquiredInThisProcess());
                //判断是否持有锁 进而进行锁是否释放的操作
                if (lock.isAcquiredInThisProcess()) {
                    lock.release();
                }
            } catch (Exception e) {
                PLog.logger.info(e.getMessage(),e);
            }
            PLog.logger.info("----------" + scriptFile + "[" + scriptSysName + "]" + "释放锁----------");
        }
    }

    private static class Mythread extends Thread {
        private InputStream in;

        public Mythread(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String result = null;
            try {
                while ((result = reader.readLine()) != null) {
                    PLog.logger.info("当前任务INFO:" + result);
                }
            } catch (IOException e) {
                PLog.logger.error(e.getMessage(),e);
            }
        }
    }
}
