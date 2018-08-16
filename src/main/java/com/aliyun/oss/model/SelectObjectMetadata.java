package com.aliyun.oss.model;

/**
 * Metadata for select object requests.
 * For example, {@link CsvObjectMetadata} contains total lines so that
 * users can do line-range query for select requests
 */
public class SelectObjectMetadata extends ObjectMetadata {

    private CsvObjectMetadata csvObjectMetadata;

    public SelectObjectMetadata() {}

    public SelectObjectMetadata(ObjectMetadata objectMetadata) {
        setUserMetadata(objectMetadata.getUserMetadata());
        metadata.putAll(objectMetadata.getRawMetadata());
    }

    public CsvObjectMetadata getCsvObjectMetadata() {
        return csvObjectMetadata;
    }

    public void setCsvObjectMetadata(CsvObjectMetadata csvObjectMetadata) {
        this.csvObjectMetadata = csvObjectMetadata;
    }

    public SelectObjectMetadata withCsvObjectMetadata(CsvObjectMetadata csvObjectMetadata) {
        setCsvObjectMetadata(csvObjectMetadata);
        return this;
    }

    public static class CsvObjectMetadata {
        private int totalLines;
        private int splits;

        public int getTotalLines() {
            return totalLines;
        }

        public void setTotalLines(int totalLines) {
            this.totalLines = totalLines;
        }

        public CsvObjectMetadata withTotalLines(int totalLines) {
            setTotalLines(totalLines);
            return this;
        }

        public int getSplits() {
            return splits;
        }

        public void setSplits(int splits) {
            this.splits = splits;
        }

        public CsvObjectMetadata withSplits(int splits) {
            setSplits(splits);
            return this;
        }
    }
}
