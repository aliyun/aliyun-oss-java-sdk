package com.aliyun.oss.model;

public class DeleteAccessPointRequest extends GenericRequest {
    private String accessPointName;

    public DeleteAccessPointRequest() {
    }

    public DeleteAccessPointRequest(String bucketName) {
        super(bucketName);
    }

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }

    public DeleteAccessPointRequest withAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
        return this;
    }
}
