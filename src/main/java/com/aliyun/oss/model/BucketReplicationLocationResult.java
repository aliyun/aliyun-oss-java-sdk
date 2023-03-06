package com.aliyun.oss.model;

import java.util.List;

public class BucketReplicationLocationResult extends GenericResult {

    private List<String> locations;

    private List<LocationTransferType> locationTransferTypeConstraint;

    private List<String> locationRTCConstraint;

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<LocationTransferType> getLocationTransferTypeConstraint() {
        return locationTransferTypeConstraint;
    }

    public void setLocationTransferTypeConstraint(List<LocationTransferType> locationTransferTypeConstraint) {
        this.locationTransferTypeConstraint = locationTransferTypeConstraint;
    }

    public List<String> getLocationRTCConstraint() {
        return locationRTCConstraint;
    }

    public void setLocationRTCConstraint(List<String> locationRTCConstraint) {
        this.locationRTCConstraint = locationRTCConstraint;
    }
}
