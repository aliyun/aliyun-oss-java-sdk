package com.aliyun.oss.model;

public class MetaQueryAudioStream {
    private String codecName;
    private long bitrate;
    private long sampleRate;
    private double startTime;
    private double duration;
    private long channels;
    private String language;

    public MetaQueryAudioStream() {
    }

    public MetaQueryAudioStream(String codecName, long bitrate, long sampleRate, double startTime, double duration, long channels, String language) {
        this.codecName = codecName;
        this.bitrate = bitrate;
        this.sampleRate = sampleRate;
        this.startTime = startTime;
        this.duration = duration;
        this.channels = channels;
        this.language = language;
    }

    public String getCodecName() {
        return codecName;
    }

    public void setCodecName(String codecName) {
        this.codecName = codecName;
    }

    public long getBitrate() {
        return bitrate;
    }

    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }

    public long getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(long sampleRate) {
        this.sampleRate = sampleRate;
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

    public long getChannels() {
        return channels;
    }

    public void setChannels(long channels) {
        this.channels = channels;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
