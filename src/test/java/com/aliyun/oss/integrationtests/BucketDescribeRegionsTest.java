package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Test;

public class BucketDescribeRegionsTest extends TestBase {

    private static final String region = "oss-cn-chengdu";
    @Test
    public void testBucketDescribeRegions() {

        try {
            DescribeRegionsResult regionsResult = ossClient.describeRegions(new DescribeRegionsRequest().WithRegion(region));

            Assert.assertEquals(region, regionsResult.getRegionInfoList().get(0).getRegion());
            Assert.assertNotNull( regionsResult.getRegionInfoList().get(0).getInternalEndpoint());
            Assert.assertNotNull( regionsResult.getRegionInfoList().get(0).getInternetEndpoint());
            Assert.assertNotNull( regionsResult.getRegionInfoList().get(0).getAccelerateEndpoint());
        } catch (Exception e1) {
            Assert.fail(e1.getMessage());
        }
    }

    @Test
    public void testBucketDescribeRegionsException() {

        try {
            DescribeRegionsResult regionsResult = ossClient.describeRegions(new DescribeRegionsRequest());
            Assert.assertNotNull( regionsResult.getRegionInfoList().size());
            Assert.assertNotNull( regionsResult.getRegionInfoList().get(0).getRegion());
            Assert.assertNotNull( regionsResult.getRegionInfoList().get(0).getInternetEndpoint());
            Assert.assertNotNull( regionsResult.getRegionInfoList().get(0).getInternetEndpoint());
            Assert.assertNotNull( regionsResult.getRegionInfoList().get(0).getAccelerateEndpoint());
        } catch (Exception e1) {
            Assert.fail(e1.getMessage());
        }
    }
}