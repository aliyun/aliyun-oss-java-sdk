package com.aliyun.oss.model;

/**
 * Container for descriptions of video and image files.
 */
public class MetaQueryRespFileInsights {
    private MetaQueryVideoInsights video;
    private MetaQueryImageInsights image;

    /**
     * Gets the video description information.
     * @return video description information
     */
    public MetaQueryVideoInsights getVideo() {
        return video;
    }

    /**
     * Sets the video description information.
     * @param video video description information
     */
    public void setVideo(MetaQueryVideoInsights video) {
        this.video = video;
    }

    /**
     * Gets the image description information.
     * @return image description information
     */
    public MetaQueryImageInsights getImage() {
        return image;
    }

    /**
     * Sets the image description information.
     * @param image image description information
     */
    public void setImage(MetaQueryImageInsights image) {
        this.image = image;
    }
}