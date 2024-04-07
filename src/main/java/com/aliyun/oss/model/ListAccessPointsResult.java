package com.aliyun.oss.model;

import java.util.List;

public class ListAccessPointsResult extends GenericResult {
    private Boolean isTruncated;
    private String nextContinuationToken;
    private String accountId;
    private List<AccessPoint> accessPoints;

    public Boolean getTruncated() {
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<AccessPoint> getAccessPoints() {
        return accessPoints;
    }

    public void setAccessPoints(List<AccessPoint> accessPoints) {
        this.accessPoints = accessPoints;
    }
}
