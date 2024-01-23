package com.aliyun.oss.model;

public class DeleteBucketDataRedundancyTransitionRequest extends GenericRequest {
    private String taskId;

    public DeleteBucketDataRedundancyTransitionRequest() {
    }

    public DeleteBucketDataRedundancyTransitionRequest(String bucketName, String taskId) {
        super(bucketName);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
