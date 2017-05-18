package com.example.administrator.myapplicationsienke.model;

/**
 * Created by Administrator on 2017-05-17.
 */
public class SelectTaskItem {
    private String taskId;     //任务ID
    private String taskName;   //任务名称
    private boolean isChecked;  //是否是选中状态

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
