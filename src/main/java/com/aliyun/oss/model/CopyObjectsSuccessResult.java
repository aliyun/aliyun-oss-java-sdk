package com.aliyun.oss.model;

public class CopyObjectsSuccessResult extends  CopyObjects {
    // Target object's ETag
    private String eTag;

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }
}
