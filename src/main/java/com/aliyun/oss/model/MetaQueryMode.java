package com.aliyun.oss.model;

public enum MetaQueryMode {
    /**
     * default value, Scalar retrieval mode
     */
    BASIC("basic"),

    /**
     * Vector retrieval mode
     */
    SEMANTIC("semantic");

    private final String metaQueryModeString;

    private MetaQueryMode(String metaQueryModeString) {
        this.metaQueryModeString = metaQueryModeString;
    }

    @Override
    public String toString() {
        return metaQueryModeString;
    }

    /**
     * Returns the MetaQueryMode enum corresponding to the given string
     *
     * @param metaQueryModeString meta query mode
     * @return matched meta query mode
     *
     * @throws IllegalArgumentException if the specified metaQueryModeString is not supported
     */
    public static MetaQueryMode parse(String metaQueryModeString) {
        for (MetaQueryMode metaQueryMode : MetaQueryMode.values()) {
            if (metaQueryMode.toString().equals(metaQueryModeString)) {
                return metaQueryMode;
            }
        }

        throw new IllegalArgumentException("Unable to parse the provided meta query mode " + metaQueryModeString);
    }
}
