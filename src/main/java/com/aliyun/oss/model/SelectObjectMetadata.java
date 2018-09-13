package com.aliyun.oss.model;

/**
 * Metadata for select object requests.
 * For example, {@link CSVObjectMetadata} contains total lines so that
 * users can do line-range query for select requests
 */
public class SelectObjectMetadata extends ObjectMetadata {

    private CSVObjectMetadata csvObjectMetadata;

    public SelectObjectMetadata() {}

    public SelectObjectMetadata(ObjectMetadata objectMetadata) {
        setUserMetadata(objectMetadata.getUserMetadata());
        metadata.putAll(objectMetadata.getRawMetadata());
    }

    public CSVObjectMetadata getCsvObjectMetadata() {
        return csvObjectMetadata;
    }

    public void setCsvObjectMetadata(CSVObjectMetadata csvObjectMetadata) {
        this.csvObjectMetadata = csvObjectMetadata;
    }

    public SelectObjectMetadata withCsvObjectMetadata(CSVObjectMetadata csvObjectMetadata) {
        setCsvObjectMetadata(csvObjectMetadata);
        return this;
    }

    public static class CSVObjectMetadata {
        private long totalLines;
        private int splits;

        public long getTotalLines() {
            return totalLines;
        }

        public void setTotalLines(long totalLines) {
            this.totalLines = totalLines;
        }

        public CSVObjectMetadata withTotalLines(long totalLines) {
            setTotalLines(totalLines);
            return this;
        }

        public int getSplits() {
            return splits;
        }

        public void setSplits(int splits) {
            this.splits = splits;
        }

        public CSVObjectMetadata withSplits(int splits) {
            setSplits(splits);
            return this;
        }
    }
}
