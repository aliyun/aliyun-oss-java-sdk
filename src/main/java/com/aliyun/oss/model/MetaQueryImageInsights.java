package com.aliyun.oss.model;

/**
 * Description information for image files.
 */
public class MetaQueryImageInsights {
    private String caption;
    private String description;

    /**
     * Gets the brief description information.
     * @return brief description information
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the brief description information.
     * @param caption brief description information
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Gets the detailed description information.
     * @return detailed description information
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the detailed description information.
     * @param description detailed description information
     */
    public void setDescription(String description) {
        this.description = description;
    }
}