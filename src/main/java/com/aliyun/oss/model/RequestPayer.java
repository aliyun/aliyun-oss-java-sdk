package com.aliyun.oss.model;

/**
 * the Payer of RequestPayment
 */
public enum RequestPayer {
    /**
     * the owner of the bucket
     */
    BucketOwner("BucketOwner"),


    /**
     * the requester to th bucket
     */
    Requester("Requester");

    private String mPayer;

    RequestPayer(String payer) {
        this.mPayer = payer;
    }

    @Override
    public String toString() {
        return this.mPayer;
    }

    public static RequestPayer parse(String payerString) {
        for (RequestPayer payer : RequestPayer.values()) {
            if (payer.toString().equals(payerString)) {
                return payer;
            }
        }
        throw new IllegalArgumentException("Unable to parse " + payerString);
    }

}
