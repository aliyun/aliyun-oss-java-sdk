package com.aliyun.oss.model;

public class MetaQueryVideoStream {
    private String codecName;
    private String language;
    private long bitrate;
    private String frameRate;
    private double startTime;
    private double duration;
    private long frameCount;
    private long bitDepth;
    private String pixelFormat;
    private String colorSpace;
    private long height;
    private long width;


    public MetaQueryVideoStream() {}

    public MetaQueryVideoStream(String codecName, String language, long bitrate, String frameRate, double startTime, double duration, long frameCount, long bitDepth, String pixelFormat, String colorSpace, long height, long width) {
        this.codecName = codecName;
        this.language = language;
        this.bitrate = bitrate;
        this.frameRate = frameRate;
        this.startTime = startTime;
        this.duration = duration;
        this.frameCount = frameCount;
        this.bitDepth = bitDepth;
        this.pixelFormat = pixelFormat;
        this.colorSpace = colorSpace;
        this.height = height;
        this.width = width;
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

    public long getBitrate() {
        return bitrate;
    }

    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }

    public String getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(String frameRate) {
        this.frameRate = frameRate;
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

    public long getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(long frameCount) {
        this.frameCount = frameCount;
    }

    public long getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(long bitDepth) {
        this.bitDepth = bitDepth;
    }

    public String getPixelFormat() {
        return pixelFormat;
    }

    public void setPixelFormat(String pixelFormat) {
        this.pixelFormat = pixelFormat;
    }

    public String getColorSpace() {
        return colorSpace;
    }

    public void setColorSpace(String colorSpace) {
        this.colorSpace = colorSpace;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }
}
