package com.aliyun.oss.model;

public class GetAccessPointRequest extends GenericRequest {
    private String accessPointName;

    public GetAccessPointRequest() {
    }

    public GetAccessPointRequest(String bucketName) {
        super(bucketName);
    }

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }

    public GetAccessPointRequest withAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
        return this;
    }
}
