package com.zaiou.common.mybatis.po;

import java.util.Date;

public class SysParams implements Po {
    private Long id;

    private String code;

    private String value;

    private String name;

    private String valueType;

    private Integer updateFlag;

    private Date craeteTime;

    private String craeteUser;

    private Date updateTime;

    private String updateUser;

    private static final long serialVersionUID = 1L;

    public SysParams(Long id, String code, String value, String name, String valueType, Integer updateFlag, Date craeteTime, String craeteUser, Date updateTime, String updateUser) {
        this.id = id;
        this.code = code;
        this.value = value;
        this.name = name;
        this.valueType = valueType;
        this.updateFlag = updateFlag;
        this.craeteTime = craeteTime;
        this.craeteUser = craeteUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
    }

    public SysParams() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType == null ? null : valueType.trim();
    }

    public Integer getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(Integer updateFlag) {
        this.updateFlag = updateFlag;
    }

    public Date getCraeteTime() {
        return craeteTime;
    }

    public void setCraeteTime(Date craeteTime) {
        this.craeteTime = craeteTime;
    }

    public String getCraeteUser() {
        return craeteUser;
    }

    public void setCraeteUser(String craeteUser) {
        this.craeteUser = craeteUser == null ? null : craeteUser.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }
}