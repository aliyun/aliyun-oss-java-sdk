package com.aliyun.oss.model;

public class ListAccessPointsRequest {
    private static final int MAX_RETURNED_KEYS_LIMIT = 1000;

    // The max objects to return---By default it's 100.
    private Integer maxKeys;
    // The marker filter----objects returned whose key must be greater than the
    // maker in lexicographical order.
    private String continuationToken;

    public ListAccessPointsRequest() {
    }


    public ListAccessPointsRequest(String continuationToken, Integer maxKeys) {
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

    public ListAccessPointsRequest withContinuationToken(String continuationToken) {
        setContinuationToken(continuationToken);
        return this;
    }

    public Integer getMaxKeys() {
        return maxKeys;
    }

    public void setMaxKeys(Integer maxKeys) {
        this.maxKeys = maxKeys;
    }

    public ListAccessPointsRequest withMaxKeys(Integer maxKeys) {
        setMaxKeys(maxKeys);
        return this;
    }
}
