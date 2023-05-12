package com.aliyun.oss.model;

import java.util.List;

public class LocationTransferType extends GenericResult {

    private String region;
    private List<String> transferTypes;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<String> getTransferTypes() {
        return transferTypes;
    }

    public void setTransferTypes(List<String> transferTypes) {
        this.transferTypes = transferTypes;
    }
}