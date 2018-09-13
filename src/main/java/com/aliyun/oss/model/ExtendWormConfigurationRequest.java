package com.aliyun.oss.model;

public class ExtendWormConfigurationRequest extends GenericRequest{

    private String wormId;
    private int retentionPeriodInDays;

    public ExtendWormConfigurationRequest(String bucketName, String wormId, int retentionPeriodInDays) {
        super(bucketName);
        this.wormId = wormId;
        this.retentionPeriodInDays = retentionPeriodInDays;
    }

    public String getWormId() {
        return wormId;
    }

    public void setWormId(String wormId) {
        this.wormId = wormId;
    }

    public int getRetentionPeriodInDays() {
        return retentionPeriodInDays;
    }

    public void setRetentionPeriodInDays(int retentionPeriodInDays) {
        this.retentionPeriodInDays = retentionPeriodInDays;
    }
}
