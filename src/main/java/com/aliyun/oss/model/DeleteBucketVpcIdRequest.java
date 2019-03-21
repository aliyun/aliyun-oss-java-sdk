package com.aliyun.oss.model;

/**
 * Created by jingdan on 2018/12/13.
 */
public class DeleteBucketVpcIdRequest extends GenericRequest {
	private String vpcRegion;
	private String vpcId;

	public String getVpcRegion() {
		return vpcRegion;
	}

	public void setVpcRegion(String vpcRegion) {
		this.vpcRegion = vpcRegion;
	}

	public String getVpcId() {
		return vpcId;
	}

	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}
}
