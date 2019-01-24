package com.aliyun.oss.model;

/**
 * Created by jingdan on 2018/12/13.
 */
public enum SSEAlgorithm {

	AES256("AES256"),

	KMS("KMS"),

	NONE("None");

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

		throw new IllegalArgumentException("Unable to parse the provided encryption " + algorithm);
	}
}
