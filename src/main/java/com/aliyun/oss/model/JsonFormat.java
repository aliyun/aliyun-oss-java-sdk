package com.aliyun.oss.model;

import java.io.Serializable;

public class JsonFormat implements Serializable {
    private JsonType jsonType;

    //Define the delimiter for `output` json records
    private String recordDelimiter = "\n";

    public JsonType getJsonType() {
        return jsonType;
    }

    public void setJsonType(JsonType jsonType) {
        this.jsonType = jsonType;
    }

    public JsonFormat withJsonType(JsonType jsonType) {
        setJsonType(jsonType);
        return this;
    }

    public String getRecordDelimiter() {
        return recordDelimiter;
    }

    public void setRecordDelimiter(String recordDelimiter) {
        this.recordDelimiter = recordDelimiter;
    }
}
