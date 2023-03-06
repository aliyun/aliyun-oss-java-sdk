package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.BucketReferer;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class BucketRefererOnlyTruncateTest {
    public static OSS initOssClient() {
        String endpoint = "http://oss-us-west-1.aliyuncs.com";
        String accessKeyId = "";
        String accessKeySecret = "";
        OSS client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        return client;
    }

    public static void destroyOssClient(OSS client) {
        client.shutdown();
    }

    public static void main(String[] args) {
        String bucketName = "luanzhi-4-mx";
        OSS client = initOssClient();

        BucketReferer bucketReferer =  client.getBucketReferer(bucketName);

        String referer0 = "http://www.aliyun.com";
        String referer1 = "https://www.aliyun.com";
        String referer2 = "http://www.*.com";
        String referer3 = "https://www.?.aliyuncs.com";

        // Set non-empty referer list
        BucketReferer r = new BucketReferer();
        r.setAllowTruncateQueryString(false);
        List<String> refererList = new ArrayList<String>();
        refererList.add(referer0);
        refererList.add(referer1);
        refererList.add(referer2);
        refererList.add(referer3);
        r.setRefererList(refererList);
        client.setBucketReferer(bucketName, r);

        BucketReferer bucketReferer001 =  client.getBucketReferer(bucketName);
        Assert.assertEquals(bucketReferer001.isAllowEmptyReferer(), true);
        Assert.assertEquals(bucketReferer001.isAllowTruncateQueryString(), false);

        System.out.println("test over");
    }
}
