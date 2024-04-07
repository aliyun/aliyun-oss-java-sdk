package com.aliyun.oss.model;

public class DeleteAccessPointPolicyRequest extends GenericRequest {
    private String accessPointName;

    public DeleteAccessPointPolicyRequest() {
    }

    public DeleteAccessPointPolicyRequest(String bucketName) {
        super(bucketName);
    }

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }

    public DeleteAccessPointPolicyRequest withAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
        return this;
    }
}
