package com.aliyun.oss.model;

/**
 * This is the request class that is used to check if the availabilityZoneType supported for the region
 */
public class GetAvailabilityZoneTypeRequest extends WebServiceRequest {
    /**
     * the region to get
     */
    private String location;

    public GetAvailabilityZoneTypeRequest(String location){
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
