package com.aliyun.oss.internal;

import com.aliyun.core.tracing.AlibabaCloudSpan;
import com.aliyun.core.tracing.AlibabaCloudSpanBuilder;
import com.aliyun.core.tracing.AlibabaCloudTracer;

public class OssTracerImpl implements AlibabaCloudTracer {
    private final io.opentelemetry.api.trace.Tracer tracer;

    public OssTracerImpl(io.opentelemetry.api.trace.Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public AlibabaCloudSpanBuilder spanBuilder(String var1) {
        tracer.spanBuilder(var1);
        return null;
    }

    @Override
    public AlibabaCloudSpan startSpan(String spanName) {
        io.opentelemetry.api.trace.Span openTelemetrySpan = tracer.spanBuilder(spanName).startSpan();
        return new OssSpanImpl(openTelemetrySpan);
    }
}