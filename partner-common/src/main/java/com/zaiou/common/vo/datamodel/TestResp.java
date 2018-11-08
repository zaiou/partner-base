package com.zaiou.common.vo.datamodel;

import com.zaiou.common.vo.DatamodelResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @auther: LB 2018/10/31 09:18
 * @modify: LB 2018/10/31 09:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Component
@Scope("prototype")
public class TestResp extends DatamodelResponse {
    private static final long serialVersionUID = 1L;

    private String name;
}
