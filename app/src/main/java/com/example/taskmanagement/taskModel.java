package com.example.taskmanagement;

public class taskModel {
    private String taskName;
    private String taskDescription;
    private String taskStatus;
    private long deadlineMillis;
    private long notideadlineMillis;
    private boolean isCompleted;
    private String documentId;

    public taskModel() {

    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setDeadlineMillis(long deadlineMillis) {
        this.deadlineMillis = deadlineMillis;
    }
    public void setnotideadlineMillis(long deadlineMillis) {
        this.notideadlineMillis = deadlineMillis;
    }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public void setDocumentId(String documentId) { this.documentId = documentId; } // setter
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
    public long getnotideadlineMillis() {
        return notideadlineMillis;
    }
    public boolean isCompleted() { return isCompleted; }
    public String getDocumentId() { return documentId; }

}

