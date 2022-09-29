package com.aliyun.oss.model;

import java.util.List;

public class CopyObjectsRequest extends GenericRequest {
    private List<CopyObjects> objects;

    public List<CopyObjects> getObjects() {
        return objects;
    }

    public void setObjects(List<CopyObjects> objects) {
        this.objects = objects;
    }
}
