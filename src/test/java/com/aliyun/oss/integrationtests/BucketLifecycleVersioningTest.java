package com.aliyun.oss.integrationtests;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.SetBucketLifecycleRequest;
import com.aliyun.oss.model.StorageClass;
import org.junit.jupiter.api.*;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.aliyun.oss.integrationtests.TestConstants.NO_SUCH_LIFECYCLE_ERR;
import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;
import com.aliyun.oss.model.LifecycleRule.NoncurrentVersionStorageTransition;
import com.aliyun.oss.model.LifecycleRule.NoncurrentVersionExpiration;

public class BucketLifecycleVersioningTest extends TestBase {
    private OSSClient ossClient;
    private String bucketName;
    private String endpoint;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        bucketName = super.bucketName + "-lifecycle-version";
        endpoint = TestConfig.OSS_TEST_ENDPOINT;

        //create client
        ClientConfiguration conf = new ClientConfiguration().setSupportCname(false);
        Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
        ossClient = new OSSClient(endpoint, new DefaultCredentialProvider(credentials), conf);
        ossClient.createBucket(bucketName);
    }

    @Test
    public void testLifecycleVersioning() throws ParseException {
        final String ruleId0 = "id0";
        final String matchPrefix0 = "prefix0/";

        try {
            SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
            LifecycleRule rule = new LifecycleRule();
            rule = new LifecycleRule(ruleId0, matchPrefix0, LifecycleRule.RuleStatus.Enabled);

            // expiredDeleteMarker
            rule.setExpiredDeleteMarker(true);

            // NoncurrentVersionExpiration
            NoncurrentVersionExpiration noncurrentVersionExpiration = new NoncurrentVersionExpiration().withNoncurrentDays(30);
            Assertions.assertTrue(noncurrentVersionExpiration.hasNoncurrentDays());

            // NoncurrentVersionStorageTransition
            NoncurrentVersionStorageTransition noncurrentVersionStorageTransition1 =
                    new NoncurrentVersionStorageTransition().withNoncurrentDays(10).withStrorageClass(StorageClass.IA);
            NoncurrentVersionStorageTransition noncurrentVersionStorageTransition2 =
                    new NoncurrentVersionStorageTransition().withNoncurrentDays(20).withStrorageClass(StorageClass.Archive);
            Assertions.assertTrue(noncurrentVersionStorageTransition1.hasNoncurrentDays());
            Assertions.assertTrue(noncurrentVersionStorageTransition2.hasNoncurrentDays());

            List<NoncurrentVersionStorageTransition> noncurrentVersionStorageTransitions = new ArrayList<NoncurrentVersionStorageTransition>();
            noncurrentVersionStorageTransitions.add(noncurrentVersionStorageTransition1);
            noncurrentVersionStorageTransitions.add(noncurrentVersionStorageTransition2);

            // add rule
            rule.setNoncurrentVersionExpiration(noncurrentVersionExpiration);
            rule.setNoncurrentVersionStorageTransitions(noncurrentVersionStorageTransitions);
            request.AddLifecycleRule(rule);


            ossClient.setBucketLifecycle(request);

            List<LifecycleRule> rules = ossClient.getBucketLifecycle(bucketName);
            Assertions.assertEquals(rules.size(), 1);
            Assertions.assertEquals(ruleId0, rules.get(0).getId());
            Assertions.assertEquals(matchPrefix0, rules.get(0).getPrefix());
            Assertions.assertEquals(LifecycleRule.RuleStatus.Enabled, rules.get(0).getStatus());
            Assertions.assertTrue(rules.get(0).getExpiredDeleteMarker());
            Assertions.assertEquals(30, rules.get(0).getNoncurrentVersionExpiration().getNoncurrentDays().intValue());
            Assertions.assertEquals(2, rules.get(0).getNoncurrentVersionStorageTransitions().size());

            ossClient.deleteBucketLifecycle(bucketName);

            // Try get bucket lifecycle again
            try {
                ossClient.getBucketLifecycle(bucketName);
            } catch (OSSException e) {
                Assertions.assertEquals(OSSErrorCode.NO_SUCH_LIFECYCLE, e.getErrorCode());
                Assertions.assertTrue(e.getMessage().startsWith(NO_SUCH_LIFECYCLE_ERR));
            }
        } catch (OSSException e) {
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testLifecycleVersionUnnormal() throws ParseException {
        final String ruleId0 = "id0";
        final String matchPrefix0 = "prefix0/";

        //  Only one expiration property should be specified.
        try {
            SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
            LifecycleRule rule = new LifecycleRule();
            rule = new LifecycleRule(ruleId0, matchPrefix0, LifecycleRule.RuleStatus.Enabled);

            rule.setExpirationDays(10);
            rule.setExpiredDeleteMarker(true);
            request.AddLifecycleRule(rule);
            Assertions.fail("Only one expiration property should be specified.");
        } catch (IllegalArgumentException e) {
        }

        // NoncurrentVersionExpiration days < NoncurrentVersionStorageTransition days
        try {
            SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
            LifecycleRule rule = new LifecycleRule();
            rule = new LifecycleRule(ruleId0, matchPrefix0, LifecycleRule.RuleStatus.Enabled);

            // NoncurrentVersionExpiration
            NoncurrentVersionExpiration noncurrentVersionExpiration = new NoncurrentVersionExpiration(5);

            // NoncurrentVersionStorageTransition
            NoncurrentVersionStorageTransition noncurrentVersionStorageTransition1 =
                    new NoncurrentVersionStorageTransition().withNoncurrentDays(10).withStrorageClass(StorageClass.IA);
            NoncurrentVersionStorageTransition noncurrentVersionStorageTransition2 =
                    new NoncurrentVersionStorageTransition(20, StorageClass.Archive);
            List<NoncurrentVersionStorageTransition> noncurrentVersionStorageTransitions = new ArrayList<NoncurrentVersionStorageTransition>();
            noncurrentVersionStorageTransitions.add(noncurrentVersionStorageTransition1);
            noncurrentVersionStorageTransitions.add(noncurrentVersionStorageTransition2);

            // add rule
            rule.setNoncurrentVersionExpiration(noncurrentVersionExpiration);
            rule.setNoncurrentVersionStorageTransitions(noncurrentVersionStorageTransitions);
            request.AddLifecycleRule(rule);
            ossClient.setBucketLifecycle(request);
            Assertions.fail("NoncurrentVersionExpiration days should not later than NoncurrentVersionStorageTransition days.");
        } catch (OSSException e) {
            Assertions.assertEquals(e.getErrorCode(), OSSErrorCode.INVALID_ARGUMENT);
        }

        // Archive transition days < IA transition days
        try {
            SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
            LifecycleRule rule = new LifecycleRule();
            rule = new LifecycleRule(ruleId0, matchPrefix0, LifecycleRule.RuleStatus.Enabled);

            // NoncurrentVersionExpiration
            NoncurrentVersionExpiration noncurrentVersionExpiration = new NoncurrentVersionExpiration().withNoncurrentDays(30);

            // NoncurrentVersionStorageTransition
            NoncurrentVersionStorageTransition noncurrentVersionStorageTransition1 =
                    new NoncurrentVersionStorageTransition().withNoncurrentDays(20).withStrorageClass(StorageClass.IA);
            NoncurrentVersionStorageTransition noncurrentVersionStorageTransition2 =
                    new NoncurrentVersionStorageTransition().withNoncurrentDays(10).withStrorageClass(StorageClass.Archive);
            List<NoncurrentVersionStorageTransition> noncurrentVersionStorageTransitions = new ArrayList<NoncurrentVersionStorageTransition>();
            noncurrentVersionStorageTransitions.add(noncurrentVersionStorageTransition1);
            noncurrentVersionStorageTransitions.add(noncurrentVersionStorageTransition2);

            // add rule
            rule.setNoncurrentVersionExpiration(noncurrentVersionExpiration);
            rule.setNoncurrentVersionStorageTransitions(noncurrentVersionStorageTransitions);
            request.AddLifecycleRule(rule);
            ossClient.setBucketLifecycle(request);
            Assertions.fail("Archive transition days should not smaller than IA transition days.");
        } catch (OSSException e) {
            Assertions.assertEquals(e.getErrorCode(), OSSErrorCode.INVALID_ARGUMENT);
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Override
    public void tearDown() throws Exception {
        if (ossClient != null) {
            ossClient.shutdown();
            ossClient = null;
        }
        super.tearDown();
    }

}
