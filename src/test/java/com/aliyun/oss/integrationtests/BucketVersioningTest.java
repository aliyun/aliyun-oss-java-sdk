package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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

    static final String bucketName = "bucket-with-versioning-test";
    static final String pure_bucketName = "pure-bucket-with-test";
    private static final String TEST_OBJECT_KEY = "test-bucket-versioning";
    private static final String TEST_PUT_OBJECT_KEY = "test-put-object-key";

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Test Starting...............");
        Boolean isBucketExist = ossClient.doesBucketExist(bucketName);
        if (!isBucketExist) {
            ossClient.createBucket(bucketName);
        }
    }
    /*
     * 一开始创建 bucket后获取bucket的多版本状态应该是"Disabled"
     */
    @Test
    public void setAndGetBucketVersioningTest() {
        String version = ossClient.getBucketVersioning(bucketName);
        Assert.assertEquals(version,"Disabled");

        PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
        putBucketVersioningRequest.setBucketVersion("Suspended");
        ossClient.putBucketVersioning(putBucketVersioningRequest);

        // 状态设置为"Suspended" 验证 Suspended;
        String bucketVersion = ossClient.getBucketVersioning(bucketName);
        Assert.assertEquals(bucketVersion, "Suspended");

        putBucketVersioningRequest.setBucketVersion("Enabled");
        ossClient.putBucketVersioning(putBucketVersioningRequest);

        Assert.assertEquals(ossClient.getBucketVersioning(bucketName), "Enabled");
    }

    @Test
    public void getBucketVersions() {
        System.out.println(ossClient.getBucketVersioning(bucketName));
        Assert.assertEquals(ossClient.getBucketVersioning(bucketName), "Enabled");
        InputStream inputStream = TestUtils.genFixedLengthInputStream(1024);
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, TEST_OBJECT_KEY, inputStream);

        PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);

        ObjectAcl m = ossClient.getObjectAcl(bucketName,TEST_OBJECT_KEY);
        ossClient.deleteObject(bucketName,TEST_OBJECT_KEY);
        // getBucketVersions
        ListObjectVersionsRequest listObjectVersionsRequest = new ListObjectVersionsRequest(bucketName);
        ObjectVersionsListing n = ossClient.listObjectVersions(listObjectVersionsRequest);

        for (OSSObjectVersionSummary t : n.getObjectSummaries()) {
            System.out.println(t.getBucketName());
            System.out.println(t.getIsLatest());
            System.out.println(t.getETag());
            System.out.println(t.getVersionId());
        }
        System.out.println(m.getRequestId());
    }

    /**
     * Description: buckeInfo接口测试
     * case 1 : 开启多版本后, 获取到bucketInfo信息中的多版本状态"Enabled"
     * case 2 : 暂停多版本后，能够获取到多版本信息中的版本状态:"Suspended"
     * case 3 : 新创建bucket获取默认，获取到的默认bucketinfo信息中应该包括多版本状态："Disabled"
     */
    @Test
    public void testGetBucketInfo() {

        try {
            // case 1
            PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
            putBucketVersioningRequest.setBucketVersion("Enabled");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            Assert.assertEquals("Enabled", ossClient.getBucketInfo(bucketName).getBucketVersion());

            putBucketVersioningRequest.setBucketVersion("Suspended");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            Assert.assertEquals("Suspended", ossClient.getBucketInfo(bucketName).getBucketVersion());

            ossClient.createBucket(pure_bucketName);
            Assert.assertEquals("Disabled", ossClient.getBucketVersioning(pure_bucketName));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            cleanUpAllBuckets(ossClient, pure_bucketName);
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
        try {
            // case 1
            PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
            putBucketVersioningRequest.setBucketVersion("Enabled");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            InputStream inputStream = TestUtils.genFixedLengthInputStream(1024);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, "TEST_PUT_OBJECT_KEY", inputStream);
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            String versionId = putObjectResult.getVersionId();
            Assert.assertNotNull(versionId);

            // case 2
            putBucketVersioningRequest.setBucketVersion("Suspended");
            ossClient.putBucketVersioning(putBucketVersioningRequest);
            PutObjectResult newputObjectResult = ossClient.putObject(putObjectRequest);
            Assert.assertNull(newputObjectResult.getVersionId());

            // case 3
            ossClient.createBucket(pure_bucketName);
            PutObjectRequest putObjectRequest1 = new PutObjectRequest(pure_bucketName, "TEST_PUT_OBJECT_KEY", inputStream);
            PutObjectResult putObjectResult1 = ossClient.putObject(putObjectRequest1);
            Assert.assertNull(putObjectResult1.getVersionId());
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            cleanUpAllBuckets(ossClient, pure_bucketName);
        }
    }

    /**
     * description: 测试生命周期
     * case 1 : 设置过期标记的deltermarker, 设置历史版本的转变规则
     * case 2 : 设置历史版本的过期天数，设置历史版本的转变规则
     * case 3 : 更新已经有的生命周期规则
     * case 4 : 设置生命周期的时候，历史版本的过期天数必须小于转变类型中的过期天数，如果大于的话，要抛出错误
     * case 5 : 清空生命周期规则
     *
     * more:
     *
     * 结论: 测试发现后端历史版本的过期天数可以设置，过期标记可以设置，但是历史版本transitions不能设置，需要同后端同学确认（emrgence）,
     * 经过排查后端文档XML大小写问题,已修复。
     */
    @Test
    public void testBucketLifyCycle() {
        final String bucketName = "normal-set-bucket-lifecycle";
        final String ruleId0 = "delete temporary files(0)";
        final String matchPrefix0 = "temporary0/";
        final String ruleId1 = "delete temporary files(1)";
        final String matchPrefix1 = "temporary1/";

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

            /* emergence 同后端确认
             * NonCurrentDays in the NoncurrentVersionExpiration action must be later than the NoncurrentVersionTransition
             * action for StorageClass Archive 到底是大还是小，这里的逻辑很重要,每一个异常情况都要写个单独的测试 case
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
     * descriptiont: 测试设置多版本生命周期异常的地方，主要是设置不合法的参数场景
     * case 1 :  设置生命周期的时候，历史版本的过期天数必须小于转变类型中的过期天数，如果大于的话，要抛出错误
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
    public void testObjectTagging(){
        final String bucketName = "bucket-version-tagging";
        final String objeceKeyName = "object-key-tagging";

        try {

        }catch (Exception e) {

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

        } catch(Exception e) {

        } finally {

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
        final String bucketName = "bucket-version-acl";
        final String objeceKeyName = "object-key-acl";

        try {

        }catch (Exception e) {

        } finally {

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
        final String objeceKeyName = "object-key-restore";

        try {

        }catch (Exception e) {

        } finally {

        }
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("Test Ending...............");
        try {
            cleanUpAllBucketsWithVersion(ossClient, bucketName);
        } catch (Exception e) {

        }
    }

}
