package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_OBJECT_CONTENT_TYPE;

public class CopyObjectsTest extends TestBase {
    
    @Test
    public void testBatchCopyExistingObjects() {
        String sourceBucket = "copy-existing-object-source-bucket";
        String sourceKey = "copy-existing-object-source-object";
        String sourceKey2 = "copy-existing-object-source-object2";
        String targetKey = "copy-existing-object-target-object";
        String targetKey2 = "copy-existing-object-target-object2";
        String userMetaKey0 = "user";
        String userMetaValue0 = "aliy";

        try {
            ossClient.createBucket(sourceBucket);

            byte[] content = { 'A', 'l', 'i', 'y', 'u', 'n' };
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(content.length);
            metadata.setContentType(DEFAULT_OBJECT_CONTENT_TYPE);
            metadata.addUserMetadata(userMetaKey0, userMetaValue0);

            PutObjectResult putObjectResult = ossClient.putObject(sourceBucket, sourceKey,
                    new ByteArrayInputStream(content), metadata);
            PutObjectResult putObjectResult2 = ossClient.putObject(sourceBucket, sourceKey2,
                    new ByteArrayInputStream(content), metadata);

            CopyObjectsRequest copyObjectsRequest = new CopyObjectsRequest();
            List<CopyObjects> objects = new ArrayList<CopyObjects>();
            CopyObjects obj1 = new CopyObjects().withSourceKey(sourceKey).withTargetKey(targetKey);
            objects.add(obj1);
            CopyObjects obj2 = new CopyObjects().withSourceKey(sourceKey2).withTargetKey(targetKey2);
            objects.add(obj2);
            copyObjectsRequest.setObjects(objects);
            copyObjectsRequest.setBucketName(sourceBucket);
            CopyObjectsResult copyObjectsResult = ossClient.copyObjects(copyObjectsRequest);

            String sourceETag = putObjectResult.getETag();
            String targetETag = copyObjectsResult.getSuccessObjects().get(0).getETag();
            Assert.assertEquals(sourceETag, targetETag);
            Assert.assertEquals(putObjectResult.getRequestId().length(), REQUEST_ID_LEN);
            String sourceETag2 = putObjectResult2.getETag();
            String targetETag2 = copyObjectsResult.getSuccessObjects().get(1).getETag();
            Assert.assertEquals(sourceETag2, targetETag2);

            OSSObject ossObject = ossClient.getObject(sourceBucket, targetKey);
            ObjectMetadata newObjectMetadata = ossObject.getObjectMetadata();
            Assert.assertEquals(DEFAULT_OBJECT_CONTENT_TYPE, newObjectMetadata.getContentType());
            Assert.assertEquals(userMetaValue0, newObjectMetadata.getUserMetadata().get(userMetaKey0));
            Assert.assertEquals(ossObject.getRequestId().length(), REQUEST_ID_LEN);

            OSSObject ossObject2 = ossClient.getObject(sourceBucket, targetKey2);
            ObjectMetadata newObjectMetadata2 = ossObject2.getObjectMetadata();
            Assert.assertEquals(DEFAULT_OBJECT_CONTENT_TYPE, newObjectMetadata2.getContentType());
            Assert.assertEquals(userMetaValue0, newObjectMetadata2.getUserMetadata().get(userMetaKey0));
            Assert.assertEquals(ossObject2.getRequestId().length(), REQUEST_ID_LEN);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            waitForCacheExpiration(5);
            deleteBucketWithObjects(ossClient, sourceBucket);
        }
    }

    @Test
    public void testBatchCopyNonexistentObject() {
        String existingSourceBucket = "copy-nonexistent-object-existing-source-bucket";
        String existingSourceKey = "copy-nonexistent-object-existing-source-object";
        String nonexistentSourceKey = "copy-nonexistent-object-nonexistent-source-object";
        String targetKey = "copy-nonexistent-object-target";

        try {
            ossClient.createBucket(existingSourceBucket);

            // Try to copy object under non-existent source bucket
            CopyObjectsRequest copyObjectsRequest = new CopyObjectsRequest();
            List<CopyObjects> objects = new ArrayList<CopyObjects>();
            CopyObjects obj1 = new CopyObjects().withSourceKey(nonexistentSourceKey).withTargetKey(targetKey);
            objects.add(obj1);
            copyObjectsRequest.setObjects(objects);
            copyObjectsRequest.setBucketName(existingSourceBucket);
            CopyObjectsResult copyObjectsResult = ossClient.copyObjects(copyObjectsRequest);

            String resultSourceKey = copyObjectsResult.getFailedObjects().get(0).getSourceKey();
            String resultTargetKey = copyObjectsResult.getFailedObjects().get(0).getTargetKey();
            String errorStatus = copyObjectsResult.getFailedObjects().get(0).getErrorStatus();
            Assert.assertEquals(nonexistentSourceKey, resultSourceKey);
            Assert.assertEquals(targetKey, resultTargetKey);
            Assert.assertEquals("NoSuchKey", errorStatus);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            deleteBucketWithObjects(ossClient, existingSourceBucket);
        }
    }

    @Test
    public void testBatchCopyObjectWithSpecialChars() {
        String sourceBucket = "copy-existing-object-source-bucket";
        String sourceKey = "测\\r试-中.~,+\"'*&￥#@%！（文）+字符|？/.zip";
        String targetKey = "测\\r试-中.~,+\"'*&￥#@%！（文）+字符|？/-2.zip";
        String userMetaKey0 = "user";
        String userMetaValue0 = "阿里人";

        try {
            ossClient.createBucket(sourceBucket);

            byte[] content = { 'A', 'l', 'i', 'y', 'u', 'n' };
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(content.length);
            metadata.setContentType(DEFAULT_OBJECT_CONTENT_TYPE);
            metadata.addUserMetadata(userMetaKey0, userMetaValue0);

            PutObjectResult putObjectResult = ossClient.putObject(sourceBucket, sourceKey,
                    new ByteArrayInputStream(content), metadata);
            CopyObjectsRequest copyObjectsRequest = new CopyObjectsRequest();
            List<CopyObjects> objects = new ArrayList<CopyObjects>();
            CopyObjects obj1 = new CopyObjects().withSourceKey(sourceKey).withTargetKey(targetKey);
            objects.add(obj1);
            copyObjectsRequest.setObjects(objects);
            copyObjectsRequest.setBucketName(sourceBucket);
            CopyObjectsResult copyObjectsResult = ossClient.copyObjects(copyObjectsRequest);
            String sourceETag = putObjectResult.getETag();
            String targetETag = copyObjectsResult.getSuccessObjects().get(0).getETag();
            Assert.assertEquals(sourceETag, targetETag);

            OSSObject ossObject = ossClient.getObject(sourceBucket, targetKey);
            ObjectMetadata newObjectMetadata = ossObject.getObjectMetadata();
            Assert.assertEquals(DEFAULT_OBJECT_CONTENT_TYPE, newObjectMetadata.getContentType());
            Assert.assertEquals(userMetaValue0, newObjectMetadata.getUserMetadata().get(userMetaKey0));

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            deleteBucketWithObjects(ossClient, sourceBucket);
        }
    }
}
