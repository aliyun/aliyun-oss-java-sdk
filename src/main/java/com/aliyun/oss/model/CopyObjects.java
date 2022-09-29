package com.aliyun.oss.model;

public class CopyObjects {
    // Source object key.
    private String sourceKey;
    // Target object key.
    private String targetKey;

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public CopyObjects withSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
        return this;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    public CopyObjects withTargetKey(String targetKey) {
        this.targetKey = targetKey;
        return this;
    }
}
