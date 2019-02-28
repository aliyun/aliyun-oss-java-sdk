package com.aliyun.oss.model;

import java.io.Serializable;

public class BucketVersion extends GenericResult implements Serializable {

    private String bucketVersion;

    /**
     * init BucketVersion
     * @param bucketVersion
     */
    public BucketVersion(String bucketVersion) {
        this.bucketVersion = bucketVersion;
    }

    /**
     * get bucket Version
     * @return
     */
    public String getBucketVersion() {
        return bucketVersion;
    }

    /**
     * set bucket version
     * @param bucketVersion
     */
    public void setBucketVersion(String bucketVersion) {
        this.bucketVersion = bucketVersion;
    }
}