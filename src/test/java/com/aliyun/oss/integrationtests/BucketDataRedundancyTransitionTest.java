package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

public class BucketDataRedundancyTransitionTest extends TestBase {

    @Test
    public void createBucketDataRedundancyTransitionTest() {

        CreateBucketDataRedundancyTransitionResult createResult = ossClient.createBucketDataRedundancyTransition(bucketName, "ZRS");
        Assert.assertEquals(200, createResult.getResponse().getStatusCode());
        Assert.assertNotNull(createResult.getTaskId());

        GetBucketDataRedundancyTransitionRequest getBucketDataRedundancyTransitionRequest = new GetBucketDataRedundancyTransitionRequest(bucketName, createResult.getTaskId());
        GetBucketDataRedundancyTransitionResult getResult = ossClient.getBucketDataRedundancyTransition(getBucketDataRedundancyTransitionRequest);
        Assert.assertEquals(200, getResult.getResponse().getStatusCode());
        Assert.assertNotNull(getResult.getTaskId());
        Assert.assertNotNull(getResult.getCreateTime());
        Assert.assertNotNull(getResult.getStatus());

        ListUserDataRedundancyTransitionRequest request = new ListUserDataRedundancyTransitionRequest();
        ListUserDataRedundancyTransitionResult listUserResult = ossClient.listUserDataRedundancyTransition(request);
        Assert.assertEquals(200, listUserResult.getResponse().getStatusCode());
        Assert.assertEquals(getResult.getBucket(), listUserResult.getBucketDataRedundancyTransition().get(0).getBucket());
        Assert.assertEquals(getResult.getTaskId(), listUserResult.getBucketDataRedundancyTransition().get(0).getTaskId());
        Assert.assertEquals(getResult.getStatus(), listUserResult.getBucketDataRedundancyTransition().get(0).getStatus());
        Assert.assertEquals(getResult.getCreateTime(), listUserResult.getBucketDataRedundancyTransition().get(0).getCreateTime());
        Assert.assertEquals(getResult.getStartTime(), listUserResult.getBucketDataRedundancyTransition().get(0).getStartTime());
        Assert.assertEquals(getResult.getEndTime(), listUserResult.getBucketDataRedundancyTransition().get(0).getEndTime());
        Assert.assertEquals(getResult.getEstimatedRemainingTime(), listUserResult.getBucketDataRedundancyTransition().get(0).getEstimatedRemainingTime());
        Assert.assertEquals(getResult.getProcessPercentage(), listUserResult.getBucketDataRedundancyTransition().get(0).getProcessPercentage());


        List<GetBucketDataRedundancyTransitionResult> list = ossClient.listBucketDataRedundancyTransition(bucketName);
        Assert.assertEquals(getResult.getTaskId(),  list.get(0).getTaskId());
        Assert.assertEquals(bucketName,  list.get(0).getBucket());


        VoidResult delResult = ossClient.deleteBucketDataRedundancyTransition(bucketName, getResult.getTaskId());
        Assert.assertEquals(204, delResult.getResponse().getStatusCode());
    }
}

