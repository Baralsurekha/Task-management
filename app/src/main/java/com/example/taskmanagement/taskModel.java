package com.example.taskmanagement;

public class taskModel {
    private String taskName;
    private String taskDescription;
    private String taskStatus;
    private long deadlineMillis;

    public taskModel(String taskName, String taskDescription, String taskStatus, long deadlineMillis) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
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
