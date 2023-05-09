package com.aliyun.oss.model;

public class DescribeRegionsRequest extends GenericRequest {
    private String region;

    public DescribeRegionsRequest() {}
    public DescribeRegionsRequest(String region) {
        this.region = region;
    }

    public DescribeRegionsRequest(String bucketName, String region) {
        super(bucketName);
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public DescribeRegionsRequest WithRegion(String region) {
        setRegion(region);
        return this;
    }
}