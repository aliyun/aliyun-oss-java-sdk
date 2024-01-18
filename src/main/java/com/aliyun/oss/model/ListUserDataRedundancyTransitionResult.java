package com.aliyun.oss.model;

import java.util.List;

public class ListUserDataRedundancyTransitionResult extends GenericResult {
    private Boolean isTruncated;
    private String nextContinuationToken;
    private List<GetBucketDataRedundancyTransitionResult> bucketDataRedundancyTransition;

    public Boolean isTruncated() {
        return isTruncated;
    }

    public void setTruncated(Boolean truncated) {
        isTruncated = truncated;
    }

    public String getNextContinuationToken() {
        return nextContinuationToken;
    }

    public void setNextContinuationToken(String nextContinuationToken) {
        this.nextContinuationToken = nextContinuationToken;
    }

    public Boolean getTruncated() {
        return isTruncated;
    }

    public List<GetBucketDataRedundancyTransitionResult> getBucketDataRedundancyTransition() {
        return bucketDataRedundancyTransition;
    }

    public void setBucketDataRedundancyTransition(List<GetBucketDataRedundancyTransitionResult> bucketDataRedundancyTransition) {
        this.bucketDataRedundancyTransition = bucketDataRedundancyTransition;
    }
}
