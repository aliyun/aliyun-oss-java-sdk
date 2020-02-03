package com.aliyun.oss.model;

import com.aliyun.oss.common.utils.StringUtils;

import java.io.Serializable;

/**
 * Represents a customer provided key for use with OSS server-side encryption.
 */
public class SSECustomerKey implements Serializable {
    private final String base64EncodedKey;
    private String base64EncodedMd5;
    private String algorithm;

    public SSECustomerKey(String base64EncodedKey) {
        if (StringUtils.isNullOrEmpty(base64EncodedKey)) {
            throw new IllegalArgumentException("Encryption key must be specified");
        }
        this.base64EncodedKey = base64EncodedKey;
        this.algorithm = SSEAlgorithm.AES256.getAlgorithm();
    }

    public SSECustomerKey(String base64EncodedKey, String algorithm) {
        this(base64EncodedKey);
        setAlgorithm(algorithm);
    }

    public String getBase64EncodedKey() {
        return base64EncodedKey;
    }

    public String getBase64EncodedMd5() {
        return base64EncodedMd5;
    }

    /**
     * Sets the optional MD5 digest (base64-encoded) of the encryption key to use when
     * encrypting the object. This will be used as a message integrity check
     * that the key was transmitted without error. If not set, the SDK will fill
     * in this value by calculating the MD5 digest of the secret key, before
     * sending the request.
     *
     * @param base64EncodedMd5
     *            The MD5 digest (base64-encoded) of the encryption key to use
     *            when encrypting the object.
     */
    public void setBase64EncodedMd5(String base64EncodedMd5) {
        this.base64EncodedMd5 = base64EncodedMd5;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the encryption algorithm to use with this customer-provided
     * server-side encryption key.
     *
     * Currently, "AES256" is the only supported algorithm.
     *
     * @see SSEAlgorithm#AES256
     *
     * @param algorithm
     *            The server-side encryption algorithm to use with this
     *            customer-provided server-side encryption key.
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
