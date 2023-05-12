package com.aliyun.oss.model;

public class PutBucketRTCRequest extends GenericRequest {
    private RtcStatus status;
    private String ruleID;
    public PutBucketRTCRequest(String bucketName, String ruleID, RtcStatus status) {
        super(bucketName);
        this.ruleID = ruleID;
        this.status = status;
    }
    public RtcStatus getStatus() {
        return status;
    }
    public void setStatus(RtcStatus status) {
        this.status = status;
    }
    public String getRuleID() {
        return ruleID;
    }
    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
    }
}