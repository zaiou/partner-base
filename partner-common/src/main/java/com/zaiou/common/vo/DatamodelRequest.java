package com.zaiou.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Description: 模型请求基础类
 * @auther: LB 2018/10/30 11:53
 * @modify: LB 2018/10/30 11:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Component
@Scope("prototype")
public class DatamodelRequest extends Request {
    private static final long serialVersionUID = 1L;
}
