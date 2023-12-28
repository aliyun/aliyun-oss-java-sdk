package com.aliyun.oss.common.auth;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.comm.RequestMessage;

public interface RequestPresigner {
    public void presign(RequestMessage request) throws ClientException;
}
