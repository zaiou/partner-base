package com.zaiou.datamodel;

import com.zaiou.common.config.CustomBeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@ComponentScan(value = "com.zaiou.**", nameGenerator = CustomBeanNameGenerator.class)
@EnableAutoConfiguration
@ServletComponentScan
public class DatamodelApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatamodelApplication.class, args);
    }
}
