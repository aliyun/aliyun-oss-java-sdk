package com.aliyun.oss.model;

public class CreateAccessPointRequest extends GenericRequest {
    private String accessPointName;
    private String networkOrigin;
    private AccessPointVpcConfiguration vpc;

    public CreateAccessPointRequest() {
    }

    public CreateAccessPointRequest(String bucketName) {
        super(bucketName);
    }

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }

    public CreateAccessPointRequest withAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
        return this;
    }

    public String getNetworkOrigin() {
        return networkOrigin;
    }

    public void setNetworkOrigin(String networkOrigin) {
        this.networkOrigin = networkOrigin;
    }

    public CreateAccessPointRequest withNetworkOrigin(String networkOrigin) {
        this.networkOrigin = networkOrigin;
        return this;
    }

    public AccessPointVpcConfiguration getVpc() {
        return vpc;
    }

    public void setVpc(AccessPointVpcConfiguration vpc) {
        this.vpc = vpc;
    }

    public CreateAccessPointRequest withVpc(AccessPointVpcConfiguration vpc) {
        this.vpc = vpc;
        return this;
    }
}
