package com.aliyun.oss.model;

/**
 * Bucket AccessMonitor Configuration
 */
public class PutBucketAccessMonitorRequest extends GenericRequest {

    private AccessMonitorStatus status;
    public PutBucketAccessMonitorRequest(String bucketName, AccessMonitorStatus status) {
        super();
        setBucketName(bucketName);
        setStatus(status);
    }

    public PutBucketAccessMonitorRequest() {
    }

    public AccessMonitorStatus getStatus() {
        return status;
    }

    public void setStatus(AccessMonitorStatus status) {
        this.status = status;
    }
}