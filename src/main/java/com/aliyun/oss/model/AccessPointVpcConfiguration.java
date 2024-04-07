package com.aliyun.oss.model;

public class AccessPointVpcConfiguration {
    private String vpcId;

    public String getVpcId() {
        return vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public AccessPointVpcConfiguration withVpcId(String vpcId) {
        this.vpcId = vpcId;
        return this;
    }
}
