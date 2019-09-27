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
import java.util.ArrayList;
import java.util.List;

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

        //without Cloud & CloudLocation
        //isEnableHistoricalObjectReplication = false
        //has getObjectPrefixList
        //has getReplicationActionList
        addBucketReplicationRequest.setTargetBucketName(targetBucketName);
        addBucketReplicationRequest.setTargetCloud(null);
        addBucketReplicationRequest.setTargetCloudLocation(null);
        addBucketReplicationRequest.setEnableHistoricalObjectReplication(false);
        List<String> prefixList = new ArrayList<String>();
        prefixList.add("prefix");
        addBucketReplicationRequest.setObjectPrefixList(prefixList);
        List<AddBucketReplicationRequest.ReplicationAction> replicationActionList = new ArrayList<AddBucketReplicationRequest.ReplicationAction>();
        replicationActionList.add(AddBucketReplicationRequest.ReplicationAction.ALL);
        addBucketReplicationRequest.setReplicationActionList(replicationActionList);

        is = addBucketReplicationRequestMarshaller.marshall(addBucketReplicationRequest);

        builder = new SAXBuilder();
        doc = null;
        try {
            doc = builder.build(is);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        root = doc.getRootElement();
        ruleElems = root.getChild("Rule");
        destination = ruleElems.getChild("Destination");
        aTargetBucketName = destination.getChildText("Bucket");
        aTargetLocation = destination.getChildText("Location");
        aTargetCloud = destination.getChildText("Cloud");
        aTargetCloudLocation = destination.getChildText("CloudLocation");
        String aHistoricalObjectReplication = ruleElems.getChildText("HistoricalObjectReplication");
        Element aPrefixSet = ruleElems.getChild("PrefixSet");
        Element aAction = ruleElems.getChild("Action");
        Assert.assertEquals(targetBucketName, aTargetBucketName);
        Assert.assertNull(aTargetLocation);
        Assert.assertNull(aTargetCloud);
        Assert.assertNull(aTargetCloudLocation);
        Assert.assertEquals("disabled", aHistoricalObjectReplication);
        Assert.assertNotNull(aPrefixSet);
        Assert.assertNotNull(aAction);
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

    @Test
    public void testAddBucketReplicationRequestMarshallerWithSyncRole()
    {
        String bucketName = "alicloud-bucket";
        String targetBucketName = "alicloud-targetBucketName";
        String targetCloud = "testTargetCloud";
        String targetCloudLocation = "testTargetCloudLocation";
        String syncRole = "syncRole";
        AddBucketReplicationRequest addBucketReplicationRequest = new AddBucketReplicationRequest(bucketName);
        addBucketReplicationRequest.setTargetBucketName(targetBucketName);
        addBucketReplicationRequest.setTargetCloud(targetCloud);
        addBucketReplicationRequest.setTargetCloudLocation(targetCloudLocation);
        addBucketReplicationRequest.setSyncRole(syncRole);

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
        String aSyncRole = ruleElems.getChildText("SyncRole");

        Assert.assertEquals(targetBucketName, aTargetBucketName);
        Assert.assertNull(aTargetLocation);
        Assert.assertEquals(targetCloud, aTargetCloud);
        Assert.assertEquals(targetCloudLocation, aTargetCloudLocation);
        Assert.assertEquals(syncRole, aSyncRole);
    }

    @Test
    public void testAddBucketReplicationRequestMarshallerWithoutSyncRole()
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
        String aSyncRole = ruleElems.getChildText("SyncRole");

        Assert.assertEquals(targetBucketName, aTargetBucketName);
        Assert.assertNull(aTargetLocation);
        Assert.assertEquals(targetCloud, aTargetCloud);
        Assert.assertEquals(targetCloudLocation, aTargetCloudLocation);
        Assert.assertNull(aSyncRole);
    }

    @Test
    public void testAddBucketReplicationRequestMarshallerWithReplicaKmsKeyID()
    {
        String bucketName = "alicloud-bucket";
        String targetBucketName = "alicloud-targetBucketName";
        String targetCloud = "testTargetCloud";
        String targetCloudLocation = "testTargetCloudLocation";
        String replicaKmsKeyID = "replicaKmsKeyID";
        AddBucketReplicationRequest addBucketReplicationRequest = new AddBucketReplicationRequest(bucketName);
        addBucketReplicationRequest.setTargetBucketName(targetBucketName);
        addBucketReplicationRequest.setTargetCloud(targetCloud);
        addBucketReplicationRequest.setTargetCloudLocation(targetCloudLocation);
        addBucketReplicationRequest.setReplicaKmsKeyID(replicaKmsKeyID);

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
        String aReplicaKmsKeyID = ruleElems.getChild("EncryptionConfiguration").getChildText("ReplicaKmsKeyID");

        Assert.assertEquals(targetBucketName, aTargetBucketName);
        Assert.assertNull(aTargetLocation);
        Assert.assertEquals(targetCloud, aTargetCloud);
        Assert.assertEquals(targetCloudLocation, aTargetCloudLocation);
        Assert.assertEquals(replicaKmsKeyID, aReplicaKmsKeyID);
    }

    @Test
    public void testAddBucketReplicationRequestMarshallerWithoutReplicaKmsKeyID()
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
        Element encryptionConfiguration = ruleElems.getChild("EncryptionConfiguration");

        Assert.assertEquals(targetBucketName, aTargetBucketName);
        Assert.assertNull(aTargetLocation);
        Assert.assertEquals(targetCloud, aTargetCloud);
        Assert.assertEquals(targetCloudLocation, aTargetCloudLocation);
        Assert.assertNull(encryptionConfiguration);
    }

    @Test
    public void testAddBucketReplicationRequestMarshallerWithSSEStatus()
    {
        String bucketName = "alicloud-bucket";
        String targetBucketName = "alicloud-targetBucketName";
        String targetCloud = "testTargetCloud";
        String targetCloudLocation = "testTargetCloudLocation";
        String SSEStatus = AddBucketReplicationRequest.ENABLED;
        AddBucketReplicationRequest addBucketReplicationRequest = new AddBucketReplicationRequest(bucketName);
        addBucketReplicationRequest.setTargetBucketName(targetBucketName);
        addBucketReplicationRequest.setTargetCloud(targetCloud);
        addBucketReplicationRequest.setTargetCloudLocation(targetCloudLocation);
        addBucketReplicationRequest.setSseKmsEncryptedObjectsStatus(SSEStatus);

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
        String aSSEStatus = ruleElems.getChild("SourceSelectionCriteria").
                getChild("SseKmsEncryptedObjects").getChildText("Status");

        Assert.assertEquals(targetBucketName, aTargetBucketName);
        Assert.assertNull(aTargetLocation);
        Assert.assertEquals(targetCloud, aTargetCloud);
        Assert.assertEquals(targetCloudLocation, aTargetCloudLocation);
        Assert.assertEquals(SSEStatus, aSSEStatus);

        //set disable
        SSEStatus = AddBucketReplicationRequest.DISABLED;
        addBucketReplicationRequest.setSseKmsEncryptedObjectsStatus(SSEStatus);
        is = addBucketReplicationRequestMarshaller.marshall(addBucketReplicationRequest);

        builder = new SAXBuilder();
        doc = null;
        try {
            doc = builder.build(is);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        root = doc.getRootElement();
        ruleElems = root.getChild("Rule");
        destination = ruleElems.getChild("Destination");
        aTargetBucketName = destination.getChildText("Bucket");
        aTargetLocation = destination.getChildText("Location");
        aTargetCloud = destination.getChildText("Cloud");
        aTargetCloudLocation = destination.getChildText("CloudLocation");
        aSSEStatus = ruleElems.getChild("SourceSelectionCriteria").
                getChild("SseKmsEncryptedObjects").getChildText("Status");

        Assert.assertEquals(targetBucketName, aTargetBucketName);
        Assert.assertNull(aTargetLocation);
        Assert.assertEquals(targetCloud, aTargetCloud);
        Assert.assertEquals(targetCloudLocation, aTargetCloudLocation);
        Assert.assertEquals(SSEStatus, aSSEStatus);

        //set other value
        addBucketReplicationRequest.setSseKmsEncryptedObjectsStatus("invalid");
        is = addBucketReplicationRequestMarshaller.marshall(addBucketReplicationRequest);

        builder = new SAXBuilder();
        doc = null;
        try {
            doc = builder.build(is);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        root = doc.getRootElement();
        ruleElems = root.getChild("Rule");
        destination = ruleElems.getChild("Destination");
        aTargetBucketName = destination.getChildText("Bucket");
        aTargetLocation = destination.getChildText("Location");
        aTargetCloud = destination.getChildText("Cloud");
        aTargetCloudLocation = destination.getChildText("CloudLocation");

        Assert.assertEquals(targetBucketName, aTargetBucketName);
        Assert.assertNull(aTargetLocation);
        Assert.assertEquals(targetCloud, aTargetCloud);
        Assert.assertEquals(targetCloudLocation, aTargetCloudLocation);
        Assert.assertNull(ruleElems.getChild("SourceSelectionCriteria"));

    }

    @Test
    public void testAddBucketReplicationRequestMarshallerWithoutSSEStatus()
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
        Element sourceSelectionCriteria = ruleElems.getChild("SourceSelectionCriteria");

        Assert.assertEquals(targetBucketName, aTargetBucketName);
        Assert.assertNull(aTargetLocation);
        Assert.assertEquals(targetCloud, aTargetCloud);
        Assert.assertEquals(targetCloudLocation, aTargetCloudLocation);
        Assert.assertNull(sourceSelectionCriteria);
    }

}
