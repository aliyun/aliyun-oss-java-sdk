package com.aliyun.oss.model;

public enum TaggingDirective {
    /**
     * Copy
     */
    Copy("Copy"),

    /**
     * Replace
     */
    Replace("Replace"),

    /**
     * Unknown
     */
    Unknown("Unknown");

    private String taggingDirective;

    private TaggingDirective(String taggingDirective) {
        this.taggingDirective = taggingDirective;
    }

    @Override
    public String toString() {
        return this.taggingDirective;
    }

    public static TaggingDirective parse(String taggingDirective) {
        for (TaggingDirective td : TaggingDirective.values()) {
            if (td.toString().equals(taggingDirective)) {
                return td;
            }
        }

        throw new IllegalArgumentException("Unable to parse " + taggingDirective);
    }
}
