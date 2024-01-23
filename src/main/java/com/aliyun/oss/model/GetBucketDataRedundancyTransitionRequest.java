package com.aliyun.oss.model;

public class GetBucketDataRedundancyTransitionRequest extends GenericRequest {
    private String taskId;

    public GetBucketDataRedundancyTransitionRequest() {
    }

    public GetBucketDataRedundancyTransitionRequest(String bucketName, String taskId) {
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
