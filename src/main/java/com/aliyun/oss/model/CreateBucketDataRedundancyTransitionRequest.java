package com.aliyun.oss.model;

public class CreateBucketDataRedundancyTransitionRequest extends GenericRequest {
    private String targetType;

    public CreateBucketDataRedundancyTransitionRequest(String bucketName, String targetType) {
        super(bucketName);
        this.targetType = targetType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}
