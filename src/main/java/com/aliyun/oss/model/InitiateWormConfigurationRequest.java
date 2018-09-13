package com.aliyun.oss.model;

public class InitiateWormConfigurationRequest extends GenericRequest {

    private int retentionPeriodInDays;

    public InitiateWormConfigurationRequest(String bucketName, int retentionPeriodInDays) {
        super(bucketName);
        this.retentionPeriodInDays = retentionPeriodInDays;
    }

    public int getRetentionPeriodInDays() {
        return retentionPeriodInDays;
    }

    public void setRetentionPeriodInDays(int retentionPeriodInDays) {
        this.retentionPeriodInDays = retentionPeriodInDays;
    }
}
