package com.aliyun.oss.model;

public class GetObjectAclRequest extends WebServiceRequest {

    private String bucketName;
    private String key;

    private String versionId;

    public GetObjectAclRequest() {
    }

    public GetObjectAclRequest(String bucketName) {
        this(bucketName, null);
    }

    public GetObjectAclRequest(String bucketName, String key) {
        this.bucketName = bucketName;
        this.key = key;
        setVersionId(null);
    }

    public GetObjectAclRequest(String bucketName, String key, String versionId) {
        this.bucketName = bucketName;
        this.key = key;
        setVersionId(versionId);
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public GetObjectAclRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GetObjectAclRequest withKey(String key) {
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
