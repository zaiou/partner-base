package com.zaiou.datamodel;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: mapper自动生成
 * @auther: LB 2018/10/29 15:28
 * @modify: LB 2018/10/29 15:28
 */
public class MybatisGeneratorPlugs {
    public static void main(String[] args) throws Throwable {

        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
//		String path = System.getProperty("user.dir") + "/generatorConfig-write.xml";// write
        String path = System.getProperty("user.dir") + "/partner-datamodel/generatorConfig-read.xml";//read
        File configFile = new File(path);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        System.exit(0);
    }
}
