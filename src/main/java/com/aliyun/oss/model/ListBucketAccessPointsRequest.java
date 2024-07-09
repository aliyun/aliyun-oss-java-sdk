package com.aliyun.oss.model;

public class ListBucketAccessPointsRequest extends GenericRequest {
    private static final int MAX_RETURNED_KEYS_LIMIT = 1000;

    // The max objects to return---By default it's 100.
    private Integer maxKeys;
    // The marker filter----objects returned whose key must be greater than the
    // maker in lexicographical order.
    private String continuationToken;


    public ListBucketAccessPointsRequest(String bucketName) {
        this(bucketName, null, null);
    }

    public ListBucketAccessPointsRequest(String bucketName, String continuationToken, Integer maxKeys) {
        super(bucketName);
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

    public ListBucketAccessPointsRequest withContinuationToken(String continuationToken) {
        setContinuationToken(continuationToken);
        return this;
    }

    public Integer getMaxKeys() {
        return maxKeys;
    }

    public void setMaxKeys(Integer maxKeys) {
        this.maxKeys = maxKeys;
    }

    public ListBucketAccessPointsRequest withMaxKeys(Integer maxKeys) {
        setMaxKeys(maxKeys);
        return this;
    }
}
