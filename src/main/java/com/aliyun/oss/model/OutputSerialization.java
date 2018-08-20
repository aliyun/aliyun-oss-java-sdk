package com.aliyun.oss.model;

import java.io.Serializable;

/**
 * Define how to output results of the select object operations.
 */
public class OutputSerialization implements Serializable {
    private CSVFormat csvOutputFormat = new CSVFormat();
    private String compressionType = CompressionType.NONE.name();
    private boolean keepAllColumns = false;
    private boolean payloadCrcEnabled = false;
    private boolean outputRawData = false;
    private boolean outputHeader = false;

    public CSVFormat getCsvOutputFormat() {
        return csvOutputFormat;
    }

    public void setCsvOutputFormat(CSVFormat csvOutputFormat) {
        this.csvOutputFormat = csvOutputFormat;
    }

    public OutputSerialization withCsvOutputFormat(CSVFormat csvFormat) {
        setCsvOutputFormat(csvFormat);
        return this;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(CompressionType compressionType) {
        this.compressionType = compressionType.name();
    }

    public OutputSerialization withCompressionType(CompressionType compressionType) {
        setCompressionType(compressionType);
        return this;
    }

    public boolean isKeepAllColumns() {
        return keepAllColumns;
    }

    public void setKeepAllColumns(boolean keepAllColumns) {
        this.keepAllColumns = keepAllColumns;
    }

    public OutputSerialization withKeepAllColumns(boolean keepAllColumns) {
        setKeepAllColumns(keepAllColumns);
        return this;
    }

    public boolean isPayloadCrcEnabled() {
        return payloadCrcEnabled;
    }

    public void setPayloadCrcEnabled(boolean payloadCrcEnabled) {
        this.payloadCrcEnabled = payloadCrcEnabled;
    }

    public OutputSerialization withCrcEnabled(boolean crcEnabled) {
        setPayloadCrcEnabled(crcEnabled);
        return this;
    }

    public boolean isOutputRawData() {
        return outputRawData;
    }

    public void setOutputRawData(boolean outputRawData) {
        this.outputRawData = outputRawData;
    }

    public OutputSerialization withOutputRawData(boolean outputRawData) {
        setOutputRawData(outputRawData);
        return this;
    }

    public boolean isOutputHeader() {
        return outputHeader;
    }

    public void setOutputHeader(boolean outputHeader) {
        this.outputHeader = outputHeader;
    }

    public OutputSerialization withOutputHeader(boolean outputHeader) {
        setOutputHeader(outputHeader);
        return this;
    }
}
