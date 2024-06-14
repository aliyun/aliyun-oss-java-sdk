package com.aliyun.oss.internal;

import com.aliyun.core.tracing.AlibabaCloudTracer;
import com.aliyun.core.tracing.TracerProvider;

public class OssTracerProvider implements TracerProvider {
    public OssTracerProvider() {
    }

    public AlibabaCloudTracer getTracer() {
        return new OssTracerImpl(TracerProvider.getDefaultProvider().getTracer());
    }
}