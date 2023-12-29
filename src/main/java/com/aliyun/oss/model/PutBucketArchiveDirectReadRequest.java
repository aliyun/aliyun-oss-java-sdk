package com.aliyun.oss.model;

public class PutBucketArchiveDirectReadRequest extends GenericRequest {
    private boolean enabled = false;
    public PutBucketArchiveDirectReadRequest(String bucketName) {
        super(bucketName);
    }
    public PutBucketArchiveDirectReadRequest(String bucketName, boolean enabled) {
        super(bucketName);
        this.enabled = enabled;
    }
    public boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public PutBucketArchiveDirectReadRequest withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
