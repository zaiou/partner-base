package com.zaiou.common.annotation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.zaiou.common.enums.SensitiveType;
import com.zaiou.common.utils.DesensitizedUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Objects;

/**
 * @Description: 数据脱敏
 * @auther: LB 2018/10/10 10:04
 * @modify: LB 2018/10/10 10:04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveSerialized extends JsonSerializer<String> implements ContextualSerializer {
    private SensitiveType type;

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty)
            throws JsonMappingException {
        if (beanProperty != null) { // 为空直接跳过
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) { // 非 String 类直接跳过
                Desensitized sensitiveInfo = beanProperty.getAnnotation(Desensitized.class);
                if (sensitiveInfo == null) {
                    sensitiveInfo = beanProperty.getContextAnnotation(Desensitized.class);
                }
                if (sensitiveInfo != null) { // 如果能得到注解，就将注解的 value 传入 SensitiveInfoSerialize
                    return new SensitiveSerialized(sensitiveInfo.type());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }

    @Override
    public void serialize(String data, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
        switch (this.type) {
            // 证件号
            case ID_CARD: {
                jsonGenerator.writeString(DesensitizedUtils.idCardNum(data));
                break;
            }
            // 手机号
            case MOBILE_PHONE: {
                jsonGenerator.writeString(DesensitizedUtils.mobilePhone(data));
                break;
            }
            // 账号
            case BANK_CARD: {
                jsonGenerator.writeString(DesensitizedUtils.bankCard(data));
                break;
            }
            // 户名
            case USER_NAME: {
                jsonGenerator.writeString(DesensitizedUtils.userName(data));
                break;
            }
        }
    }

}