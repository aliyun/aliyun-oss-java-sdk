package com.aliyun.oss.model;

public class PolicyCallbackItem {
    // policy名称
    private String policyName;
    // 回调参数
    private String callback;
    // 自定义回调参数
    private String callbackVar;

    public PolicyCallbackItem(String policyName, String callback) {
        this.policyName = policyName;
        this.callback = callback;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getCallbackVar() {
        return callbackVar;
    }

    public void setCallbackVar(String callbackVar) {
        this.callbackVar = callbackVar;
    }

    public PolicyCallbackItem withCallbackVar(String callbackVar) {
        this.callbackVar = callbackVar;
        return this;
    }
}
