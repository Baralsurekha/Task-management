package com.example.taskmanagement;

public class taskModel {
    private String taskName;
    private String taskDescription;
    private String taskStatus;
    private long deadlineMillis;

    public taskModel() {

    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setDeadlineMillis(long deadlineMillis) {
        this.deadlineMillis = deadlineMillis;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public long getDeadlineMillis() {
        return deadlineMillis;
    }
}
