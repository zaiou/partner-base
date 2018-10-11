package com.zaiou.web;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 将数据库表生成mybatis对应的po、xml文件
 * @auther: LB 2018/9/30 10:13
 * @modify: LB 2018/9/30 10:13
 */
public class MybatisGeneratorPlugs {
    public static void main(String[] args) throws Throwable {

        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
        String path = System.getProperty("user.dir") + "/src/main/resources/generatorConfig.xml";
        File configFile = new File(path);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        System.exit(0);
    }
}
