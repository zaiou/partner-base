package com.zaiou.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Description: 基础vo
 * @auther: LB 2018/9/20 20:24
 * @modify: LB 2018/9/20 20:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@Scope("prototype")
@Primary
public class Request implements Serializable {
    private static final long serialVersionUID = -8175064641670758182L;

    private String threadName = Thread.currentThread().getName();// 线程号

    //交易码
    private String jym;
}
