package com.aliyun.oss.model;

public class GetDescribeRegionsRequest extends GenericRequest {
    private String region;

    public GetDescribeRegionsRequest() {}
    public GetDescribeRegionsRequest(String region) {
        this.region = region;
    }

    public GetDescribeRegionsRequest(String bucketName, String region) {
        super(bucketName);
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public GetDescribeRegionsRequest WithRegion(String region) {
        setRegion(region);
        return this;
    }
}