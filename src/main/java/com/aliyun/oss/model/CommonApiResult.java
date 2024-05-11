package com.aliyun.oss.model;

public class CommonApiResult<T> extends GenericResult {

    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
