package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static com.aliyun.oss.integrationtests.TestConfig.OSS_TEST_REGION;

public class BucketCommentTest extends TestBase {

  @Test
  public void normalSetBucketCommentTest() {
    final String testBucketName = "test-comment-bucket";
    final String testComment = "this is a test comment";
    try {
      // create a test bucket
      CreateBucketRequest createBucketRequest = new CreateBucketRequest(testBucketName);
      createBucketRequest.setLocationConstraint(OSS_TEST_REGION);
      createBucketRequest.setStorageClass(StorageClass.Standard);
      ossClient.createBucket(createBucketRequest);

      SetBucketCommentRequest setBucketCommentRequest = new SetBucketCommentRequest(testBucketName);
      setBucketCommentRequest.setComment(testComment);

      ossClient.putBucketComment(setBucketCommentRequest);

      BucketList bucketList = ossClient.listBuckets(testBucketName, "", 100);

      List<Bucket> buckets = bucketList.getBucketList();

      Assert.assertEquals(1, buckets.size());
      Assert.assertEquals(testComment, buckets.get(0).getComment());

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    } finally {
      ossClient.deleteBucket(testBucketName);
    }
  }

  @Test
  public void updateBucketComment() {
    final String bucketName = "test-comment-bucket";
    final String oriComment = "this is a test comment";
    final String newComment = "this is a new comment";

    try {
      CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
      createBucketRequest.setLocationConstraint(OSS_TEST_REGION);
      createBucketRequest.setStorageClass(StorageClass.Standard);
      createBucketRequest.setComment(oriComment);
      ossClient.createBucket(createBucketRequest);

      BucketList bucketList = ossClient.listBuckets(bucketName, "", 100);
      List<Bucket> buckets = bucketList.getBucketList();
      Assert.assertEquals(1, buckets.size());
      Assert.assertEquals(oriComment, buckets.get(0).getComment());

      SetBucketCommentRequest setBucketCommentRequest = new SetBucketCommentRequest(bucketName);
      setBucketCommentRequest.setComment(newComment);

      ossClient.putBucketComment(setBucketCommentRequest);
      BucketList newBucketList = ossClient.listBuckets(bucketName, "", 100);

      List<Bucket> newBuckets = newBucketList.getBucketList();

      Assert.assertEquals(1, newBuckets.size());
      Assert.assertEquals(newComment, newBuckets.get(0).getComment());
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    } finally {
      ossClient.deleteBucket(bucketName);
    }
  }
}
