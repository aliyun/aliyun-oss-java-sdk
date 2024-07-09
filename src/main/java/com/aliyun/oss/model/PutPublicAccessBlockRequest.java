package com.aliyun.oss.model;

public class PutPublicAccessBlockRequest extends GenericRequest {
    private boolean blockPublicAccess = false;

    public PutPublicAccessBlockRequest() {
    }

    public PutPublicAccessBlockRequest(boolean blockPublicAccess) {
        this.blockPublicAccess = blockPublicAccess;
    }

    public boolean getBlockPublicAccess() {
        return blockPublicAccess;
    }

    public void setBlockPublicAccess(boolean blockPublicAccess) {
        this.blockPublicAccess = blockPublicAccess;
    }

    public PutPublicAccessBlockRequest withBlockPublicAccess(boolean blockPublicAccess) {
        this.blockPublicAccess = blockPublicAccess;
        return this;
    }
}
