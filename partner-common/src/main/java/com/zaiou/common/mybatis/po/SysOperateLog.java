package com.zaiou.common.mybatis.po;

import java.util.Date;

public class SysOperateLog implements Po {
    private Long id;

    private String type;

    private String func;

    private String text;

    private String operateUser;

    private String operateName;

    private Date operateTime;

    private String ip;

    private static final long serialVersionUID = 1L;

    public SysOperateLog(Long id, String type, String func, String text, String operateUser, String operateName, Date operateTime, String ip) {
        this.id = id;
        this.type = type;
        this.func = func;
        this.text = text;
        this.operateUser = operateUser;
        this.operateName = operateName;
        this.operateTime = operateTime;
        this.ip = ip;
    }

    public SysOperateLog() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func == null ? null : func.trim();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text == null ? null : text.trim();
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser == null ? null : operateUser.trim();
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName == null ? null : operateName.trim();
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }
}