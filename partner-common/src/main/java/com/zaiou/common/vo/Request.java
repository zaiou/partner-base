package com.zaiou.common.vo;

import java.io.Serializable;

/**
 * @Description: 基础vo
 * @auther: LB 2018/9/20 20:24
 * @modify: LB 2018/9/20 20:24
 */
public class Request implements Serializable {
    private static final long serialVersionUID = -8175064641670758182L;

    private String threadName = Thread.currentThread().getName();// 线程号
}
