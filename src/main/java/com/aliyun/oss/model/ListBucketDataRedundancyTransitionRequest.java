package com.aliyun.oss.model;

public class ListBucketDataRedundancyTransitionRequest extends GenericRequest {
    private DataRedundancyType redundancyType;


    public ListBucketDataRedundancyTransitionRequest() {
    }


    public DataRedundancyType getRedundancyType() {
        return redundancyType;
    }

    public void setRedundancyType(DataRedundancyType redundancyType) {
        this.redundancyType = redundancyType;
    }
}
