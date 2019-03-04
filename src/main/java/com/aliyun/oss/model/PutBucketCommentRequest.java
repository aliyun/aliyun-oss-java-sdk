package com.aliyun.oss.model;

public class PutBucketCommentRequest extends GenericRequest{
  private String comment;

  public PutBucketCommentRequest(String bucketName, String comment) {
    super(bucketName);
    this.comment = comment;
  }

  public PutBucketCommentRequest(String bucketName) {
    this(bucketName, null);
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
