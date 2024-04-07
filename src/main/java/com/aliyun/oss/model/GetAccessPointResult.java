package com.aliyun.oss.model;

public class GetAccessPointResult extends GenericResult {
    private String accessPointName;
    private String bucket;
    private String accountId;
    private String networkOrigin;
    private AccessPointVpcConfiguration vpc;
    private String accessPointArn;
    private String creationDate;
    private String alias;
    private String status;
    private AccessPointEndpoints endpoints;

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getNetworkOrigin() {
        return networkOrigin;
    }

    public void setNetworkOrigin(String networkOrigin) {
        this.networkOrigin = networkOrigin;
    }

    public AccessPointVpcConfiguration getVpc() {
        return vpc;
    }

    public void setVpc(AccessPointVpcConfiguration vpc) {
        this.vpc = vpc;
    }

    public String getAccessPointArn() {
        return accessPointArn;
    }

    public void setAccessPointArn(String accessPointArn) {
        this.accessPointArn = accessPointArn;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AccessPointEndpoints getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(AccessPointEndpoints endpoints) {
        this.endpoints = endpoints;
    }
}
