package com.aliyun.oss.model;

public class DeleteBucketTaggingRequest extends GenericRequest {
  private String tags;
  
  public DeleteBucketTaggingRequest(String bucketName, String tags) {
    super(bucketName);
    this.tags = tags;
  }
  
  public DeleteBucketTaggingRequest(String bucketName) {
    super(bucketName);
  }
  
  public String getTags() {
    return tags;
  }
  
  public void setTags(String tags) {
    this.tags = tags;
  }
}
