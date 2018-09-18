package com.aliyun.oss.model;

import java.util.Date;

public class WormConfiguration extends GenericResult {
    
    private String wormId;

    private WormState state;

    private Date creationDate;

    private Date expirationDate;

    private int retentionPeriodInDays;
    

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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getRetentionPeriodInDays() {
        return retentionPeriodInDays;
    }

    public void setRetentionPeriodInDays(int retentionPeriodInDays) {
        this.retentionPeriodInDays = retentionPeriodInDays;
    }


    public enum WormState {
        IN_PROGRESS("InProgress"),

        LOCKED("Locked"),

        EXPIRED("Expired");

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
