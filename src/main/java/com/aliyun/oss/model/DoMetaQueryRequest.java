package com.aliyun.oss.model;

import java.util.List;

public class DoMetaQueryRequest extends GenericRequest {
    private String nextToken;
    private int maxResults;
    private String query;
    private String sort;
    private SortOrder order;
    private Aggregations aggregations;
    private String encodingType;
    private MetaQueryMode metaQueryMode;
    private List<String> mediaTypes;
    private String simpleQuery;

    public DoMetaQueryRequest(String bucketName, int maxResults) {
        super(bucketName);
        this.maxResults = maxResults;
    }

    public DoMetaQueryRequest(String bucketName, int maxResults, String query, String sort) {
        super(bucketName);
        this.maxResults = maxResults;
        this.query = query;
        this.sort = sort;
    }

    public DoMetaQueryRequest(String bucketName, int maxResults, String query, String sort, MetaQueryMode metaQueryMode,
                              List<String> mediaTypes, String simpleQuery) {
        this(bucketName, maxResults, query, sort);
        this.metaQueryMode = metaQueryMode;
        this.mediaTypes = mediaTypes;
        this.simpleQuery = simpleQuery;
    }

    public String getNextToken() {
        return nextToken;
    }

    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public Aggregations getAggregations() {
        return aggregations;
    }

    public void setAggregations(Aggregations aggregations) {
        this.aggregations = aggregations;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public MetaQueryMode getMetaQueryMode() {
        return metaQueryMode;
    }

    public void setMetaQueryMode(MetaQueryMode metaQueryMode) {
        this.metaQueryMode = metaQueryMode;
    }

    public List<String> getMediaTypes() {
        return mediaTypes;
    }

    public void setMediaTypes(List<String> mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    public String getSimpleQuery() {
        return simpleQuery;
    }

    public void setSimpleQuery(String simpleQuery) {
        this.simpleQuery = simpleQuery;
    }
}