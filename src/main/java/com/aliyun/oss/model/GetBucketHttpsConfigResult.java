package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

public class GetBucketHttpsConfigResult extends GenericResult {
    private boolean enable = false;
    private List<String> tlsVersion = new ArrayList<String>();

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<String> getTlsVersion() {
        return tlsVersion;
    }

    public void setTlsVersion(List<String> tlsVersion) {
        this.tlsVersion = tlsVersion;
    }
}
