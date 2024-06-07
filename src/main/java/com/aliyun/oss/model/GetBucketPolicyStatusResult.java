package com.aliyun.oss.model;

public class GetBucketPolicyStatusResult extends GenericResult {
    private boolean isPublic = false;

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
