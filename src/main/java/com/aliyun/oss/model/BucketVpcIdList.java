package com.aliyun.oss.model;

import java.util.List;

public class BucketVpcIdList extends GenericResult{

	private List<VpcInfo> list;

	public List<VpcInfo> getList() {
		return list;
	}

	public void setList(List<VpcInfo> list) {
		this.list = list;
	}

	public static class VpcInfo {
		private String vpcRegion;
		private String tunnelId;
		private String vpcId;
		private String vpcTag;

		public String getVpcRegion() {
			return vpcRegion;
		}

		public void setVpcRegion(String vpcRegion) {
			this.vpcRegion = vpcRegion;
		}

		public String getTunnelId() {
			return tunnelId;
		}

		public void setTunnelId(String tunnelId) {
			this.tunnelId = tunnelId;
		}

		public String getVpcId() {
			return vpcId;
		}

		public void setVpcId(String vpcId) {
			this.vpcId = vpcId;
		}

		public String getVpcTag() {
			return vpcTag;
		}

		public void setVpcTag(String vpcTag) {
			this.vpcTag = vpcTag;
		}
	}

}
