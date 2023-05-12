package com.aliyun.oss.model;

import java.io.Serializable;

public class BucketChannelConfig implements Serializable {
    //规则名
    private String ruleName;
    //经过urlSafeBase64编码的正则表达式
    private String ruleRegex;
    // 保存前端的数据，reserved
    private String frontContent;
    // GMT 时间转化为字符串
    private String createTime;
    // GMT 时间转化为字符串
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
