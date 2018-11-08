package com.zaiou.common.vo.datamodel;

import com.zaiou.common.vo.DatamodelRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @auther: LB 2018/10/30 11:50
 * @modify: LB 2018/10/30 11:50
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Component
@Scope("prototype")
public class TestReq extends DatamodelRequest {
    private static final long serialVersionUID = 1L;

    private String name;
}
