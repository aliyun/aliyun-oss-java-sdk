package com.aliyun.oss.common.parser;

/**
 * Created by zhoufeng.chen on 2018/1/10.
 */

import com.aliyun.oss.common.comm.io.FixedLengthInputStream;
import com.aliyun.oss.model.AddBucketReplicationRequest;
import junit.framework.Assert;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import java.io.IOException;

import static com.aliyun.oss.common.parser.RequestMarshallers.addBucketReplicationRequestMarshaller;

public class RequestMarshallersTest {
    @Test
    public void testAddBucketReplicationRequestMarshallerWithCloudLocation()
    {
        String bucketName = "alicloud-bucket";
        String targetBucketName = "alicloud-targetBucketName";
        String targetCloud = "testTargetCloud";
        String targetCloudLocation = "testTargetCloudLocation";
        AddBucketReplicationRequest addBucketReplicationRequest = new AddBucketReplicationRequest(bucketName);
        addBucketReplicationRequest.setTargetBucketName(targetBucketName);
        addBucketReplicationRequest.setTargetCloud(targetCloud);
        addBucketReplicationRequest.setTargetCloudLocation(targetCloudLocation);

        FixedLengthInputStream is = addBucketReplicationRequestMarshaller.marshall(addBucketReplicationRequest);

        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(is);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element root = doc.getRootElement();
        Element ruleElems = root.getChild("Rule");
        Element destination = ruleElems.getChild("Destination");
        String aTargetBucketName = destination.getChildText("Bucket");
        String aTargetLocation = destination.getChildText("Location");
        String aTargetCloud = destination.getChildText("Cloud");
        String aTargetCloudLocation = destination.getChildText("CloudLocation");

        Assert.assertEquals(targetBucketName, aTargetBucketName);
        Assert.assertNull(aTargetLocation);
        Assert.assertEquals(targetCloud, aTargetCloud);
        Assert.assertEquals(targetCloudLocation, aTargetCloudLocation);

    }

    @Test
    public void testAddBucketReplicationRequestMarshallerWithoutCloudLocation()
    {
        String bucketName = "alicloud-bucket";
        String targetBucketName = "alicloud-targetBucketName";
        String targetBucketLocation = "alicloud-targetBucketLocation";
        String targetCloud = "testTargetCloud";
        String targetCloudLocation = "testTargetCloudLocation";
        AddBucketReplicationRequest addBucketReplicationRequest = new AddBucketReplicationRequest(bucketName);
        addBucketReplicationRequest.setTargetBucketName(targetBucketName);
        addBucketReplicationRequest.setTargetBucketLocation(targetBucketLocation);
        addBucketReplicationRequest.setTargetCloud(targetCloud);
        addBucketReplicationRequest.setTargetCloudLocation(targetCloudLocation);

        FixedLengthInputStream is = addBucketReplicationRequestMarshaller.marshall(addBucketReplicationRequest);

        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(is);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element root = doc.getRootElement();
        Element ruleElems = root.getChild("Rule");
        Element destination = ruleElems.getChild("Destination");
        String aTargetBucketName = destination.getChildText("Bucket");
        String aTargetLocation = destination.getChildText("Location");
        String aTargetCloud = destination.getChildText("Cloud");
        String aTargetCloudLocation = destination.getChildText("CloudLocation");

        Assert.assertEquals(targetBucketName, aTargetBucketName);
        Assert.assertEquals(targetBucketLocation, aTargetLocation);
        Assert.assertNull(aTargetCloud);
        Assert.assertNull(aTargetCloudLocation);
    }
}
