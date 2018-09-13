package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Test;

public class BucketWormTest extends TestBase {

    @Test
    public void testBucketWorm() {

        WormConfiguration configuration;
        ossClient.initiateWormConfiguration(new InitiateWormConfigurationRequest(bucketName, 1));

        configuration = ossClient.getBucketWorm(new GenericRequest(bucketName));
        Assert.assertEquals(configuration.getRetentionPeriodInDays(), 1);
        Assert.assertEquals(configuration.getState(), WormConfiguration.WormState.IN_PROGRESS);


        ossClient.abortBucketWorm(new GenericRequest(bucketName));

        try {
            ossClient.getBucketWorm(new GenericRequest(bucketName));
            Assert.fail();
        }catch (OSSException e){
            Assert.assertEquals(e.getErrorCode(),"NoSuchWORMConfiguration");
        }

        ossClient.initiateWormConfiguration(new InitiateWormConfigurationRequest(bucketName, 1));
        configuration = ossClient.getBucketWorm(new GenericRequest(bucketName));
        Assert.assertEquals(configuration.getRetentionPeriodInDays(), 1);
        Assert.assertEquals(configuration.getState(), WormConfiguration.WormState.IN_PROGRESS);

        ossClient.completeBucketWorm(new CompleteWormConfigurationRequest(bucketName, configuration.getWormId()));
        configuration = ossClient.getBucketWorm(new GenericRequest(bucketName));
        Assert.assertEquals(configuration.getRetentionPeriodInDays(), 1);
        Assert.assertEquals(configuration.getState(), WormConfiguration.WormState.LOCKED);

        ossClient.extendBucketWorm(new ExtendWormConfigurationRequest(bucketName, configuration.getWormId(), 2));
        configuration = ossClient.getBucketWorm(new GenericRequest(bucketName));
        Assert.assertEquals(configuration.getRetentionPeriodInDays(), 2);
        Assert.assertEquals(configuration.getState(), WormConfiguration.WormState.LOCKED);

        ossClient.abortBucketWorm(new GenericRequest(bucketName));

        ossClient.getBucketInfo(bucketName);
    }


}
