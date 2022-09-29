package com.aliyun.oss.model;

import java.util.List;

public class CopyObjectsResult extends GenericResult {
    /**
     * Successfully copied objects collection.
     */
    private  List<CopyObjectsSuccessResult> successObjects;
    /**
     * Copy failed objects collection.
     */
    private  List<CopyObjectsFailedResult> failedObjects;

    public List<CopyObjectsSuccessResult> getSuccessObjects() {
        return successObjects;
    }

    public void setSuccessObjects(List<CopyObjectsSuccessResult> successObjects) {
        this.successObjects = successObjects;
    }

    public List<CopyObjectsFailedResult> getFailedObjects() {
        return failedObjects;
    }

    public void setFailedObjects(List<CopyObjectsFailedResult> failedObjects) {
        this.failedObjects = failedObjects;
    }
}
