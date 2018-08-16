package com.aliyun.oss.model;

import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_CSV_META;

public class CreateSelectObjectMetadataRequest extends HeadObjectRequest {
    private String process;
    private InputSerialization inputSerialization = new InputSerialization();
    private boolean overwrite;

    public CreateSelectObjectMetadataRequest(String bucketName, String key) {
        super(bucketName, key);
        setProcess(SUBRESOURCE_CSV_META);
        setOverwrite(false);
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public CreateSelectObjectMetadataRequest withProcess(String process) {
        setProcess(process);
        return this;
    }

    public InputSerialization getInputSerialization() {
        return inputSerialization;
    }

    public void setInputSerialization(InputSerialization inputSerialization) {
        this.inputSerialization = inputSerialization;
    }

    public CreateSelectObjectMetadataRequest withInputSerialization(InputSerialization inputSerialization) {
        setInputSerialization(inputSerialization);
        return this;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public CreateSelectObjectMetadataRequest withOverwrite(boolean overwrite) {
        setOverwrite(overwrite);
        return this;
    }
}
