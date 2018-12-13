package com.aliyun.oss.model;

/**
 * Created by jingdan on 2018/12/13.
 */
public class SetBucketEncryptionRequest extends GenericRequest {
	/**
	 * 加密类型
	 */
	private SSEAlgorithm algorithm;

	/**
	 * KMS 才会有的密钥id
	 */
	private String kMSMasterKeyID;

	public SSEAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(SSEAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public String getkMSMasterKeyID() {
		return kMSMasterKeyID;
	}

	public void setkMSMasterKeyID(String kMSMasterKeyID) {
		this.kMSMasterKeyID = kMSMasterKeyID;
	}
}
