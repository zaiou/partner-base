package com.zaiou.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.ClassUtils;

/**
 * @Description: 自定义bean的生成策略
 * @auther: LB 2018/9/8 15:17
 * @modify: LB 2018/9/8 15:17
 */
@Slf4j
public class CustomBeanNameGenerator extends AnnotationBeanNameGenerator {
    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        String sourceClsName = ClassUtils.getShortName(definition.getBeanClassName());
        String decapitalize;
        if (sourceClsName == null || sourceClsName.length() == 0) {
            decapitalize = sourceClsName;
        } else {
            char chars[] = sourceClsName.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            decapitalize = new String(chars);
        }
        log.info("自定义bean的生成策略-------->", sourceClsName, decapitalize);
        return decapitalize;
    }
}
