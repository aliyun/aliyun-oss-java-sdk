package com.aliyun.oss.model;

public class RegionInfo extends GenericResult {

    private String region;
    private String internetEndpoint;
    private String internalEndpoint;
    private String accelerateEndpoint;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getInternetEndpoint() {
        return internetEndpoint;
    }

    public void setInternetEndpoint(String internetEndpoint) {
        this.internetEndpoint = internetEndpoint;
    }

    public String getInternalEndpoint() {
        return internalEndpoint;
    }

    public void setInternalEndpoint(String internalEndpoint) {
        this.internalEndpoint = internalEndpoint;
    }

    public String getAccelerateEndpoint() {
        return accelerateEndpoint;
    }

    public void setAccelerateEndpoint(String accelerateEndpoint) {
        this.accelerateEndpoint = accelerateEndpoint;
    }
}
