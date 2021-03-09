package com.aliyun.oss.model;

public class CertificateConfiguration {
    /**
     * Certificate public key, PEM format
     */
    private String publicKey;

    /**
     * Certificate private key, PEM format
     */
    private String privateKey;

    /**
     * Certificate ID in CAS
     */
    private String id;

    /**
     * If enabled, OSS will not check the previous certificate ID.
     */
    private boolean forceOverwriteCert;

    /**
     * Previous certificate ID.
     */
    private String previousId;

    /**
     * Gets the certificate public key
     */
    public String getPublicKey() { return publicKey; }

    /**
     * Sets the certificate public key.
     */
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    /**
     * Sets the certificate public key and return the updated CertificateConfiguration object.
     */
    public CertificateConfiguration withPublicKey(String publicKey) {
        setPublicKey(publicKey);
        return this;
    }

    /**
     * Gets the certificate private key.
     */
    public String getPrivateKey() { return privateKey; }

    /**
     * Sets the certificate private key.
     */
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }

    /**
     * Sets the certificate private key and return the updated CertificateConfiguration object.
     */
    public CertificateConfiguration withPrivateKey(String privateKey) {
        setPrivateKey(privateKey);
        return this;
    }

    /**
     * Gets the certificate ID.
     */
    public String getId() { return id; }

    /**
     * Sets the certificate ID.
     */
    public void setId(String id) { this.id = id; }

    /**
     * Sets the certificate ID and return the updated CertificateConfiguration object.
     */
    public CertificateConfiguration withId(String id) {
        setId(id);
        return this;
    }

    /**
     * Get if force overwrite certificate or not.
     */
    public boolean isForceOverwriteCert() { return forceOverwriteCert; }

    /**
     * Set if force overwrite certificate or not.
     * @param forceOverwriteCert If enabled, OSS will not check the previous certificate ID.
     */
    public void setForceOverwriteCert(boolean forceOverwriteCert) { this.forceOverwriteCert = forceOverwriteCert; }

    /**
     * Set if force overwrite certificate or not and return the updated CertificateConfiguration object.
     * @param forceOverwriteCert If enabled, OSS will not check the previous certificate ID.
     */
    public CertificateConfiguration withForceOverwriteCert(boolean forceOverwriteCert) {
        setForceOverwriteCert(forceOverwriteCert);
        return this;
    }

    /**
     * Gets the previous certificate ID.
     */
    public String getPreviousId() { return previousId; }

    /**
     * Sets the previous certificate ID.
     */
    public void setPreviousId(String previousId) { this.previousId = previousId; }

    /**
     * Sets the previous certificate ID and return the updated CertificateConfiguration object.
     */
    public CertificateConfiguration withPreviousId(String previousId) {
        setPreviousId(previousId);
        return this;
    }
}
