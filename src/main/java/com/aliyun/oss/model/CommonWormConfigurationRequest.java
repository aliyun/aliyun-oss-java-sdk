package com.aliyun.oss.model;

public class CommonWormConfigurationRequest extends GenericRequest {
    private String wormId;

    public CommonWormConfigurationRequest(String bucketName, String wormId) {
        super(bucketName);
        this.wormId = wormId;
    }

    public String getWormId() {
        return wormId;
    }

    public void setWormId(String wormId) {
        this.wormId = wormId;
    }
}
