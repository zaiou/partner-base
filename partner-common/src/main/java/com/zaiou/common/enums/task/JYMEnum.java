package com.zaiou.common.enums.task;

import lombok.Getter;

/**
 * @Description:定时任务交易码
 * @auther: LB 2018/11/29 15:20
 * @modify: LB 2018/11/29 15:20
 */
@Getter
public enum  JYMEnum {

    F000000("F000000", "测试")
    ;

    private String trancode;
    private String msg;

    private JYMEnum(String trancode, String msg) {
        this.trancode = trancode;
        this.msg = msg;
    }

    public JYMEnum getEnum(String trancode) {
        JYMEnum[] values = values();
        for (JYMEnum value : values) {
            if (trancode.equalsIgnoreCase(value.getTrancode())) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", trancode, msg);
    }
}
