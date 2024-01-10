package com.aliyun.oss.internal;

import java.io.*;

public class CheckPointObjectInputStream extends ObjectInputStream {

    public CheckPointObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
            ClassNotFoundException {
        if (!desc.getName().equals(OSSDownloadOperation.DownloadCheckPoint.class.getName())) {
            throw new InvalidClassException(
                    "Unauthorized deserialization attempt",
                    desc.getName());
        }
        return super.resolveClass(desc);
    }
}
