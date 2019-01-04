package com.zaiou.common.mybatis.po;

import java.io.Serializable;
import java.util.Date;

public class SysScheduleLog implements Po {
    private Long id;

    private String taskKey;

    private String taskName;

    private Date exeDate;

    private Integer result;

    private String remark;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public SysScheduleLog(Long id, String taskKey, String taskName, Date exeDate, Integer result, String remark, Date startTime, Date endTime, Date createTime) {
        this.id = id;
        this.taskKey = taskKey;
        this.taskName = taskName;
        this.exeDate = exeDate;
        this.result = result;
        this.remark = remark;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = createTime;
    }

    public SysScheduleLog() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey == null ? null : taskKey.trim();
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    public Date getExeDate() {
        return exeDate;
    }

    public void setExeDate(Date exeDate) {
        this.exeDate = exeDate;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}