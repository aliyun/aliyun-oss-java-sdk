package com.aliyun.oss.internal.signer;

import com.aliyun.oss.common.auth.Credentials;

import java.util.Date;
import java.util.Set;

public class OSSSignerParams {
    /* Note that resource path should not have been url-encoded. */
    private String resourcePath;

    private Credentials credentials;

    private String product;

    private String region;

    private long tickOffset;

    private String cloudBoxId;

    private Date expiration;

    private Set<String> additionalHeaderNames;

    public OSSSignerParams(String resourcePath, Credentials creds) {
        this.resourcePath = resourcePath;
        this.credentials = creds;
        this.tickOffset = 0;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials creds) {
        this.credentials = creds;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getTickOffset() {
        return tickOffset;
    }

    public void setTickOffset(long tickOffset) {
        this.tickOffset = tickOffset;
    }

    public String getCloudBoxId() {
        return cloudBoxId;
    }

    public void setCloudBoxId(String cloudBoxId) {
        this.cloudBoxId = cloudBoxId;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Set<String> getAdditionalHeaderNames() {
        return additionalHeaderNames;
    }

    public void setAdditionalHeaderNames(Set<String> additionalHeaderNames) {
        this.additionalHeaderNames = additionalHeaderNames;
    }
}
