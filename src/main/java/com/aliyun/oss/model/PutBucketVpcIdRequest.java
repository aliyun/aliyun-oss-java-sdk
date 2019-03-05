package com.aliyun.oss.model;

/**
 * Created by jingdan on 2018/12/13.
 */
public class PutBucketVpcIdRequest extends DeleteBucketVpcIdRequest {
	private String vpcTag;

	public String getVpcTag() {
		return vpcTag;
	}

	public void setVpcTag(String vpcTag) {
		this.vpcTag = vpcTag;
	}
}
