package com.aliyun.oss.model;

import com.aliyun.oss.internal.OSSUtils;

/**
 * This is the request class to list objects versions under a bucket.
 */
public class ListObjectVersionsRequest extends GenericRequest {

    private static final int MAX_RETURNED_KEYS_LIMIT = 1000;

    // The prefix filter----objects returned whose key must start with this
    // prefix.
    private String prefix;

    // The marker filter----objects returned whose key must be greater than the
    // maker in lexicographical order.
    private String marker;


    private String keyMarker;

    private String versionIdMarker;

    // The max objects to return---By default it's 100.
    private Integer maxKeys;

    // The delimiters of object names returned.
    private String delimiter;

    /**
     * The encoding type of object name in the response body. Currently object
     * name allow any unicode character. However the XML 1.0 could not parse
     * some Unicode character such as ASCII character 0 to 10. For these XMl 1.0
     * non-supported characters, we can use the encoding type to encode the
     * object name.
     */
    private String encodingType;

    public ListObjectVersionsRequest() {
    }

    public ListObjectVersionsRequest(String bucketName) {
        this(bucketName, null, null,null, null, null);
    }

    /**
     * Constructor
     *
     * @param bucketName
     *            Bucket name.
     * @param prefix
     *            The prefix filter---Objects to return must start with this
     *            prefix in their names.
     * @param keyMarker
     *            The marker filter---Objects to return whose names must be
     *            greater than this marker value.
     *
     * @param versionIdMarker
     *
     * @param maxKeys
     *            The max object counts to return. The default is 100.
     * @param delimiter
     *            The delimiter for the object names to return.
     */
    public ListObjectVersionsRequest(String bucketName, String prefix, String keyMarker, String versionIdMarker, String delimiter, Integer maxKeys) {
        super(bucketName);
        setPrefix(prefix);
        setMarker(keyMarker);
        setVersionIdMarker(versionIdMarker);
        setDelimiter(delimiter);
        if (maxKeys != null) {
            setMaxKeys(maxKeys);
        }
    }

    /**
     * Gets the prefix filter.
     *
     * @return The prefix filter.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix filter.
     *
     * @param prefix
     *            The prefix filter.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Sets teh prefix filter and return the current ListObjectsRequest instance
     * (this).
     *
     * @param prefix
     *            The prefix filter.
     */
    public ListObjectVersionsRequest withPrefix(String prefix) {
        setPrefix(prefix);
        return this;
    }

    /**
     * Gets the marker filter.
     *
     * @return The marker filter.
     */
    public String getMarker() {
        return marker;
    }

    /**
     * Sets the marker filter.
     *
     * @param marker
     *            The marker filter.
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * Sets the mark filter and returns the current ListObjectsRequest instance
     * (this).
     *
     * @param marker
     *            marker
     */
    public ListObjectVersionsRequest withMarker(String marker) {
        setMarker(marker);
        return this;
    }

    /**
     * Gets the max objects to return. By default it's 100.
     *
     * @return The max objects to return.
     */
    public Integer getMaxKeys() {
        return maxKeys;
    }

    /**
     * Sets the max objects to return. By default it's 100, the max is 1000.
     *
     * @param maxKeys
     *            The max objects to return. The max value is 1000.
     */
    public void setMaxKeys(Integer maxKeys) {
        if (maxKeys < 0 || maxKeys > MAX_RETURNED_KEYS_LIMIT) {
            throw new IllegalArgumentException(OSSUtils.OSS_RESOURCE_MANAGER.getString("MaxKeysOutOfRange"));
        }

        this.maxKeys = maxKeys;
    }

    /**
     * Sets the max objects and returns the current ListObjectsRequest instance
     * (this). By default it's 100, the max is 1000.
     *
     * @param maxKeys
     *            The max objects to return.
     */
    public ListObjectVersionsRequest withMaxKeys(Integer maxKeys) {
        setMaxKeys(maxKeys);
        return this;
    }

    /**
     * Gets the delimiter of object names.
     *
     * @return the delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the delimiter.
     *
     * @param delimiter
     *            the delimiter to set
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Sets the delimiter and returns the current ListObjectsRequest instance
     * (this).
     *
     * @param delimiter
     *            the delimiter to set
     */
    public ListObjectVersionsRequest withDelimiter(String delimiter) {
        setDelimiter(delimiter);
        return this;
    }

    /**
     * Gets the encoding type of object names in response body.
     *
     * @return The encoding type of object names in response body.
     */
    public String getEncodingType() {
        return encodingType;
    }

    /**
     * Sets the encoding type of object names in response body.
     *
     * @param encodingType
     *            The encoding type of object names in response body. Valid
     *            values are 'null' or 'url'.
     */
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * Sets the encoding type of object names in response body and returns the
     * current ListObjectsRequest instance (this).
     *
     * @param encodingType
     *            The encoding type of object names in response body. Valid
     *            values are 'null' or 'url'.
     */
    public ListObjectVersionsRequest withEncodingType(String encodingType) {
        setEncodingType(encodingType);
        return this;
    }

    public String getKeyMarker() {
        return keyMarker;
    }

    public void setKeyMarker(String keyMarker) {
        this.keyMarker = keyMarker;
    }

    public String getVersionIdMarker() {
        return versionIdMarker;
    }

    public void setVersionIdMarker(String versionIdMarker) {
        this.versionIdMarker = versionIdMarker;
    }
}
