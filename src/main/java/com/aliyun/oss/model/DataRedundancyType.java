package com.aliyun.oss.model;

/**
 * the availabilityZoneType for region
 */
public enum DataRedundancyType {

    /**
     * Locally redundant storage
     */
    LRS("LRS"),

    /**
     * Zone-redundant storage
     */
    ZRS("ZRS");

    private String typeString;

    DataRedundancyType(String typeString) {
        this.typeString = typeString;
    }

    @Override
    public String toString() {
        return this.typeString;
    }

    public static DataRedundancyType parse(String typeString) {
        for (DataRedundancyType type : DataRedundancyType.values()) {
            if (type.toString().equals(typeString)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unable to parse " + typeString);
    }
}
