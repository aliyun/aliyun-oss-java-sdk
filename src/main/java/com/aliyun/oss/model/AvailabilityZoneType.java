package com.aliyun.oss.model;

/**
 * the availabilityZoneType for region
 */
public enum AvailabilityZoneType {

    /**
     * 1 Cluster
     */
    Normal("Normal"),

    /**
     * 2 Cluster
     */
    Two_AZ("2AZ"),

    /**
     * 3 Cluster
     */
    Three_AZ("3AZ");

    private String typeString;

    AvailabilityZoneType(String typeString) {
        this.typeString = typeString;
    }

    @Override
    public String toString() {
        return this.typeString;
    }

    public static AvailabilityZoneType parse(String typeString) {
        for (AvailabilityZoneType type : AvailabilityZoneType.values()) {
            if (type.toString().equals(typeString)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unable to parse " + typeString);
    }
}
