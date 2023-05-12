package com.aliyun.oss.model;

public enum RtcStatus {
    /**
     * Start RTC service.
     */
    Enabled("enabled"),
    /**
     * Turn off RTC service.
     */
    Disabled("disabled"),
    /**
     * Unknown
     */
    Unknown("Unknown");


    private String statusString;
    private RtcStatus(String rtcStatusString) {
        this.statusString = rtcStatusString;
    }
    @Override
    public String toString() {
        return this.statusString;
    }
    public static RtcStatus parse(String rtcStatus) {
        for (RtcStatus status : RtcStatus.values()) {
            if (status.toString().equals(rtcStatus)) {
                return status;
            }
        }
        return Unknown;
    }
}