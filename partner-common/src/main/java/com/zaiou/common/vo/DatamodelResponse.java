package com.zaiou.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Description: 模型响应基础类
 * @auther: LB 2018/10/31 09:19
 * @modify: LB 2018/10/31 09:19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Component
@Scope("prototype")
public class DatamodelResponse extends Response {
    private static final long serialVersionUID = 1L;
}
