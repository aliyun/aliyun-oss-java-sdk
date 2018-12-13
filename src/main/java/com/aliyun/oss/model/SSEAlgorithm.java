package com.aliyun.oss.model;

/**
 * Created by jingdan on 2018/12/13.
 */
public enum SSEAlgorithm {

	Default("None"),

	AES256("AES256"),

	KMS("KMS");

	private String algorithmString;

	private SSEAlgorithm(String algorithmString) {
		this.algorithmString = algorithmString;
	}

	@Override
	public String toString() {
		return this.algorithmString;
	}

	public static SSEAlgorithm parse(String algorithm) {
		for (SSEAlgorithm algorithmItem : SSEAlgorithm.values()) {
			if (algorithmItem.toString().equals(algorithm)) {
				return algorithmItem;
			}
		}

		throw new IllegalArgumentException("Unable to parse the provided acl " + algorithm);
	}
}
