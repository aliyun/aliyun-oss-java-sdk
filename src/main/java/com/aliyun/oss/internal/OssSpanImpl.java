package com.aliyun.oss.internal;

import com.aliyun.core.tracing.AlibabaCloudSpan;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.StatusCode;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OssSpanImpl implements AlibabaCloudSpan {
    private final io.opentelemetry.api.trace.Span span;

    OssSpanImpl(io.opentelemetry.api.trace.Span span) {
        this.span = span;
    }

    @Override
    public void end() {
        span.end();
    }

    @Override
    public void end(long l, TimeUnit timeUnit) {

    }

    @Override
    public SpanContext getSpanContext() {
        return span.getSpanContext();
    }

    @Override
    public boolean isRecording() {
        return false;
    }

    public void addEvent(String eventName, Map<String, Object> attributes) {

    }

    @Override
    public <T> AlibabaCloudSpan setAttribute(AttributeKey<T> attributeKey, T t) {
        return this;
    }

    @Override
    public Span addEvent(String s, Attributes attributes) {
        return this;
    }

    @Override
    public Span addEvent(String s, Attributes attributes, long l, TimeUnit timeUnit) {
        return this;
    }

    @Override
    public Span setStatus(StatusCode statusCode, String s) {
        return this;
    }

    public AlibabaCloudSpan recordException(Throwable throwable){
        return this;
    }

    @Override
    public Span recordException(Throwable throwable, Attributes attributes) {
        return this;
    }

    @Override
    public Span updateName(String s) {
        return this;
    }

}