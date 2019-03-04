package com.aliyun.oss.model;

public class GetObjectTaggingRequest extends  WebServiceRequest {

    private String bucketName;
    private String key;

    private String versionId;

    public GetObjectTaggingRequest() {
    }

    public GetObjectTaggingRequest(String bucketName) {
        this(bucketName, null);
    }

    public GetObjectTaggingRequest(String bucketName, String key) {
        this.bucketName = bucketName;
        this.key = key;
        this.versionId = null;
    }

    public GetObjectTaggingRequest(String bucketName, String key, String versionId) {
        this.bucketName = bucketName;
        this.key = key;
        this.versionId = versionId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public GetObjectTaggingRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GetObjectTaggingRequest withKey(String key) {
        setKey(key);
        return this;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getVersionId() {
        return versionId;
    }
}
