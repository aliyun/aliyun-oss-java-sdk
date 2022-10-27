package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

public class LifecycleFilter {
    private Long objectSizeGreaterThan;
    private Long objectSizeLessThan;
    private List<LifecycleNot> notList = new ArrayList<LifecycleNot>();

    public Long getObjectSizeGreaterThan() {
        return objectSizeGreaterThan;
    }

    public void setObjectSizeGreaterThan(Long objectSizeGreaterThan) {
        this.objectSizeGreaterThan = objectSizeGreaterThan;
    }

    public Long getObjectSizeLessThan() {
        return objectSizeLessThan;
    }

    public void setObjectSizeLessThan(Long objectSizeLessThan) {
        this.objectSizeLessThan = objectSizeLessThan;
    }

    public List<LifecycleNot> getNotList() {
        return notList;
    }

    public void setNotList(List<LifecycleNot> notList) {
        this.notList = notList;
    }
}