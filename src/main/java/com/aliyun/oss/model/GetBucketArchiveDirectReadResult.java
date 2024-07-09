package com.aliyun.oss.model;

public class GetBucketArchiveDirectReadResult extends GenericResult {
    private boolean enabled;
    public boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public GetBucketArchiveDirectReadResult withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}