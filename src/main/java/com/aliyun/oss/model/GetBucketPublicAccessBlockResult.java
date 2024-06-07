package com.aliyun.oss.model;

public class GetBucketPublicAccessBlockResult extends GenericResult {
    private boolean blockPublicAccess = false;

    public boolean getBlockPublicAccess() {
        return blockPublicAccess;
    }

    public void setBlockPublicAccess(boolean blockPublicAccess) {
        this.blockPublicAccess = blockPublicAccess;
    }
}
