package com.zaiou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 数据导出模板枚举类
 * @auther: LB 2018/11/6 15:24
 * @modify: LB 2018/11/6 15:24
 */
@Getter
@AllArgsConstructor
public enum ExportTempFileEnum {

    export_1001("测试档案信息导出", "1001_temp"),
    ;

    private String type;
    private String name;
}
