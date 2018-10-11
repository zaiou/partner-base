package com.zaiou.common.exception;

import com.zaiou.common.enums.ResultInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 通用异常
 * @auther: LB 2018/9/19 12:00
 * @modify: LB 2018/9/19 12:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BussinessException extends RuntimeException{
    private static final long serialVersionUID = 8359442347358660195L;

    private String respCode;
    private String respMsg;


    public BussinessException() {
        this.respCode = "";
        this.respMsg = "";
    }

    public BussinessException(String _respCode, String _respMsg) {
        super(_respMsg);
        this.respCode = _respCode;
        this.respMsg = _respMsg;
    }

    public BussinessException(ResultInfo resultInfo, Object... params) {
        //从resultInfo中获取异常消息放到BussinessException的父类异常的message中
        super(resultInfo.getCacheMsg(params));
        this.respCode = resultInfo.getCode();
        //获取BussinessException的父类异常的message
        this.respMsg = getMessage();
    }
}
