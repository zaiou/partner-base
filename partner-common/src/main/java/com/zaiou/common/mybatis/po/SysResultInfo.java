package com.zaiou.common.mybatis.po;

public class SysResultInfo implements Po {
    private Long id;

    private String system;

    private String resultCode;

    private String message;

    private static final long serialVersionUID = 1L;

    public SysResultInfo(Long id, String system, String resultCode, String message) {
        this.id = id;
        this.system = system;
        this.resultCode = resultCode;
        this.message = message;
    }

    public SysResultInfo() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system == null ? null : system.trim();
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode == null ? null : resultCode.trim();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }
}