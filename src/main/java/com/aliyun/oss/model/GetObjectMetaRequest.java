package com.aliyun.oss.model;

public class GetObjectMetaRequest extends  WebServiceRequest {
    private String bucketName;
    private String key;
    private String versionId;

    public GetObjectMetaRequest() {
    }

    public GetObjectMetaRequest(String bucketName) {
        this(bucketName, null);
    }

    public GetObjectMetaRequest(String bucketName, String key) {
        this.bucketName = bucketName;
        this.versionId = null;
        this.key = key;
    }

    public GetObjectMetaRequest(String bucketName, String key, String versionId) {
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

    public GetObjectMetaRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GetObjectMetaRequest withKey(String key) {
        setKey(key);
        return this;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }
}

