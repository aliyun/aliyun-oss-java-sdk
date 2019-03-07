package com.aliyun.oss.model;

public class PutBucketVersioningRequest extends  GenericRequest {

    private String bucketVersion;

    public PutBucketVersioningRequest(String bucketName) {
        super(bucketName);
    }

    public String getBucketVersion() {
        return bucketVersion;
    }

    public void setBucketVersion(String bucketVersion) {
        this.bucketVersion = bucketVersion;
    }
}