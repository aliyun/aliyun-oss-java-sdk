package com.aliyun.oss.model;

public class PutBucketPublicAccessBlockRequest extends GenericRequest {
    private boolean blockPublicAccess = false;

    public PutBucketPublicAccessBlockRequest(String bucketName) {
        super(bucketName);
    }

    public PutBucketPublicAccessBlockRequest(String bucketName, boolean blockPublicAccess) {
        super(bucketName);
        this.blockPublicAccess = blockPublicAccess;
    }

    public boolean getBlockPublicAccess() {
        return blockPublicAccess;
    }

    public void setBlockPublicAccess(boolean blockPublicAccess) {
        this.blockPublicAccess = blockPublicAccess;
    }

    public PutBucketPublicAccessBlockRequest withBlockPublicAccess(boolean blockPublicAccess) {
        this.blockPublicAccess = blockPublicAccess;
        return this;
    }
}
