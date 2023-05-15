package com.aliyun.oss.model;

import java.io.Serializable;

public class BucketChannelConfig implements Serializable {
    //rule name
    private String ruleName;
    //Regular expression encoded with urlSafeBase64
    private String ruleRegex;
    // Save front-end data, reserved
    private String frontContent;
    // Convert GMT time to a string
    private String createTime;
    // Convert GMT time to a string
    private String lastModifiedTime;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleRegex() {
        return ruleRegex;
    }

    public void setRuleRegex(String ruleRegex) {
        this.ruleRegex = ruleRegex;
    }

    public String getFrontContent() {
        return frontContent;
    }

    public void setFrontContent(String frontContent) {
        this.frontContent = frontContent;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}