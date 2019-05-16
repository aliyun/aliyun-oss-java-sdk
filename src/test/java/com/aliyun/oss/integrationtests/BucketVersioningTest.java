package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.aliyun.oss.integrationtests.TestConstants.NO_SUCH_BUCKET_ERR;
import static com.aliyun.oss.integrationtests.TestConstants.NO_SUCH_LIFECYCLE_ERR;
import static com.aliyun.oss.integrationtests.TestUtils.genRandomString;
import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;
import static com.aliyun.oss.model.SetBucketLifecycleRequest.MAX_LIFECYCLE_RULE_LIMIT;
import static com.aliyun.oss.model.SetBucketLifecycleRequest.MAX_RULE_ID_LENGTH;

public class BucketVersioningTest extends TestBase {

    static final String bucketNamePrefix = "bucket-version";
    /**
     * description: test get and set bucket version
     * case:
     *    Action:
     *    1. 创建 bucket, 默认状态"Disabled"
     *    2. 设置 "Enabled状态"，断言 "Enabled"
     *    3. 设置 "Suspended状态",断言 "Suspended"
     *    4. 设置 "其他任意的数值",断言报错 MalformedXML ErrorCode
     */
    @Test
    public void setAndGetBucketVersioningTest() {
        String bucketName = "bucket-version-set-and-get";
        try {
            ossClient.createBucket(bucketName);
            String version = ossClient.getBucketVersioning(bucketName);
            Assert.assertEquals(version,"Disabled");

            // 状态设置为"Enabled"开启状态,验证"Enabled"
            PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
            putBucketVersioningRequest.setBucketVersion("Enabled");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            Assert.assertEquals(ossClient.getBucketVersioning(bucketName), "Enabled");

            // 状态设置为"Suspended" 验证 "Suspended"
            putBucketVersioningRequest.setBucketVersion("Suspended");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            Assert.assertEquals(ossClient.getBucketVersioning(bucketName), "Suspended");

            try {
                putBucketVersioningRequest.setBucketVersion("invalid");
                ossClient.putBucketVersioning(putBucketVersioningRequest);
            } catch(OSSException e) {
                Assert.assertEquals(e.getErrorCode(), "MalformedXML");
            }
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    /***
     * 多版本批量删除接口压力测试
     * 场景：大量创建不同的object,然后为不同的object创建大量历史版本，然后调用批量删除接口完成操作
     */
    @Test
    public void testPressueVersions() {

    }
    /**
     * descripiton: test listObjectVersions （场景主要针对2个object，针对2个生成大量的历史版本，然后通过接口去获取）
     * case
     *     Action:
     *     1 . 创建bucket
     *     2 . 生成2个不同的object
     *     3 . 开启多版本
     *     4 . 针对同名object上传覆盖操作,生成大量的历史版本
     *     5 . 调接口 测试getBucketVersions 查询参数中的多个参数
     *     ....
     *     清空bucket不相互干扰
     *     场景1：创建101个，测试truncated，测试默认值 100 条件过滤测试
     *
     */
    @Test
    public void testGetObjectVersions() {
        String bucketName = "bucket-version-listing";
        String keyObjectName  = "object-version-listing";

        try {
            ossClient.createBucket(bucketName);
            PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
            // case 1
            for (int i = 0 ;i < 90;i++) {
                System.out.println(i);
                putBucketVersioningRequest.setBucketVersion("Enabled");
                ossClient.putBucketVersioning(putBucketVersioningRequest);
                InputStream inputStream = TestUtils.genFixedLengthInputStream(1024);
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyObjectName, inputStream);
                PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
                String versionId = putObjectResult.getVersionId();
                Assert.assertNotNull(versionId);
            }

            ListObjectVersionsRequest listObjectVersionsRequest = new ListObjectVersionsRequest(bucketName);
            ObjectVersionsListing objectVersionsListing = ossClient.listObjectVersions(listObjectVersionsRequest);

            Assert.assertEquals(90,objectVersionsListing.getObjectSummaries().size());
            Assert.assertEquals(false, objectVersionsListing.isTruncated());
            Assert.assertEquals(null, objectVersionsListing.getNextKeyMarker());
            Assert.assertEquals(null,objectVersionsListing.getNextVersionIdMarker());

            for (OSSObjectVersionSummary t : objectVersionsListing.getObjectSummaries()) {
                System.out.println("bucketName: "+ t.getBucketName()+"\n");
                System.out.println("objectKey : "+ t.getKey() +  "\n");
                System.out.println("是否最新版本: "+ t.getIsLatest() + "\n");
                System.out.println("是否删除标记: "+ t.getDeleteMarker() + "\n");
                System.out.println("versionId: "+ t.getVersionId());
            }

            // setMaxKeys 10
            ListObjectVersionsRequest listObjectVersionsRequest1 = new ListObjectVersionsRequest(bucketName);
            listObjectVersionsRequest1.setMaxKeys(10);
            ObjectVersionsListing objectVersionsListing1= ossClient.listObjectVersions(listObjectVersionsRequest1);
            Assert.assertEquals(10,objectVersionsListing1.getObjectSummaries().size());
            Assert.assertEquals(true, objectVersionsListing1.isTruncated());
            Assert.assertNotNull(objectVersionsListing1.getNextKeyMarker());
            Assert.assertNotNull(objectVersionsListing1.getNextVersionIdMarker());

        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Description: buckeInfo接口测试
     * case 1 : 开启多版本后, 获取到bucketInfo信息中的多版本状态"Enabled"
     * case 2 : 暂停多版本后，能够获取到多版本信息中的版本状态:"Suspended"
     * case 3 : 新创建bucket获取默认，获取到的默认bucketinfo信息中应该包括多版本状态："Disabled"
     */
    @Test
    public void testGetBucketInfo() {
        String bucketName = "bucket-version-bucketinfo";
        try {
            ossClient.createBucket(bucketName);
            Assert.assertEquals("Disabled", ossClient.getBucketVersioning(bucketName));
            // case 1
            PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
            putBucketVersioningRequest.setBucketVersion("Enabled");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            Assert.assertEquals("Enabled", ossClient.getBucketInfo(bucketName).getBucket().getBucketVersion());

            putBucketVersioningRequest.setBucketVersion("Suspended");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            Assert.assertEquals("Suspended", ossClient.getBucketInfo(bucketName).getBucket().getBucketVersion());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    /***
     * description: 测试putObject
     * case 1: 测试开启多版本后,putObject接口会返回x-oss-versionid不为空
     * case 2: 测试暂停多版本后,putObject接口会返回NULL
     * case 3: 新建一个bucket,默认不开启多版本,状态为"Disabled",上传一个Object,保障原来的接口没有问题，尤其代码逻辑对x-oss-version-id判空逻辑是否有问题
     */
    @Test
    public void testPutObject() {
        String bucketName     = "bucket-version-put-object";
        String keyObjectName  = "ojbect-version-001";

        try {
            ossClient.createBucket(bucketName);
            PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
            // case 1
            for (int i = 0 ;i < 5;i++) {
                putBucketVersioningRequest.setBucketVersion("Enabled");
                ossClient.putBucketVersioning(putBucketVersioningRequest);
                InputStream inputStream = TestUtils.genFixedLengthInputStream(1024);
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyObjectName, inputStream);
                PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
                String versionId = putObjectResult.getVersionId();
                Assert.assertNotNull(versionId);
            }

            // case 2
            putBucketVersioningRequest.setBucketVersion("Suspended");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            InputStream inputStream = TestUtils.genFixedLengthInputStream(1024);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyObjectName, inputStream);
            PutObjectResult newputObjectResult = ossClient.putObject(putObjectRequest);
            Assert.assertNull(newputObjectResult.getVersionId());

            // case 3
            ossClient.createBucket("bucket-version-pure-bucket");
            PutObjectRequest putObjectRequest1 = new PutObjectRequest("bucket-version-pure-bucket", "xxxxx", inputStream);
            PutObjectResult putObjectResult1 = ossClient.putObject(putObjectRequest1);
            Assert.assertNull(putObjectResult1.getVersionId());
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * description: 测试生命周期
     * case 1 : 设置过期标记的deletermarker, 设置历史版本的转变规则
     * case 2 : 设置历史版本的过期天数，设置历史版本的转变规则
     * case 3 : 更新已经有的生命周期规则
     * case 4 : 设置生命周期的时候，历史版本的过期天数必须小于转变类型中的过期天数，如果大于的话，要抛出错误
     * case 5 : 清空生命周期规则
     *
     * more:
     *
     * 1. 试发现后端历史版本的过期天数可以设置，过期标记可以设置，但是历史版本transitions不能设置，需要同后端同学确认（emrgence）,
     * 经过排查后端文档XML大小写问题,已修复。
     * 2. Only one expiration property should be specified. Expriation中days和过期标记只能设置一个
     * 3. 设置taggging生命周期的时候不能设置过期标记
     * 4. Could not specify a tag-based filter with AbortMultipartUpload or DeleteBucket or ExpiredObjectDeleteMarker in lifecycle rul
     * */
    @Test
    public void testNormalBucketLifyCycle() {
        final String bucketName = "normal-set-bucket-lifecycle";
        final String ruleId0 = "delete temporary files(0)";
        final String matchPrefix0 = "temporary0/";
        final String ruleId1 = "delete temporary files(1)";
        final String matchPrefix1 = "temporary1/";
        final String ruleId2 = "delete temporary files(2)";
        final String matchPrefix2 = "temporary2/";

        try {
            ossClient.createBucket(bucketName);

            SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);

            // case 1
            LifecycleRule rule = new LifecycleRule(ruleId0, matchPrefix0, LifecycleRule.RuleStatus.Enabled);
            List<LifecycleRule.NoncurrentVersionTransition> noncurrentVersionTransitions = new ArrayList<LifecycleRule.NoncurrentVersionTransition>();
            LifecycleRule.NoncurrentVersionTransition noncurrentVersionTransition = new LifecycleRule.NoncurrentVersionTransition();
            noncurrentVersionTransition.setExpirationDays(40);
            noncurrentVersionTransition.setStorageClass(StorageClass.Archive);
            noncurrentVersionTransitions.add(noncurrentVersionTransition);
            rule.setNoncurrentVersionTransitions(noncurrentVersionTransitions);
            rule.setExpiredObjectDeleteMarker(true);

            /* NonCurrentDays in the NoncurrentVersionExpiration action must be later than the NoncurrentVersionTransition
             * action for StorageClass Archive
             */
            rule.setNoncurrentVersionExpirationInDays(50);
            request.AddLifecycleRule(rule);

            // case 2
            rule = new LifecycleRule(ruleId1, matchPrefix1, LifecycleRule.RuleStatus.Enabled);
            List<LifecycleRule.NoncurrentVersionTransition> noncurrentVersionTransitions1 = new ArrayList<LifecycleRule.NoncurrentVersionTransition>();
            LifecycleRule.NoncurrentVersionTransition noncurrentVersionTransition1 = new LifecycleRule.NoncurrentVersionTransition();
            noncurrentVersionTransition1.setExpirationDays(50);
            noncurrentVersionTransition1.setStorageClass(StorageClass.Archive);
            noncurrentVersionTransitions1.add(noncurrentVersionTransition1);

            noncurrentVersionTransition1 = new LifecycleRule.NoncurrentVersionTransition();
            noncurrentVersionTransition1.setStorageClass(StorageClass.IA);
            noncurrentVersionTransition1.setExpirationDays(40);
            noncurrentVersionTransitions1.add(noncurrentVersionTransition1);

            rule.setNoncurrentVersionTransitions(noncurrentVersionTransitions1);
            rule.setNoncurrentVersionExpirationInDays(60);
            rule.setExpirationDays(30);
            request.AddLifecycleRule(rule);


            // case 3
            rule = new LifecycleRule(ruleId2, matchPrefix2, LifecycleRule.RuleStatus.Enabled);
            List<LifecycleRule.NoncurrentVersionTransition> noncurrentVersionTransitions2 = new ArrayList<LifecycleRule.NoncurrentVersionTransition>();
            LifecycleRule.NoncurrentVersionTransition noncurrentVersionTransition2 = new LifecycleRule.NoncurrentVersionTransition();
            noncurrentVersionTransition2.setExpirationDays(40);
            noncurrentVersionTransition2.setStorageClass(StorageClass.Archive);
            noncurrentVersionTransitions2.add(noncurrentVersionTransition2);
            rule.setNoncurrentVersionTransitions(noncurrentVersionTransitions2);
            rule.setNoncurrentVersionExpirationInDays(50);
            rule.setExpirationDays(30);
            request.AddLifecycleRule(rule);

            ossClient.setBucketLifecycle(request);
            List<LifecycleRule> rules = ossClient.getBucketLifecycle(bucketName);

            // case 1 assert
            LifecycleRule r0 = rules.get(0);
            Assert.assertEquals(r0.getId(),ruleId0);
            Assert.assertEquals(r0.getPrefix(), matchPrefix0);
            Assert.assertEquals(r0.getStatus(), LifecycleRule.RuleStatus.Enabled);
            Assert.assertTrue(r0.hasNoncurrentVersionTransitions());
            Assert.assertFalse(r0.hasAbortMultipartUpload());
            Assert.assertTrue(r0.hasExpiredObjectDeleteMarker());
            Assert.assertEquals(r0.getNoncurrentVersionExpirationInDays(), 50);

            // case 2 assert
            LifecycleRule r1 = rules.get(1);
            Assert.assertEquals(r1.getId(),ruleId1);
            Assert.assertEquals(r1.getPrefix(), matchPrefix1);
            Assert.assertEquals(r1.getStatus(), LifecycleRule.RuleStatus.Enabled);
            Assert.assertTrue(r1.hasNoncurrentVersionTransitions());
            Assert.assertFalse(r1.hasAbortMultipartUpload());
            Assert.assertFalse(r1.hasExpiredObjectDeleteMarker());
            Assert.assertEquals(r1.getNoncurrentVersionExpirationInDays(), 60);

            // case 3 assert
            LifecycleRule r2 = rules.get(2);
            Assert.assertEquals(r2.getId(),ruleId2);
            Assert.assertEquals(r2.getPrefix(), matchPrefix2);
            Assert.assertEquals(r2.getStatus(), LifecycleRule.RuleStatus.Enabled);
            Assert.assertTrue(r2.hasNoncurrentVersionTransitions());
            Assert.assertFalse(r2.hasAbortMultipartUpload());
            Assert.assertFalse(r2.hasExpiredObjectDeleteMarker());
            Assert.assertEquals(r2.getNoncurrentVersionExpirationInDays(), 50);
            // case 3
            final String nullRuleId = null;
            request.clearLifecycles();
            request.AddLifecycleRule(new LifecycleRule(nullRuleId, matchPrefix0, LifecycleRule.RuleStatus.Enabled, 7));
            ossClient.setBucketLifecycle(request);

            waitForCacheExpiration(5);

            rules = ossClient.getBucketLifecycle(bucketName);
            Assert.assertEquals(rules.size(), 1);

            r0 = rules.get(0);
            Assert.assertEquals(matchPrefix0, r0.getPrefix());
            Assert.assertEquals(r0.getStatus(), LifecycleRule.RuleStatus.Enabled);
            Assert.assertEquals(r0.getExpirationDays(), 7);

            // case 4
            ossClient.deleteBucketLifecycle(bucketName);
            try {
                ossClient.getBucketLifecycle(bucketName);
            } catch (OSSException e) {
                Assert.assertEquals(OSSErrorCode.NO_SUCH_LIFECYCLE, e.getErrorCode());
                Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_LIFECYCLE_ERR));
            }
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    /**
     * Description: test testUnormalSetBucketLifecycle
     * Case 1 :
     *   设置生命周期的时候，历史版本的过期天数必须小于转变类型中的过期天数，如果大于的话，要抛出错误
     */
    @Test
    public void testUnormalSetBucketLifecycle() throws ParseException {
        final String bucketName = "unormal-set-bucket-lifecycle";
        final String ruleId0 = "delete obsoleted files";
        final String matchPrefix0 = "obsoleted/";

        try {
            ossClient.createBucket(bucketName);

            // Set non-existent bucket
            final String nonexistentBucket = "nonexistent-bucket";
            final LifecycleRule r = new LifecycleRule(ruleId0, matchPrefix0, LifecycleRule.RuleStatus.Enabled, 3);
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(nonexistentBucket);
                request.AddLifecycleRule(r);
                ossClient.setBucketLifecycle(request);

                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (OSSException e) {
                Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
                Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
            }

            // Set bucket without ownership
            final String bucketWithoutOwnership = "oss";
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketWithoutOwnership);
                request.AddLifecycleRule(r);
                ossClient.setBucketLifecycle(request);

                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (OSSException e) {
                Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
            }

            // Set length of rule id exceeding RULE_ID_MAX_LENGTH(255)
            final String ruleId256 = genRandomString(MAX_RULE_ID_LENGTH + 1);
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
                request.AddLifecycleRule(new LifecycleRule(ruleId256, matchPrefix0, LifecycleRule.RuleStatus.Enabled, 3));

                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }

            // Set size of lifecycle rules exceeding LIFECYCLE_RULE_MAX_LIMIT(1000)
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(nonexistentBucket);
                for (int i = 0; i < (MAX_LIFECYCLE_RULE_LIMIT + 1) ; i++) {
                    request.AddLifecycleRule(r);
                }

                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }

            // Set both rule id and prefix null
            final String nullRuleId = null;
            final String nullMatchPrefix = null;
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
                request.AddLifecycleRule(new LifecycleRule(nullRuleId, nullMatchPrefix, LifecycleRule.RuleStatus.Enabled, 3));
                ossClient.setBucketLifecycle(request);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }

            // Set both expiration day and expiration time
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(nonexistentBucket);
                LifecycleRule invalidRule = new LifecycleRule();
                invalidRule.setId(ruleId0);
                invalidRule.setPrefix(matchPrefix0);
                invalidRule.setStatus(LifecycleRule.RuleStatus.Enabled);
                invalidRule.setExpirationTime(DateUtil.parseIso8601Date("2022-10-12T00:00:00.000Z"));
                invalidRule.setExpirationDays(3);
                request.AddLifecycleRule(invalidRule);

                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }

            // Set neither expiration day nor expiration time
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(nonexistentBucket);
                LifecycleRule invalidRule = new LifecycleRule();
                invalidRule.setId(ruleId0);
                invalidRule.setPrefix(matchPrefix0);
                invalidRule.setStatus(LifecycleRule.RuleStatus.Enabled);
                request.AddLifecycleRule(invalidRule);

                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }

            // With abort multipart upload option
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(nonexistentBucket);
                LifecycleRule invalidRule = new LifecycleRule();
                invalidRule.setId(ruleId0);
                invalidRule.setPrefix(matchPrefix0);
                invalidRule.setStatus(LifecycleRule.RuleStatus.Enabled);
                invalidRule.setExpirationDays(3);
                LifecycleRule.AbortMultipartUpload abortMultipartUpload = new LifecycleRule.AbortMultipartUpload();
                abortMultipartUpload.setExpirationDays(3);
                abortMultipartUpload.setCreatedBeforeDate(DateUtil.parseIso8601Date("2022-10-12T00:00:00.000Z"));
                invalidRule.setAbortMultipartUpload(abortMultipartUpload);
                request.AddLifecycleRule(invalidRule);

                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }

            // With storage transition option
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(nonexistentBucket);
                LifecycleRule invalidRule = new LifecycleRule();
                invalidRule.setId(ruleId0);
                invalidRule.setPrefix(matchPrefix0);
                invalidRule.setStatus(LifecycleRule.RuleStatus.Enabled);
                invalidRule.setExpirationDays(3);
                LifecycleRule.StorageTransition storageTransition = new LifecycleRule.StorageTransition();
                storageTransition.setExpirationDays(3);
                storageTransition.setCreatedBeforeDate(DateUtil.parseIso8601Date("2022-10-12T00:00:00.000Z"));
                List<LifecycleRule.StorageTransition> storageTransitions = new ArrayList<LifecycleRule.StorageTransition>();
                storageTransitions.add(storageTransition);
                invalidRule.setStorageTransition(storageTransitions);
                request.AddLifecycleRule(invalidRule);

                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }

        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    /**
     * Description:  test objectTagging
     * case :
     *  Action:
     *     1. 创建bucket
     *     2. putObject
     *     3. 开启多版本
     *     4. putObject --> versionId
     *     5  setObjectTagging ---> getObjectTagging
     *     6. getObjectTagging(versionId) ---> setObjecTagging(versionId) ---> getObjectTagging(versionId);
     *     7. deleteObjectTaggging(object, versionId)
     *     8. 断言
     *     9. 清空all bucket;
     */
    @Test
    public void testObjectTagging() {
        final String bucketName = "bucket-version-tagging";
        final String objectKeyName = "object-version-tagging";
        String versionId;

        try {
            ossClient.createBucket(bucketName);
            InputStream inputStream = TestUtils.genFixedLengthInputStream(1024);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKeyName, inputStream);
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);

            // 3. 开启多版本
            PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
            putBucketVersioningRequest.setBucketVersion("Enabled");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            Assert.assertEquals("Enabled",ossClient.getBucketVersioning(bucketName));

            Tag tag1 = new Tag("tag1", "value1");
            Tag tag2 = new Tag("tag2", "value2");
            Tag tag3 = new Tag("tag3", "value3");

            List<Tag> tagSet = new ArrayList<Tag>();
            tagSet.add(tag1);
            tagSet.add(tag2);
            tagSet.add(tag3);

            ObjectTagging objectTagging = new ObjectTagging(tagSet);
            SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(bucketName, objectKeyName, objectTagging);
            ossClient.setObjectTagging(setObjectTaggingRequest);

            GenericRequest getObjectTaggingRequest = new GenericRequest(bucketName, objectKeyName);
            ObjectTagging objectTagging1 = ossClient.getObjectTagging(getObjectTaggingRequest);

            org.junit.Assert.assertNotNull(objectTagging1);
            org.junit.Assert.assertNotNull(objectTagging1.getTagSet());
            org.junit.Assert.assertTrue(objectTagging1.getTagSet().size() == 3);

            // 创建历史版本
            PutObjectResult putObjectResult1 = ossClient.putObject(putObjectRequest);
            versionId = putObjectResult1.getVersionId();

            Tag tag4 = new Tag("tag4", "value4");
            Tag tag5 = new Tag("tag5", "value5");
            List<Tag> tagSet2 = new ArrayList<Tag>();
            tagSet2.add(tag4);
            tagSet2.add(tag5);

            ObjectTagging objectTaggingX = new ObjectTagging(tagSet2);
            SetObjectTaggingRequest setObjectTaggingRequest1 = new SetObjectTaggingRequest(bucketName, objectKeyName, objectTaggingX, versionId);
            ossClient.setObjectTagging(setObjectTaggingRequest1);

            GenericRequest getObjectTaggingRequestY = new GenericRequest(bucketName, objectKeyName, versionId);
            ObjectTagging objectTaggingY = ossClient.getObjectTagging(getObjectTaggingRequestY);

            org.junit.Assert.assertNotNull(objectTaggingY);
            org.junit.Assert.assertNotNull(objectTaggingY.getTagSet());
            org.junit.Assert.assertTrue(objectTaggingY.getTagSet().size() == 2);

            DeleteObjectTaggingRequest deleteObjectTaggingRequest = new DeleteObjectTaggingRequest(bucketName, objectKeyName);

            ossClient.deleteObjectTagging(deleteObjectTaggingRequest);

            ObjectTagging objectTagging2 = ossClient.getObjectTagging(getObjectTaggingRequest);

            org.junit.Assert.assertNotNull(objectTagging2);
            org.junit.Assert.assertNotNull(objectTagging2.getTagSet());
            org.junit.Assert.assertTrue(objectTagging2.getTagSet().size() == 0);

            DeleteObjectTaggingRequest deleteObjectTaggingRequest1 = new DeleteObjectTaggingRequest(bucketName, objectKeyName, versionId);
            ossClient.deleteObjectTagging(deleteObjectTaggingRequest1);

            GenericRequest getObjectTaggingRequest1 = new GenericRequest(bucketName,objectKeyName, versionId);
            ObjectTagging objectTagging3 = ossClient.getObjectTagging(getObjectTaggingRequest1);
            org.junit.Assert.assertNotNull(objectTagging3);
            org.junit.Assert.assertNotNull(objectTagging3.getTagSet());
            org.junit.Assert.assertTrue(objectTagging3.getTagSet().size() == 0);

        }catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {

        }
    }

    /**
     * description: test deleteObject
     * case:
     *    Action:
     *       1. 创建bucket
     *       2. putObject
     *       3. 开启多版本
     *       4. putObject     -->    versionId
     *       5. deleteObject  ===》 返回x-oss-delete-marker,x-oss-version-id
     *       6. deleteObject(删除标记versionid) ===> 返回x-oss-delete-marker,x-oss-version-id 返回删除标记
     *       7. getObject(versionId)
     *       7. putObject versionid1
     *       8. putObject versionid2
     *       9. deleteObject versionid1
     *       10.getObject(versionId);
     */
    @Test
    public void testDeleteObject() {

        final String bucketName     = "bucket-version-delete";
        final String objectKeyName  = "object-version-delete";

        try {
            ossClient.createBucket(bucketName);
            InputStream inputStream = TestUtils.genFixedLengthInputStream(1024);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKeyName, inputStream);
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);

            // 开启多版本
            PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
            putBucketVersioningRequest.setBucketVersion("Enabled");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            Assert.assertEquals("Enabled",ossClient.getBucketVersioning(bucketName));

            // putObject again
            PutObjectResult putObjectResult1 = ossClient.putObject(putObjectRequest);
            String versionId = putObjectResult1.getVersionId();

            ListObjectVersionsRequest listObjectVersionsRequest = new ListObjectVersionsRequest(bucketName);
            ObjectVersionsListing objectVersionsListing = ossClient.listObjectVersions(listObjectVersionsRequest);

            Assert.assertEquals(2, objectVersionsListing.getObjectSummaries().size());

            // delete with no versionid should size + 1
            ossClient.deleteObject(bucketName, objectKeyName);
            ListObjectVersionsRequest listObjectVersionsRequest1 = new ListObjectVersionsRequest(bucketName);
            ObjectVersionsListing objectVersionsListing1 = ossClient.listObjectVersions(listObjectVersionsRequest1);
            Assert.assertEquals(3, objectVersionsListing1.getObjectSummaries().size());

            // delete with  versionid should size -1
            ossClient.deleteObject(bucketName, objectKeyName, versionId);
            ListObjectVersionsRequest listObjectVersionsRequest2 = new ListObjectVersionsRequest(bucketName);
            ObjectVersionsListing objectVersionsListing2 = ossClient.listObjectVersions(listObjectVersionsRequest2);
            Assert.assertEquals(2, objectVersionsListing2.getObjectSummaries().size());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * description: test getObjectACL and setObjectACl
     * case:
     *  Action: 1. 创建bucket
     *          2. putObject
     *          3. 开启多版本
     *          4. putObject ==> versionId
     *          5. getObjectACL --> setObjectACL----> getObjectACL
     *          6. getObjectACL(versionId)(返回一开始的默认数值) ---> setObjectACL(versionId)-----> getObjectACL(versionId)
     */
    @Test
    public void testObjectACL() {
        final String bucketName = "bucket-version-acl-test04";
        final String objectKeyName = "object-version-acl-test04";

        final CannedAccessControlList[] ACLS = {
            CannedAccessControlList.Default,
            CannedAccessControlList.Private,
            CannedAccessControlList.PublicRead,
            CannedAccessControlList.PublicReadWrite
        };

        try {
            // 1 创建bucket
            ossClient.createBucket(bucketName);

            // 2 putObject
            InputStream inputStream = TestUtils.genFixedLengthInputStream(1024);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKeyName, inputStream);
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);

            // 3 getObjectACL默认：default
            ObjectAcl objectAcl = ossClient.getObjectAcl(bucketName, objectKeyName);
            Assert.assertEquals(ObjectPermission.Default, objectAcl.getPermission());
            ossClient.setObjectAcl(bucketName, objectKeyName, CannedAccessControlList.Private);
            ObjectAcl objectAcl1 = ossClient.getObjectAcl(bucketName,objectKeyName);
            Assert.assertEquals(ObjectPermission.Private, objectAcl1.getPermission());

            // 4 开启多版本
            PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
            putBucketVersioningRequest.setBucketVersion("Enabled");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            Assert.assertEquals("Enabled", ossClient.getBucketVersioning(bucketName));

            // 5 再put一个object
            PutObjectResult putObjectResult1 = ossClient.putObject(putObjectRequest);
            String versionId = putObjectResult1.getVersionId();

            ObjectAcl objectAcl2 = ossClient.getObjectAcl(bucketName, objectKeyName, versionId);
            Assert.assertEquals(ObjectPermission.Default, objectAcl2.getPermission());

            ossClient.setObjectAcl(bucketName, objectKeyName,versionId, CannedAccessControlList.PublicReadWrite);
            ObjectAcl objectAcl3 = ossClient.getObjectAcl(bucketName, objectKeyName, versionId);
            Assert.assertEquals(ObjectPermission.PublicReadWrite, objectAcl3.getPermission());

            ObjectAcl objectAcl4 = ossClient.getObjectAcl(bucketName, objectKeyName, "null");
            Assert.assertEquals(ObjectPermission.Private, objectAcl4.getPermission());

        }catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /***
     * description: test restoreobject（未开启多版本之前不测了，因为接口改动不大）
     * case1 :
     *  Action: 1. 创建archive bucket
     *          2. putobject
     *          3. 开启多版本
     *          4. putObject versionId 3.
     *          5. getObject 不传入versionId  断言：返回无效状态参考restoreObjecttest
     *          6. restoreobject 不传入versiondId
     *          7. getObject 判读下状态
     *          8. getObject 传入versionId，判断返回无效状态
     *          6. restoreobject 传入versionId
     *          7  getObject 传入versionId 获取状态应该是解冻状态
     * 4. restoreobject
     */
    @Test
    public void testRestoreObject() {
        final String bucketName = "bucket-version-restore";
        final String objectKeyName = "object-key-restore";

        try {
            // 1. create bucket
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            createBucketRequest.setStorageClass(StorageClass.Archive);
            ossClient.createBucket(createBucketRequest);

            // 2. putObject
            InputStream inputStream = TestUtils.genFixedLengthInputStream(1024);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKeyName, inputStream);
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);

            try {
                ossClient.getObject(bucketName, objectKeyName);
                Assert.fail("Restore object should not be successful");
            } catch (OSSException e) {
                Assert.assertEquals(OSSErrorCode.INVALID_OBJECT_STATE, e.getErrorCode());
            } finally {
                // 开启多版本
                PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
                putBucketVersioningRequest.setBucketVersion("Enabled");
                ossClient.putBucketVersioning(putBucketVersioningRequest);

                // putObject
                PutObjectResult putObjectResult1 = ossClient.putObject(putObjectRequest);
                String versionId = putObjectResult1.getVersionId();

                // restore 当前版本
                ObjectMetadata objectMetadata = ossClient.getObjectMetadata(bucketName, objectKeyName);
                // check whether the object is archive class
                StorageClass storageClass = objectMetadata.getObjectStorageClass();
                if (storageClass == StorageClass.Archive) {
                    // restore object
                    ossClient.restoreObject(bucketName, objectKeyName);
                    // wait for restore completed
                    do {
                        Thread.sleep(1000);
                        objectMetadata = ossClient.getObjectMetadata(bucketName, objectKeyName);
                        System.out.println("x-oss-restore:" + objectMetadata.getObjectRawRestore());
                    } while (!objectMetadata.isRestoreCompleted());
                }

                // get restored object
                OSSObject ossObject = ossClient.getObject(bucketName, objectKeyName);
                ossObject.getObjectContent().close();

                // restore versionid 版本
                ObjectMetadata objectMetadata1 = ossClient.getObjectMetadata(bucketName, objectKeyName, versionId);
                // check whether the object is archive class
                Assert.assertEquals(objectMetadata1.getObjectStorageClass(), StorageClass.Archive);
                // restore object
                ossClient.restoreObject(bucketName, objectKeyName, versionId);
                // wait for restore completed
                do {
                    Thread.sleep(1000);
                    objectMetadata1 = ossClient.getObjectMetadata(bucketName, objectKeyName, versionId);
                    System.out.println("x-oss-restore:" + objectMetadata1.getObjectRawRestore());
                } while (!objectMetadata1.isRestoreCompleted());
                // get restored object
                OSSObject ossObject1 = ossClient.getObject(bucketName, objectKeyName);
                ossObject1.getObjectContent().close();
            }

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetObjectMeta() {
        String bucketName = "bucket-version-metadata";
        String objectKeyName = "object-version-metadata";
        try {
            ossClient.createBucket(bucketName);

            InputStream inputStream = TestUtils.genFixedLengthInputStream(1024);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKeyName, inputStream);
            ossClient.putObject(putObjectRequest);

            // 开启多版本
            PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
            putBucketVersioningRequest.setBucketVersion("Enabled");
            ossClient.putBucketVersioning(putBucketVersioningRequest);

            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            String versionId = putObjectResult.getVersionId();
            ObjectMetadata objectMetadata = ossClient.getObjectMetadata(bucketName, objectKeyName, versionId);
            Assert.assertNotNull(objectMetadata.getVersionId(), versionId);
        } catch(Exception e){
            Assert.fail(e.getMessage());
        } finally {
            ObjectVersionsListing objectVersionsListing = ossClient.listObjectVersions(bucketName);
            if (objectVersionsListing.getObjectSummaries().size() != 0) {
                for (OSSObjectVersionSummary t: objectVersionsListing.getObjectSummaries()) {
                    System.out.println("versionId: " + t.getVersionId());
                    ossClient.deleteObject(bucketName, objectKeyName, t.getVersionId());
                }
            }
            ossClient.deleteBucket(bucketName);
        }
    }

    /**
     * description versionId 同后端确认
     * todo
     */
    @Test
    public void testBucketVersionBuildUrl() {
    }

    @AfterClass
    public static void afterProcess() {
        // before
        System.out.println("Clean before =============");
        List<Bucket> buckets = getOSSClient().listBuckets();

        for (Bucket b: buckets) {
            System.out.print(b.getName()+"\n");
        }

        cleanUpAllBucketsWithVersion(getOSSClient(),bucketNamePrefix);

        System.out.println("Clean After =============");
        List<Bucket> buckets1 = getOSSClient().listBuckets();

        for (Bucket b: buckets1) {
            System.out.print(b.getName()+"\n");
        }
    }
}