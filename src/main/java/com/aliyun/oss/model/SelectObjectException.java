package com.aliyun.oss.model;

import java.io.IOException;

public class SelectObjectException extends IOException {
    public static final String INVALID_INPUT_STREAM = "InvalidInputStream";
    public static final String INVALID_CRC = "InvalidCRC";
    public static final String INVALID_SELECT_VERSION = "InvalidSelectVersion";
    public static final String INVALID_SELECT_FRAME = "InvalidSelectFrame";

    private int status;
    private String errorCode;

    public SelectObjectException(int status, String errorCode, String message) {
        super("status: " + status + ", message: " + message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
