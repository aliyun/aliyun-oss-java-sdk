package com.aliyun.oss.model;

public class PutAccessPointPolicyRequest extends GenericRequest {
    private String accessPointPolicy;
    private String accessPointName;

    public PutAccessPointPolicyRequest() {
    }

    public PutAccessPointPolicyRequest(String bucketName) {
        super(bucketName);
    }

    public String getAccessPointPolicy() {
        return accessPointPolicy;
    }

    public void setAccessPointPolicy(String accessPointPolicy) {
        this.accessPointPolicy = accessPointPolicy;
    }

    public PutAccessPointPolicyRequest withAccessPointPolicy(String accessPointPolicy) {
        this.accessPointPolicy = accessPointPolicy;
        return this;
    }

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }

    public PutAccessPointPolicyRequest withAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
        return this;
    }
}
