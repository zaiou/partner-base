package com.zaiou.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

/**
 * @Description:
 * @auther: LB 2018/11/29 15:24
 * @modify: LB 2018/11/29 15:24
 */
@Slf4j
public class Tools {

    /**
     * getOnlyPK(利用时间yyyyMMddHHmmssSSS 格式,然后补4为的随机数得到的唯一值)
     *
     * @Title: getOnlyPK
     * @Description: 利用时间yyyyMMddHHmmssSSS 格式,然后补4为的随机数得到的唯一值
     * @Date May 4, 2012 2:58:50 PM
     * @modifyDate May 4, 2012 2:58:50 PM
     * @return String 得到的唯一值 java.lang.String
     */
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static String getOnlyPK() {
        int min = 1000000;
        int max = 9999999;
        int tmp = new Random().nextInt(Integer.MAX_VALUE);
        StringBuilder sb = new StringBuilder();
        sb.append(getSampleDateFormat_yyyyMMddHHmmssSSS().format(new Date()));
        sb.append(tmp % (max - min + 1) + min);
        return sb.toString();
    }

    public static String getOnlyRoleId() {
        int min = 100;
        int max = 999;
        int tmp = new Random().nextInt(Integer.MAX_VALUE);
        StringBuilder sb = new StringBuilder();
        sb.append(getSampleDateFormat_yyyyMMddHHmmssSSS().format(new Date()));
        sb.append(tmp % (max - min + 1) + min);
        return sb.toString();
    }

    public static SimpleDateFormat getSampleDateFormat_yyyyMMddHHmmssSSS() {
        return simpleDateFormat;
    }

    /**
     * 执行指令 暂不支持Window操作系统返回结果，例如ps aux|grep java的执行结果
     */
    public static final String executeCommand(String command) {
        if (isWindowsOS()) {
            return "This Method is not support for Window OS!";
        }
        StringBuffer sb = new StringBuffer();
        try {
            if (StringUtils.isBlank(command)) {
                command = "ps aux|grep java";
            }
            log.info("command line: {}\n", command);
            Runtime runtime = Runtime.getRuntime();
            Process pro = runtime.exec(new String[] { "/bin/sh", "-c", command });
            pro.waitFor();
            BufferedReader read = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String result = null;
            while ((result = read.readLine()) != null) {
                sb.append(result + "\n");
            }
            read.close();
            read = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
            result = null;
            while ((result = read.readLine()) != null) {
                sb.append(result + "\n");
            }
            read.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        log.info("command line result: {}\n", sb.toString());
        return sb.toString();
    }

    public static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }

    public static boolean isLinuxOS() {
        boolean isLinuxOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("linux") > -1) {
            isLinuxOS = true;
        }
        return isLinuxOS;
    }

    public static boolean isMacOS() {
        boolean isOSX = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("mac") > -1) {
            isOSX = true;
        }
        return isOSX;
    }

    /**
     *  获取本机ip地址
     * @return
     */
    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("IP地址获取失败", e);
        }
        return "";
    }

    /**
     * 获取本机mac地址
     * @return
     */
    public static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            byte[] mac = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    mac = netInterface.getHardwareAddress();
                    if (mac != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < mac.length; i++) {
                            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                        }
                        if (sb.length() > 0) {
                            return sb.toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("MAC地址获取失败", e);
        }
        return "";
    }

}
