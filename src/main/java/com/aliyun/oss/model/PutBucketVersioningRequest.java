package com.aliyun.oss.model;

public class PutBucketVersioningRequest extends  GenericRequest {

    private BucketVersion bucketVersion;

    public PutBucketVersioningRequest(String bucketName) {
        super(bucketName);
    }

    public BucketVersion getBucketVersion() {
        return bucketVersion;
    }

    public void setBucketVersion(BucketVersion BucketVersion) {
        this.bucketVersion = BucketVersion;
    }
}