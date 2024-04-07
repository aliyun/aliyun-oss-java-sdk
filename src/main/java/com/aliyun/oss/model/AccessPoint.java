package com.aliyun.oss.model;

public class AccessPoint {
    private String bucket;
    private String accessPointName;
    private String alias;
    private String networkOrigin;
    private String status;
    private AccessPointVpcConfiguration vpc;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNetworkOrigin() {
        return networkOrigin;
    }

    public void setNetworkOrigin(String networkOrigin) {
        this.networkOrigin = networkOrigin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AccessPointVpcConfiguration getVpc() {
        return vpc;
    }

    public void setVpc(AccessPointVpcConfiguration vpc) {
        this.vpc = vpc;
    }
}
