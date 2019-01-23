package com.aliyun.oss.model;

public class SetBucketCommentRequest extends GenericRequest{
  private String comment;

  public SetBucketCommentRequest(String bucketName, String comment) {
    super(bucketName);
    this.comment = comment;
  }

  public SetBucketCommentRequest(String bucketName) {
    this(bucketName, null);
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
