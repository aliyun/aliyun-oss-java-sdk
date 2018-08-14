package com.aliyun.oss.model;

import java.util.Date;

public class WormConfiguration extends GenericResult {

    private String bucketName;

    private String wormId;

    private WormState state;

    private Date createDate;

    private Date lockedDate;

    private int retensionPeriodInDays;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getWormId() {
        return wormId;
    }

    public void setWormId(String wormId) {
        this.wormId = wormId;
    }

    public WormState getState() {
        return state;
    }

    public void setState(WormState state) {
        this.state = state;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(Date lockedDate) {
        this.lockedDate = lockedDate;
    }

    public int getRetensionPeriodInDays() {
        return retensionPeriodInDays;
    }

    public void setRetensionPeriodInDays(int retensionPeriodInDays) {
        this.retensionPeriodInDays = retensionPeriodInDays;
    }


    public enum WormState {
        IN_PROGRESS("InProgress"),

        LOCKED("Locked");

        private String name;

        WormState(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static WormState parse(String stateString) {
            for (WormState state : WormState.values()) {
                if (state.toString().equals(stateString)) {
                    return state;
                }
            }
            throw new IllegalArgumentException("Unable to parse " + stateString);
        }
    }
}
