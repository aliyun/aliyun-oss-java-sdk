package com.aliyun.oss.model;

/**
 * Created by jingdan on 2018/12/13.
 * EncryptionRule 规则
 */
public class ServerSideEncryptionRule extends GenericResult{
	/**
	 * Kms Key Id
	 */
	private String kMSMasterKeyID;

	/**
	 * Encryption Type
	 */
	private SSEAlgorithm algorithm;

	public String getkMSMasterKeyID() {
		return kMSMasterKeyID;
	}

	public void setkMSMasterKeyID(String kMSMasterKeyID) {
		this.kMSMasterKeyID = kMSMasterKeyID;
	}

	public SSEAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(SSEAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
}
