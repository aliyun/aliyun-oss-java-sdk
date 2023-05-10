package com.aliyun.oss.model;


public class AsyncProcessObjectRequest extends GenericRequest {

    public AsyncProcessObjectRequest(String bucketName, String key, String process) {
        super(bucketName, key);
        this.process = process;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public AsyncProcessObjectRequest withProcess(String process) {
        this.process = process;
        return this;
    }

    private String process;
}
