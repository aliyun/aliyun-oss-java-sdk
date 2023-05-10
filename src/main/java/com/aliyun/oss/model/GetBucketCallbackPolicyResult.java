package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

public class GetBucketCallbackPolicyResult extends GenericResult {

    private List<PolicyCallbackItem> policyCallbackItems = new ArrayList<PolicyCallbackItem>();

    public List<PolicyCallbackItem> getPolicyCallbackItems() {
        return policyCallbackItems;
    }

    public void setPolicyCallbackItems(List<PolicyCallbackItem> policyCallbackItems) {
        this.policyCallbackItems = policyCallbackItems;
    }
}
