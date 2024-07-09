package com.aliyun.oss.model;

public class CreateAccessPointResult extends GenericResult {
    private String accessPointArn;
    private String alias;

    public String getAccessPointArn() {
        return accessPointArn;
    }

    public void setAccessPointArn(String accessPointArn) {
        this.accessPointArn = accessPointArn;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
