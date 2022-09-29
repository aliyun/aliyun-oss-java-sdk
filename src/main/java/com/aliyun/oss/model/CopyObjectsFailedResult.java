package com.aliyun.oss.model;

public class CopyObjectsFailedResult extends CopyObjects {
    // Error state when copying objects.
    private String errorStatus;

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }
}
