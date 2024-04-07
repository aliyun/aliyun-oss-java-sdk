package com.aliyun.oss.model;

public class GetAccessPointPolicyRequest extends GenericRequest {
    private String accessPointName;

    public GetAccessPointPolicyRequest() {
    }

    public GetAccessPointPolicyRequest(String bucketName) {
        super(bucketName);
    }

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }

    public GetAccessPointPolicyRequest withAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
        return this;
    }
}
