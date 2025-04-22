package com.aliyun.oss.model;

public class OpenMetaQueryRequest extends GenericRequest {

    /**
     * Meta query activation mode: scalar search or vector search
     */
    private MetaQueryMode metaQueryMode;

    public OpenMetaQueryRequest(String bucketName) {
        super(bucketName);
    }

    public OpenMetaQueryRequest(String bucketName, MetaQueryMode metaQueryMode) {
        super(bucketName);
        this.metaQueryMode = metaQueryMode;
    }

    public MetaQueryMode getMetaQueryMode() {
        return metaQueryMode;
    }

    public void setMetaQueryMode(MetaQueryMode metaQueryMode) {
        this.metaQueryMode = metaQueryMode;
    }
}
