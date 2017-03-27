package com.example.administrator.myapplicationsienke.model;

/**
 * Created by Administrator on 2017/3/15 0015.
 */
public class TaskChoose {
    private String taskName;   //任务名称
    private String taskNumber;  //任务编号
    private String checkType;   //安检类型
    private String totalUserNumber;  //总用户数
    private String endTime;   //结束时间

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getTotalUserNumber() {
        return totalUserNumber;
    }

    public void setTotalUserNumber(String totalUserNumber) {
        this.totalUserNumber = totalUserNumber;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
