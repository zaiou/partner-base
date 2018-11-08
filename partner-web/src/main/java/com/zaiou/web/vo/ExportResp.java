package com.zaiou.web.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Description: 导出字段
 * @auther: LB 2018/11/7 09:41
 * @modify: LB 2018/11/7 09:41
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExportResp implements Serializable {
    private static final long serialVersionUID = -6188024384375998650L;

    private String custName;

    private String custAge;
}
