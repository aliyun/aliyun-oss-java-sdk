package com.aliyun.oss.model;

public class PutBucketRequestPaymentRequest extends GenericRequest {

    private RequestPayer payer;

    public PutBucketRequestPaymentRequest(String bucketName) {
        super(bucketName);
    }

    public RequestPayer getPayer() {
        return payer;
    }

    public void setPayer(RequestPayer payer) {
        this.payer = payer;
    }
  }
