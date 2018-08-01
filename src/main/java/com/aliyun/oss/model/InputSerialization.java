package com.aliyun.oss.model;

import java.io.Serializable;

/**
 * Define input serialization of the select object operations.
 */
public class InputSerialization implements Serializable {
    private CSVFormat csvInputFormat = new CSVFormat();
    private String compressionType = CompressionType.NONE.name();

    public CSVFormat getCsvInputFormat() {
        return csvInputFormat;
    }

    public void setCsvInputFormat(CSVFormat csvInputFormat) {
        this.csvInputFormat = csvInputFormat;
    }

    public InputSerialization withCsvInputFormat(CSVFormat csvFormat) {
        setCsvInputFormat(csvFormat);
        return this;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(CompressionType compressionType) {
        this.compressionType = compressionType.name();
    }

    public InputSerialization withCompressionType(CompressionType compressionType) {
        setCompressionType(compressionType);
        return this;
    }
}
