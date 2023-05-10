package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

public class SetBucketCallbackPolicyRequest extends GenericRequest {

    private List<PolicyCallbackItem> policyCallbackItems = new ArrayList<PolicyCallbackItem>();

    public SetBucketCallbackPolicyRequest(String bucketName) {
        super(bucketName);
    }

    public SetBucketCallbackPolicyRequest(String bucketName, List<PolicyCallbackItem> policyCallbackItems) {
        super(bucketName);
        this.policyCallbackItems = policyCallbackItems;
    }

    public List<PolicyCallbackItem> getPolicyCallbackItems() {
        return policyCallbackItems;
    }

    public void setPolicyCallbackItems(List<PolicyCallbackItem> policyCallbackItems) {
        this.policyCallbackItems = policyCallbackItems;
    }

    public SetBucketCallbackPolicyRequest withPolicyCallbackItems(List<PolicyCallbackItem> policyCallbackItems) {
        this.policyCallbackItems = policyCallbackItems;
        return this;
    }
}
