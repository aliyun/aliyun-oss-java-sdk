package com.aliyun.oss.internal;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class CheckPointObjectInputStream extends ObjectInputStream {

    static final List<String> names = Arrays.asList(new String[]{
            OSSDownloadOperation.DownloadCheckPoint.class.getName(),
            OSSDownloadOperation.DownloadPart.class.getName(),
            OSSDownloadOperation.ObjectStat.class.getName(),
            OSSUploadOperation.UploadCheckPoint.class.getName(),
            OSSUploadOperation.UploadPart.class.getName(),
            OSSUploadOperation.FileStat.class.getName(),
            com.aliyun.oss.model.PartETag.class.getName(),
            java.util.ArrayList.class.getName(),
            java.util.Date.class.getName()
    });

    public CheckPointObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
            ClassNotFoundException {
        if (!names.contains(desc.getName())) {
            throw new InvalidClassException(
                    "Unauthorized deserialization attempt",
                    desc.getName());
        }
        return super.resolveClass(desc);
    }
}
