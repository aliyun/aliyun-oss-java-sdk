package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

public class PutBucketHttpsConfigRequest extends GenericRequest {
    private boolean enable = false;
    private List<String> tlsVersion = new ArrayList<String>();
    public PutBucketHttpsConfigRequest(String bucketName) {
        super(bucketName);
    }
    public PutBucketHttpsConfigRequest(String bucketName, boolean enabled, List<String> tlsVersion) {
        super(bucketName);
        this.tlsVersion = tlsVersion;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public PutBucketHttpsConfigRequest withEnabled(boolean enable) {
        this.enable = enable;
        return this;
    }

    public List<String> getTlsVersion() {
        return tlsVersion;
    }

    public void setTlsVersion(List<String> tlsVersion) {
        this.tlsVersion = tlsVersion;
    }

    public PutBucketHttpsConfigRequest withTlsVersion(List<String> tlsVersion) {
        this.tlsVersion = tlsVersion;
        return this;
    }
}
