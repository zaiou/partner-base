package com.zaiou.cleaning.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author zaiou 2019-05-27
 * @Description: 配置文件操作
 * @modify zaiou 2019-05-27
 */
public class PropertiesUtil {

    public static Map<String, String> propertiesMap = new HashMap<String, String>();

    static {
        String userDir = System.getProperty("user.dir");
        String cleaningPropertiesPath = "/data/partner/partner-cleaning/config/cleaning.properties";
//        String cleaningPropertiesPath = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"/config/cleaning.properties";
        System.out.println("当前路径"+cleaningPropertiesPath);

        InputStream in = null;

        try {
            in = new BufferedInputStream(new FileInputStream(cleaningPropertiesPath));
            ResourceBundle resourceBundle = new PropertyResourceBundle(in);
            for (String key : resourceBundle.keySet()) {
                String value = resourceBundle.getString(key);
                propertiesMap.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
