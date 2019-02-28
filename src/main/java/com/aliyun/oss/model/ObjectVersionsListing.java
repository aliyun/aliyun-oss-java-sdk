package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectVersionsListing extends GenericResult {

    /**
     * A list of summary information describing the objects stored in the bucket
     */
    private List<OSSObjectVersionSummary> objectSummaries = new ArrayList<OSSObjectVersionSummary>();

    private List<String> commonPrefixes = new ArrayList<String>();

    private String bucketName;

    private String nextMarker;

    private boolean isTruncated;

    private String prefix;

    /***
     * listObjects请求参数中的marker参数
     */
    private String marker;

    /**
     *  new KeyMarker
     */
    private String keyMarker;


    /**
     * new VersionIdMarker
     */

    private String versionIdMarker;

    /**
     *  mew NextKeyMarker
     */

    private String nextKeyMarker;

    /**
     * new NextVersionIdMarker
     */
    private String nextVersionIdMarker;

    private int maxKeys;

    private String delimiter;

    private String encodingType;

    public List<OSSObjectVersionSummary> getObjectSummaries() {
        return objectSummaries;
    }

    public void addObjectSummary(OSSObjectVersionSummary objectSummary) {
        this.objectSummaries.add(objectSummary);
    }

    public void setObjectSummaries(List<OSSObjectVersionSummary> objectSummaries) {
        this.objectSummaries.clear();
        if (objectSummaries != null && !objectSummaries.isEmpty()) {
            this.objectSummaries.addAll(objectSummaries);
        }
    }

    public void clearObjectSummaries() {
        this.objectSummaries.clear();
    }

    public List<String> getCommonPrefixes() {
        return commonPrefixes;
    }

    public void addCommonPrefix(String commonPrefix) {
        this.commonPrefixes.add(commonPrefix);
    }

    public void setCommonPrefixes(List<String> commonPrefixes) {
        this.commonPrefixes.clear();
        if (commonPrefixes != null && !commonPrefixes.isEmpty()) {
            this.commonPrefixes.addAll(commonPrefixes);
        }
    }

    public void clearCommonPrefixes() {
        this.commonPrefixes.clear();
    }

    public String getNextMarker() {
        return nextMarker;
    }

    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getMaxKeys() {
        return maxKeys;
    }

    public void setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public boolean isTruncated() {
        return isTruncated;
    }

    public void setTruncated(boolean isTruncated) {
        this.isTruncated = isTruncated;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
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

    public String getNextKeyMarker(){
        return nextKeyMarker;
    }

    public void setNextKeyMarker(String nextKeyMarker){
        this.nextKeyMarker = nextKeyMarker;
    }

    public String getNextVersionIdMarker() {
        return nextVersionIdMarker;
    }

    public void setNextVersionIdMarker(String nextVersionIdMarker) {
        this.nextVersionIdMarker = nextVersionIdMarker;
    }
}
