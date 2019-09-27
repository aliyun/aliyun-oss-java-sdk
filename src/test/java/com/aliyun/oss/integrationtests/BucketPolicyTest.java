package com.aliyun.oss.integrationtests;

import java.util.Date;
import java.util.Random;

import org.junit.Test;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetBucketPolicyResult;
import com.aliyun.oss.model.SetBucketPolicyRequest;

import static com.aliyun.oss.integrationtests.TestConstants.NO_SUCH_BUCKET_ERR;

import junit.framework.Assert;

public class BucketPolicyTest extends TestBase {

    @Test
    public void testNormalPolicy() {
        try {
            String policyText = "{\"Version\":\"1\",\"Statement\":[{\"Action\":[\"oss:GetObject\",\"oss:PutObject\"],\"Effect\":\"Deny\",\"Principal\":\"[123456790]\",\"Resource\":\"[\\\"acs:oss:*:1234567890:*\\/*\\\"]\"}]}";

            // Set normal policy
            ossClient.setBucketPolicy(bucketName, policyText);

            // Get policy
            GetBucketPolicyResult result = ossClient.getBucketPolicy(bucketName);

            // Verfiy policy text
            Assert.assertEquals(policyText, result.getPolicyText());

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            // Delete policy after test
            ossClient.deleteBucketPolicy(bucketName);
        }
    }

    @Test
    public void testUnnormalSetPolicy() {
        long ticks = new Date().getTime() / 1000 + new Random().nextInt(5000);
        String notExsiteBucketName = BUCKET_NAME_PREFIX + ticks;
        String normalPolicyText = "{\"Version\":\"1\",\"Statement\":[{\"Action\":[\"oss:GetObject\",\"oss:PutObject\"],\"Effect\":\"Deny\",\"Principal\":\"[123456790]\",\"Resource\":\"[\\\"acs:oss:*:1234567890:*\\/*\\\"]\"}]}";

        // Set non-existent bucket
        try {
            SetBucketPolicyRequest setPolicyReq = new SetBucketPolicyRequest(notExsiteBucketName);
            setPolicyReq.setPolicyText(normalPolicyText);
            ossClient.setBucketPolicy(setPolicyReq);
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
        }

        // Set bucket without ownership
        final String bucketWithoutOwnership = "oss";//AccessDenied
        try {
            SetBucketPolicyRequest setPolicyReq = new SetBucketPolicyRequest(bucketWithoutOwnership, normalPolicyText);
            ossClient.setBucketPolicy(setPolicyReq);
            Assert.fail("Set bucket policy should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
        }

        // Set bucket with unnormal plicy text
        final String unnormalPolicyText = "{unnormal-policy-text}";
        try {
            SetBucketPolicyRequest setPolicyReq = new SetBucketPolicyRequest(bucketName, unnormalPolicyText);
            ossClient.setBucketPolicy(setPolicyReq);
            Assert.fail("Set bucket policy should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.INVALID_POLICY_DOCUMENT, e.getErrorCode());
        }
    }

    @Test
    public void testUnnormalGetPolicy() {
        long ticks = new Date().getTime() / 1000 + new Random().nextInt(5000);
        String notExsiteBucketName = BUCKET_NAME_PREFIX + ticks;

        // Get non-existent bucket
        try {
            GetBucketPolicyResult result = ossClient.getBucketPolicy(notExsiteBucketName);
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
        }

        // Get non-exsitent policy
        String newBucketName = notExsiteBucketName;
        try {
            ossClient.createBucket(newBucketName);
            GetBucketPolicyResult result = ossClient.getBucketPolicy(newBucketName);
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET_POLICY, e.getErrorCode());
        } finally {
            ossClient.deleteBucket(newBucketName);
        }

        // Get bucket without ownership
        final String bucketWithoutOwnership = "oss";//AccessDenied
        try {
            GetBucketPolicyResult result = ossClient.getBucketPolicy(bucketWithoutOwnership);
            Assert.fail("Get bucket policy should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
        }
    }

    @Test
    public void testUnnormalDeletePolicy() {
        long ticks = new Date().getTime() / 1000 + new Random().nextInt(5000);
        String notExsiteBucketName = BUCKET_NAME_PREFIX + ticks;

        // Delete non-existent bucket
        try {
            ossClient.deleteBucketPolicy(notExsiteBucketName);
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
        }

        // Delete non-exsitent policy
        final String newBucketName = notExsiteBucketName;
        try {
            ossClient.createBucket(newBucketName);
            ossClient.deleteBucketPolicy(newBucketName);
        } catch (Exception e) {
            Assert.fail("deleteBucketPolicy err" + e.getMessage());
        } finally {
            ossClient.deleteBucket(newBucketName);
        }

        // Delete bucket without ownership
        final String bucketWithoutOwnership = "oss";//AccessDenied
        try {
            ossClient.deleteBucketPolicy(bucketWithoutOwnership);
            Assert.fail("Delete bucket policy should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
        }
    }
}
