package com.zaiou.common.vo.datamodel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 消息处理实体
 * @auther: LB 2018/11/1 10:16
 * @modify: LB 2018/11/1 10:16
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProcessMessageReq {
    private String threadName;

    private String currentName;

    private String testId;
}
