package com.aliyun.oss.model;

public class CreateBucketDataRedundancyTransitionResult extends GenericResult {
    private String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}