package com.aliyun.oss.model;

public class GetBucketDataRedundancyTransitionResult extends GenericResult {
    private String taskId;
    private String createTime;
    private String startTime;
    private String endTime;
    private String status;
    private int estimatedRemainingTime;
    private int processPercentage;
    private String bucket;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getEstimatedRemainingTime() {
        return estimatedRemainingTime;
    }

    public void setEstimatedRemainingTime(int estimatedRemainingTime) {
        this.estimatedRemainingTime = estimatedRemainingTime;
    }

    public int getProcessPercentage() {
        return processPercentage;
    }

    public void setProcessPercentage(int processPercentage) {
        this.processPercentage = processPercentage;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
