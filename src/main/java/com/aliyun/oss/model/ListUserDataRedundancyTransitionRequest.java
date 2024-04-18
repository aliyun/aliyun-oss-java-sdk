package com.aliyun.oss.model;

public class ListUserDataRedundancyTransitionRequest extends GenericRequest {
    private DataRedundancyType redundancyType;
    // The max objects to return---By default it's 100.
    private Integer maxKeys;
    // The marker filter----objects returned whose key must be greater than the
    // maker in lexicographical order.
    private String continuationToken;

    public ListUserDataRedundancyTransitionRequest() {
    }

    public ListUserDataRedundancyTransitionRequest(String continuationToken, Integer maxKeys) {
        setContinuationToken(continuationToken);
        if (maxKeys != null) {
            setMaxKeys(maxKeys);
        }
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    public ListUserDataRedundancyTransitionRequest withContinuationToken(String continuationToken) {
        setContinuationToken(continuationToken);
        return this;
    }

    public Integer getMaxKeys() {
        return maxKeys;
    }

    public void setMaxKeys(Integer maxKeys) {
        this.maxKeys = maxKeys;
    }

    public ListUserDataRedundancyTransitionRequest withMaxKeys(Integer maxKeys) {
        setMaxKeys(maxKeys);
        return this;
    }

    public DataRedundancyType getRedundancyType() {
        return redundancyType;
    }

    public void setRedundancyType(DataRedundancyType redundancyType) {
        this.redundancyType = redundancyType;
    }
}
