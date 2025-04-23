package com.aliyun.oss.model;

public class MetaQuerySubtitle {
    private String codecName;
    private String language;
    private double startTime;
    private double duration;

    public MetaQuerySubtitle() {}

    public MetaQuerySubtitle(String codecName, String language, double startTime, double duration) {
        this.codecName = codecName;
        this.language = language;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getCodecName() {
        return codecName;
    }

    public void setCodecName(String codecName) {
        this.codecName = codecName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
