package com.aliyun.oss.common.parser;

import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.internal.ResponseParsers;
import com.aliyun.oss.internal.model.OSSErrorResult;
import com.aliyun.oss.model.*;
import org.junit.jupiter.api.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoufeng.chen on 2018/1/10.
 */
public class ResponseParsersTest {
    @Test
    public void testParseGetBucketReplicationWithCloudLocation() {
        String respBody = "<ReplicationConfiguration>\n" +
                " <Rule>\n" +
                "    <ID>12345678</ID>\n" +
                "        <Destination>\n" +
                "            <Bucket>testBucketName</Bucket>\n" +
                "            <Cloud>testCloud</Cloud>\n" +
                "            <CloudLocation>testCloudLocation</CloudLocation>\n" +
                "        </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                " </Rule>\n" +
                "</ReplicationConfiguration>\n";
        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<ReplicationRule> rules = null;
        try {
            rules = ResponseParsers.parseGetBucketReplication(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }
        Assertions.assertTrue(rules.size() > 0);

        ReplicationRule rule = rules.get(0);
        Assertions.assertEquals("12345678", rule.getReplicationRuleID());
        Assertions.assertEquals("testBucketName", rule.getTargetBucketName());
        Assertions.assertNull(rule.getTargetBucketLocation());
        Assertions.assertEquals("testCloud", rule.getTargetCloud());
        Assertions.assertEquals("testCloudLocation", rule.getTargetCloudLocation());
        Assertions.assertEquals(true, rule.isEnableHistoricalObjectReplication());
    }

    @Test
    public void testParseGetBucketReplicationWithoutCloudLocation() {
        String respBody = "<ReplicationConfiguration>\n" +
                " <Rule>\n" +
                "    <ID>12345678</ID>\n" +
                "        <Destination>\n" +
                "            <Bucket>testBucketName</Bucket>\n" +
                "            <Location>testLocation</Location>\n" +
                "        </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>disabled</HistoricalObjectReplication>\n" +
                " </Rule>\n" +
                "</ReplicationConfiguration>\n";
        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<ReplicationRule> rules = null;
        try {
            rules = ResponseParsers.parseGetBucketReplication(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }
        Assertions.assertTrue(rules.size() > 0);

        ReplicationRule rule = rules.get(0);
        Assertions.assertEquals("12345678", rule.getReplicationRuleID());
        Assertions.assertEquals("testBucketName", rule.getTargetBucketName());
        Assertions.assertEquals("testLocation", rule.getTargetBucketLocation());
        Assertions.assertNull(rule.getTargetCloud());
        Assertions.assertNull(rule.getTargetCloudLocation());
        Assertions.assertEquals(false, rule.isEnableHistoricalObjectReplication());
        Assertions.assertNull(rule.getSourceBucketLocation());

    }

    @Test
    public void testParseGetBucketReplicationWithSourceLocation() {
        String respBody = "<ReplicationConfiguration>\n" +
                " <Rule>\n" +
                "    <ID>12345678</ID>\n" +
                "    <Destination>\n" +
                "        <Bucket>testBucketName</Bucket>\n" +
                "        <Cloud>testCloud</Cloud>\n" +
                "        <CloudLocation>testCloudLocation</CloudLocation>\n" +
                "    </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                "    <Source>\n" +
                "        <Location>sourceLocation</Location>\n" +
                "    </Source>\n" +
                " </Rule>\n" +
                "</ReplicationConfiguration>\n";
        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<ReplicationRule> rules = null;
        try {
            rules = ResponseParsers.parseGetBucketReplication(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }
        Assertions.assertTrue(rules.size() > 0);

        ReplicationRule rule = rules.get(0);
        Assertions.assertEquals("12345678", rule.getReplicationRuleID());
        Assertions.assertEquals("testBucketName", rule.getTargetBucketName());
        Assertions.assertNull(rule.getTargetBucketLocation());
        Assertions.assertEquals("testCloud", rule.getTargetCloud());
        Assertions.assertEquals("testCloudLocation", rule.getTargetCloudLocation());
        Assertions.assertEquals(true, rule.isEnableHistoricalObjectReplication());
        Assertions.assertEquals("sourceLocation", rule.getSourceBucketLocation());
    }

    @Test
    public void testParseGetBucketReplicationProgressWithCloudLocation() {
        Date dt = new Date();
        String respBody = "<ReplicationProgress>\n" +
                " <Rule>\n" +
                "     <ID>12345678</ID>\n" +
                "     <Destination>\n" +
                "         <Bucket>testBucketName</Bucket>\n" +
                "         <Cloud>testCloud</Cloud>\n" +
                "         <CloudLocation>testCloudLocation</CloudLocation>\n" +
                "     </Destination>\n" +
                "     <PrefixSet>\n" +
                "         <Prefix>aaa</Prefix>\n" +
                "         <Prefix>bbb</Prefix>\n" +
                "     </PrefixSet>\n" +
                "     <Action>xxx,xxx,xxx</Action>\n" +
                "     <Status>doing</Status>\n" +
                "     <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                "     <Progress>\n" +
                "         <HistoricalObject>0.8</HistoricalObject>\n" +
                "         <NewObject>" + DateUtil.formatIso8601Date(dt) + "</NewObject>\n" +
                "     </Progress>\n" +
                " </Rule>\n" +
                "</ReplicationProgress>";


        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        BucketReplicationProgress progress = null;
        try {
            progress = ResponseParsers.parseGetBucketReplicationProgress(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication process response body fail!");
        }

        Assertions.assertEquals("12345678", progress.getReplicationRuleID());
        Assertions.assertEquals("testBucketName", progress.getTargetBucketName());
        Assertions.assertNull(progress.getTargetBucketLocation());
        Assertions.assertEquals("testCloud", progress.getTargetCloud());
        Assertions.assertEquals("testCloudLocation", progress.getTargetCloudLocation());
        Assertions.assertEquals(0.8f, progress.getHistoricalObjectProgress());
        Assertions.assertEquals(dt, progress.getNewObjectProgress());

    }

    @Test
    public void testParseGetBucketReplicationProgressWithoutCloudLocation() {
        Date dt = new Date();
        String respBody = "<ReplicationProgress>\n" +
                " <Rule>\n" +
                "     <ID>12345678</ID>\n" +
                "     <Destination>\n" +
                "         <Bucket>testBucketName</Bucket>\n" +
                "         <Location>testLocation</Location>\n" +
                "     </Destination>\n" +
                "     <PrefixSet>\n" +
                "         <Prefix>aaa</Prefix>\n" +
                "         <Prefix>bbb</Prefix>\n" +
                "     </PrefixSet>\n" +
                "     <Action>xxx,xxx,xxx</Action>\n" +
                "     <Status>doing</Status>\n" +
                "     <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                "     <Progress>\n" +
                "         <HistoricalObject>0.9</HistoricalObject>\n" +
                "         <NewObject>" + DateUtil.formatIso8601Date(dt) + "</NewObject>\n" +
                "     </Progress>\n" +
                " </Rule>\n" +
                "</ReplicationProgress>";


        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        BucketReplicationProgress progress = null;
        try {
            progress = ResponseParsers.parseGetBucketReplicationProgress(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication process response body fail!");
        }

        Assertions.assertEquals("12345678", progress.getReplicationRuleID());
        Assertions.assertEquals("testBucketName", progress.getTargetBucketName());
        Assertions.assertEquals("testLocation", progress.getTargetBucketLocation());
        Assertions.assertNull(progress.getTargetCloud());
        Assertions.assertNull(progress.getTargetCloudLocation());
        Assertions.assertEquals(0.9f, progress.getHistoricalObjectProgress());
        Assertions.assertEquals(dt, progress.getNewObjectProgress());
    }

    @Test
    public void testParseGetBucketReplicationProgressWithUnnormalParam() {
        Date dt = new Date();
        // <HistoricalObjectReplication>disabled
        String respBody1 = "<ReplicationProgress>\n" +
                " <Rule>\n" +
                "     <ID>12345678</ID>\n" +
                "     <Destination>\n" +
                "         <Bucket>testBucketName</Bucket>\n" +
                "         <Location>testLocation</Location>\n" +
                "     </Destination>\n" +
                "     <PrefixSet>\n" +
                "         <Prefix>aaa</Prefix>\n" +
                "         <Prefix>bbb</Prefix>\n" +
                "     </PrefixSet>\n" +
                "     <Action>xxx,xxx,xxx</Action>\n" +
                "     <Status>doing</Status>\n" +
                "     <HistoricalObjectReplication>disabled</HistoricalObjectReplication>\n" +
                "     <Progress>\n" +
                "         <HistoricalObject>0.9</HistoricalObject>\n" +
                "         <NewObject>" + DateUtil.formatIso8601Date(dt) + "</NewObject>\n" +
                "     </Progress>\n" +
                " </Rule>\n" +
                "</ReplicationProgress>";

        // none <Progress>
        String respBody2 = "<ReplicationProgress>\n" +
                " <Rule>\n" +
                "     <ID>12345678</ID>\n" +
                "     <Destination>\n" +
                "         <Bucket>testBucketName</Bucket>\n" +
                "         <Location>testLocation</Location>\n" +
                "     </Destination>\n" +
                "     <PrefixSet>\n" +
                "         <Prefix>aaa</Prefix>\n" +
                "         <Prefix>bbb</Prefix>\n" +
                "     </PrefixSet>\n" +
                "     <Action>xxx,xxx,xxx</Action>\n" +
                "     <Status>doing</Status>\n" +
                "     <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                " </Rule>\n" +
                "</ReplicationProgress>";

        // none <HistoricalObject>
        String respBody3 = "<ReplicationProgress>\n" +
                " <Rule>\n" +
                "     <ID>12345678</ID>\n" +
                "     <Destination>\n" +
                "         <Bucket>testBucketName</Bucket>\n" +
                "         <Location>testLocation</Location>\n" +
                "     </Destination>\n" +
                "     <PrefixSet>\n" +
                "         <Prefix>aaa</Prefix>\n" +
                "         <Prefix>bbb</Prefix>\n" +
                "     </PrefixSet>\n" +
                "     <Action>xxx,xxx,xxx</Action>\n" +
                "     <Status>doing</Status>\n" +
                "     <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                "     <Progress>\n" +
                "         <NewObject>" + DateUtil.formatIso8601Date(dt) + "</NewObject>\n" +
                "     </Progress>\n" +
                " </Rule>\n" +
                "</ReplicationProgress>";


        InputStream instream1 = null;
        InputStream instream2 = null;
        InputStream instream3 = null;
        try {
            instream1 = new ByteArrayInputStream(respBody1.getBytes("utf-8"));
            instream2 = new ByteArrayInputStream(respBody2.getBytes("utf-8"));
            instream3 = new ByteArrayInputStream(respBody3.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        BucketReplicationProgress progress1 = null;
        BucketReplicationProgress progress2 = null;
        BucketReplicationProgress progress3 = null;
        try {
            progress1 = ResponseParsers.parseGetBucketReplicationProgress(instream1);
            progress2 = ResponseParsers.parseGetBucketReplicationProgress(instream2);
            progress3 = ResponseParsers.parseGetBucketReplicationProgress(instream3);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication process response body fail!");
        }

        Assertions.assertFalse(progress1.isEnableHistoricalObjectReplication());
        Assertions.assertEquals(Float.parseFloat("0"), progress2.getHistoricalObjectProgress());
        Assertions.assertNull(progress2.getNewObjectProgress());
        Assertions.assertEquals(Float.parseFloat("0"), progress3.getHistoricalObjectProgress());
        Assertions.assertNotNull(progress3.getNewObjectProgress());

        // test parse error
        String respBody4 = respBody1 + "-error-body";

        InputStream instream4 = null;
        try {
            instream4 = new ByteArrayInputStream(respBody4.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketReplicationProgress(instream4);
        } catch (ResponseParseException e) {
            // expected exception.
        }
    }

    @Test
    public void testParseGetBucketReplicationWithSyncRole() {
        String respBody = "<ReplicationConfiguration>\n" +
                " <Rule>\n" +
                "    <ID>12345678</ID>\n" +
                "        <Destination>\n" +
                "            <Bucket>testBucketName</Bucket>\n" +
                "            <Cloud>testCloud</Cloud>\n" +
                "            <CloudLocation>testCloudLocation</CloudLocation>\n" +
                "        </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                "    <SyncRole>ft-sync-role</SyncRole>\n" +
                " </Rule>\n" +
                "</ReplicationConfiguration>\n";
        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<ReplicationRule> rules = null;
        try {
            rules = ResponseParsers.parseGetBucketReplication(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }
        Assertions.assertTrue(rules.size() > 0);

        ReplicationRule rule = rules.get(0);
        Assertions.assertEquals("12345678", rule.getReplicationRuleID());
        Assertions.assertEquals("testBucketName", rule.getTargetBucketName());
        Assertions.assertNull(rule.getTargetBucketLocation());
        Assertions.assertEquals("testCloud", rule.getTargetCloud());
        Assertions.assertEquals("testCloudLocation", rule.getTargetCloudLocation());
        Assertions.assertEquals(true, rule.isEnableHistoricalObjectReplication());
        Assertions.assertEquals("ft-sync-role", rule.getSyncRole());
        Assertions.assertNull(rule.getSseKmsEncryptedObjectsStatus());
        Assertions.assertNull(rule.getReplicaKmsKeyID());
    }

    @Test
    public void testParseGetBucketReplicationWithReplicaKmsKeyID() {
        String respBody = "<ReplicationConfiguration>\n" +
                " <Rule>\n" +
                "    <ID>12345678</ID>\n" +
                "        <Destination>\n" +
                "            <Bucket>testBucketName</Bucket>\n" +
                "            <Cloud>testCloud</Cloud>\n" +
                "            <CloudLocation>testCloudLocation</CloudLocation>\n" +
                "        </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                "    <EncryptionConfiguration>\n" +
                "        <ReplicaKmsKeyID>12345</ReplicaKmsKeyID>\n" +
                "    </EncryptionConfiguration>\n" +
                " </Rule>\n" +
                "</ReplicationConfiguration>\n";
        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<ReplicationRule> rules = null;
        try {
            rules = ResponseParsers.parseGetBucketReplication(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }
        Assertions.assertTrue(rules.size() > 0);

        ReplicationRule rule = rules.get(0);
        Assertions.assertEquals("12345678", rule.getReplicationRuleID());
        Assertions.assertEquals("testBucketName", rule.getTargetBucketName());
        Assertions.assertNull(rule.getTargetBucketLocation());
        Assertions.assertEquals("testCloud", rule.getTargetCloud());
        Assertions.assertEquals("testCloudLocation", rule.getTargetCloudLocation());
        Assertions.assertEquals(true, rule.isEnableHistoricalObjectReplication());
        Assertions.assertNull(rule.getSyncRole());
        Assertions.assertNull(rule.getSseKmsEncryptedObjectsStatus());
        Assertions.assertEquals("12345", rule.getReplicaKmsKeyID());
    }

    @Test
    public void testParseGetBucketReplicationWithSseKmsEncryptedObjectsStatus() {
        String respBody = "<ReplicationConfiguration>\n" +
                " <Rule>\n" +
                "    <ID>12345678</ID>\n" +
                "        <Destination>\n" +
                "            <Bucket>testBucketName</Bucket>\n" +
                "            <Cloud>testCloud</Cloud>\n" +
                "            <CloudLocation>testCloudLocation</CloudLocation>\n" +
                "        </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                "    <SourceSelectionCriteria>\n" +
                "         <SseKmsEncryptedObjects>\n" +
                "             <Status>Enabled</Status>\n" +
                "         </SseKmsEncryptedObjects>\n" +
                "    </SourceSelectionCriteria>\n" +
                " </Rule>\n" +
                "</ReplicationConfiguration>\n";
        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<ReplicationRule> rules = null;
        try {
            rules = ResponseParsers.parseGetBucketReplication(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }
        Assertions.assertTrue(rules.size() > 0);

        ReplicationRule rule = rules.get(0);
        Assertions.assertEquals("12345678", rule.getReplicationRuleID());
        Assertions.assertEquals("testBucketName", rule.getTargetBucketName());
        Assertions.assertNull(rule.getTargetBucketLocation());
        Assertions.assertEquals("testCloud", rule.getTargetCloud());
        Assertions.assertEquals("testCloudLocation", rule.getTargetCloudLocation());
        Assertions.assertEquals(true, rule.isEnableHistoricalObjectReplication());
        Assertions.assertNull(rule.getSyncRole());
        Assertions.assertEquals("Enabled", rule.getSseKmsEncryptedObjectsStatus());
        Assertions.assertNull(rule.getReplicaKmsKeyID());
    }

    @Test
    public void testParseGetBucketReplicationWithPrefixAndAction() {
        String respBody = "<ReplicationConfiguration>\n" +
                " <Rule>\n" +
                "    <ID>12345678</ID>\n" +
                "        <Destination>\n" +
                "            <Bucket>testBucketName</Bucket>\n" +
                "            <Cloud>testCloud</Cloud>\n" +
                "            <CloudLocation>testCloudLocation</CloudLocation>\n" +
                "        </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                "    <PrefixSet>\n" +
                "        <Prefix>test-prefix-1</Prefix>\n" +
                "        <Prefix>test-prefix-2</Prefix>\n" +
                "        <Prefix>test-prefix-3</Prefix>\n" +
                "    </PrefixSet>\n" +
                "    <Action>PUT,DELETE</Action>\n" +
                "    <EncryptionConfiguration>\n" +
                "        <ReplicaKmsKeyID>12345</ReplicaKmsKeyID>\n" +
                "    </EncryptionConfiguration>\n" +
                " </Rule>\n" +
                "</ReplicationConfiguration>\n";
        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<ReplicationRule> rules = null;
        try {
            rules = ResponseParsers.parseGetBucketReplication(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }
        Assertions.assertTrue(rules.size() > 0);

        ReplicationRule rule = rules.get(0);
        Assertions.assertEquals("12345678", rule.getReplicationRuleID());
        Assertions.assertEquals("testBucketName", rule.getTargetBucketName());
        Assertions.assertNull(rule.getTargetBucketLocation());
        Assertions.assertEquals("testCloud", rule.getTargetCloud());
        Assertions.assertEquals("testCloudLocation", rule.getTargetCloudLocation());
        Assertions.assertEquals(true, rule.isEnableHistoricalObjectReplication());
        Assertions.assertEquals(3, rule.getObjectPrefixList().size());
        for (String o : rule.getObjectPrefixList()) {
            Assertions.assertTrue(o.startsWith("test-prefix-"));
        }
        Assertions.assertEquals(2, rule.getReplicationActionList().size());
        Assertions.assertNull(rule.getSyncRole());
        Assertions.assertNull(rule.getSseKmsEncryptedObjectsStatus());
        Assertions.assertEquals("12345", rule.getReplicaKmsKeyID());
    }

    @Test
    public void parseGetBucketReplicationLocation() {
        String respBody = "<ReplicationLocation>\n" +
                "    <Location>test-location-1</Location>\n" +
                "    <Location>test-location-2</Location>\n" +
                "</ReplicationLocation>\n";
        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<String> locations = null;
        try {
            locations = ResponseParsers.parseGetBucketReplicationLocation(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }
        Assertions.assertEquals(2, locations.size());
        for (String o : locations) {
            Assertions.assertTrue(o.startsWith("test-location-"));
        }


        // test parse error
        respBody  += "-error-body";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketReplicationLocation(instream);
            Assertions.fail("should be failed here.");
        } catch (Exception e) {
            // expected exception.
        }
    }

    @Test
    public void testParseGetLiveChannelStat() {
        String respBody = "" +
                "<LiveChannelStat>\n" +
                "  <Status>Live</Status>\n" +
                "  <ConnectedTime>2016-08-25T06:25:15.000Z</ConnectedTime>\n" +
                "  <RemoteAddr>10.1.2.3:47745</RemoteAddr>\n" +
                "  <Video>\n" +
                "    <Width>1280</Width>\n" +
                "    <Height>536</Height>\n" +
                "    <FrameRate>24</FrameRate>\n" +
                "    <Bandwidth>0</Bandwidth>\n" +
                "    <Codec>H264</Codec>\n" +
                "  </Video>\n" +
                "  <Audio>\n" +
                "    <Bandwidth>0</Bandwidth>\n" +
                "    <SampleRate>44100</SampleRate>\n" +
                "    <Codec>ADPCM</Codec>\n" +
                "  </Audio>\n" +
                "</LiveChannelStat>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        LiveChannelStat stat = null;
        try {
            stat = ResponseParsers.parseGetLiveChannelStat(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Assertions.assertEquals(PushflowStatus.Live, stat.getPushflowStatus());
        Assertions.assertEquals("10.1.2.3:47745", stat.getRemoteAddress());
        Assertions.assertEquals(1280, stat.getVideoStat().getWidth());
        Assertions.assertEquals(44100, stat.getAudioStat().getSampleRate());
    }

    @Test
    public void testParseGetLiveChannelHistory() {
        String respBody = "" +
                "<LiveChannelHistory>\n" +
                "  <LiveRecord>\n" +
                "    <StartTime>2016-07-30T01:53:21.000Z</StartTime>\n" +
                "    <EndTime>2016-07-30T01:53:31.000Z</EndTime>\n" +
                "    <RemoteAddr>10.101.194.148:56861</RemoteAddr>\n" +
                "  </LiveRecord>\n" +
                "  <LiveRecord>\n" +
                "    <StartTime>2016-07-30T01:53:35.000Z</StartTime>\n" +
                "    <EndTime>2016-07-30T01:53:45.000Z</EndTime>\n" +
                "    <RemoteAddr>10.101.194.148:57126</RemoteAddr>\n" +
                "  </LiveRecord>\n" +
                "</LiveChannelHistory>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<LiveRecord> records = null;
        try {
            records = ResponseParsers.parseGetLiveChannelHistory(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Assertions.assertEquals(2, records.size());
        Assertions.assertEquals("10.101.194.148:56861", records.get(0).getRemoteAddress());
    }

    @Test
    public void testParseListImageStyle() {
        String respBody = "" +
                "<ImageStyle>\n" +
                "  <Style>\n" +
                "    <Name>Name1</Name>\n" +
                "    <Content>Style1</Content>\n" +
                "    <LastModifyTime>Wed, 02 Oct 2019 14:30:18 GMT</LastModifyTime>\n" +
                "    <CreateTime>Wed, 02 Oct 2019 14:30:18 GMT</CreateTime>\n" +
                "  </Style>\n" +
                "  <Style>\n" +
                "    <Name>Name2</Name>\n" +
                "    <Content>Style2</Content>\n" +
                "    <LastModifyTime>Wed, 02 Oct 2019 14:30:18 GMT</LastModifyTime>\n" +
                "    <CreateTime>Wed, 02 Oct 2019 14:30:18 GMT</CreateTime>\n" +
                "  </Style>\n" +
                "</ImageStyle>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<Style> records = null;
        try {
            records = ResponseParsers.parseListImageStyle(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Assertions.assertEquals(2, records.size());
        Assertions.assertEquals("Name1", records.get(0).GetStyleName());
    }

    @Test
    public void testParseBucketImage() {
        String respBody = "" +
                "  <Style>\n" +
                "    <Name>Name</Name>\n" +
                "    <Default404Pic>404Pic</Default404Pic>\n" +
                "    <StyleDelimiters>#</StyleDelimiters>\n" +
                "    <Status>Enable</Status>\n" +
                "    <AutoSetContentType>True</AutoSetContentType>\n" +
                "    <OrigPicForbidden>True</OrigPicForbidden>\n" +
                "    <SetAttachName>True</SetAttachName>\n" +
                "    <UseStyleOnly>True</UseStyleOnly>\n" +
                "    <UseSrcFormat>True</UseSrcFormat>\n" +
                "  </Style>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        GetBucketImageResult result = null;
        try {
            result = ResponseParsers.parseBucketImage(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Assertions.assertEquals("Enable", result.GetStatus());
    }

    @Test
    public void testParseImageStyle() {
        String respBody = "" +
                "  <Style>\n" +
                "    <Name>Name1</Name>\n" +
                "    <Content>Style1</Content>\n" +
                "    <LastModifyTime>Wed, 02 Oct 2019 14:30:18 GMT</LastModifyTime>\n" +
                "    <CreateTime>Wed, 02 Oct 2019 14:30:18 GMT</CreateTime>\n" +
                "  </Style>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        GetImageStyleResult result = null;
        try {
            result = ResponseParsers.parseImageStyle(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Assertions.assertEquals("Name1", result.GetStyleName());
    }

    @Test
    public void testParseGetBucketCname() throws Exception {
        String respBody = "" +
                "<CnameConfiguration>\n" +
                "  <Cname>\n" +
                "    <Domain>Domain1</Domain>\n" +
                "    <Status>Enabled</Status>\n" +
                "    <LastModified>2019-09-30T01:53:45.000Z</LastModified>\n" +
                "    <IsPurgeCdnCache>True</IsPurgeCdnCache>\n" +
                "  </Cname>\n " +
                "  <Cname>\n" +
                "    <Domain>Domain2</Domain>\n" +
                "    <Status>Disabled</Status>\n" +
                "    <LastModified>2019-09-30T01:53:45.000Z</LastModified>\n" +
                "  </Cname>\n" +
                "  <Cname>\n" +
                "    <Domain>Domain3</Domain>\n" +
                "    <Status>Enabled</Status>\n" +
                "    <LastModified>2019-09-30T01:53:45.000Z</LastModified>\n" +
                "    <Certificate>\n" +
                "        <Type>CAS</Type>\n" +
                "        <CertId>hangzhou-01</CertId>\n" +
                "        <Status>Enabled</Status>\n" +
                "    </Certificate>\n" +
                "  </Cname>\n" +
                "  <Cname>\n" +
                "    <Domain>Domain4</Domain>\n" +
                "    <Status>Enabled</Status>\n" +
                "    <LastModified>2019-09-30T01:53:45.000Z</LastModified>\n" +
                "    <Certificate>\n" +
                "        <Type>Upload</Type>\n" +
                "        <Status>Disabled</Status>\n" +
                "    </Certificate>\n" +
                "  </Cname>\n" +
                "</CnameConfiguration>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<CnameConfiguration> result = null;
        try {
            result = ResponseParsers.parseGetBucketCname(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Date date = DateUtil.parseIso8601Date("2019-09-30T01:53:45.000Z");

        Assertions.assertEquals(4, result.size());

        Assertions.assertEquals("Domain1", result.get(0).getDomain());
        Assertions.assertEquals(CnameConfiguration.CnameStatus.Enabled, result.get(0).getStatus());
        Assertions.assertEquals(date, result.get(0).getLastMofiedTime());
        Assertions.assertEquals(new Boolean(true), result.get(0).getPurgeCdnCache());
        Assertions.assertNull(result.get(0).getCertType());

        Assertions.assertEquals("Domain2", result.get(1).getDomain());
        Assertions.assertEquals(CnameConfiguration.CnameStatus.Disabled, result.get(1).getStatus());
        Assertions.assertEquals(date, result.get(1).getLastMofiedTime());
        Assertions.assertNull(result.get(1).getPurgeCdnCache());
        Assertions.assertNull(result.get(1).getCertType());

        Assertions.assertEquals("Domain3", result.get(2).getDomain());
        Assertions.assertEquals(CnameConfiguration.CnameStatus.Enabled, result.get(2).getStatus());
        Assertions.assertEquals(date, result.get(2).getLastMofiedTime());
        Assertions.assertNull(result.get(2).getPurgeCdnCache());
        Assertions.assertEquals(CnameConfiguration.CertType.CAS, result.get(2).getCertType());
        Assertions.assertEquals("hangzhou-01", result.get(2).getCertId());
        Assertions.assertEquals(CnameConfiguration.CertStatus.Enabled, result.get(2).getCertStatus());

        Assertions.assertEquals("Domain4", result.get(3).getDomain());
        Assertions.assertEquals(CnameConfiguration.CnameStatus.Enabled, result.get(3).getStatus());
        Assertions.assertEquals(date, result.get(3).getLastMofiedTime());
        Assertions.assertNull(result.get(3).getPurgeCdnCache());
        Assertions.assertEquals(CnameConfiguration.CertType.Upload, result.get(3).getCertType());
        Assertions.assertNull(result.get(3).getCertId());
        Assertions.assertEquals(CnameConfiguration.CertStatus.Disabled, result.get(3).getCertStatus());
    }

    @Test
    public void testParseGetBucketInfoBasic() {
        String respBody = "" +
                "<BucketInfo>\n" +
                "  <Bucket>\n" +
                "           <CreationDate>2013-07-31T10:56:21.000Z</CreationDate>\n" +
                "            <ExtranetEndpoint>oss-cn-hangzhou.aliyuncs.com</ExtranetEndpoint>\n" +
                "            <IntranetEndpoint>oss-cn-hangzhou-internal.aliyuncs.com</IntranetEndpoint>\n" +
                "            <Location>oss-cn-hangzhou</Location>\n" +
                "            <Name>oss-example</Name>\n" +
                "            <Owner>\n" +
                "              <DisplayName>username</DisplayName>\n" +
                "              <ID>27183473914****</ID>\n" +
                "            </Owner>\n" +
                "            <AccessControlList>\n" +
                "              <Grant>private</Grant>\n" +
                "            </AccessControlList>\n" +
                "          </Bucket>\n" +
                " </BucketInfo>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        BucketInfo result = null;
        try {
            result = ResponseParsers.parseGetBucketInfo(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Assertions.assertEquals(CannedAccessControlList.Private, result.getCannedACL());
        Assertions.assertEquals("oss-cn-hangzhou", result.getBucket().getLocation());
        Assertions.assertEquals("oss-example", result.getBucket().getName());
    }

    @Test
    public void testParseGetBucketInfo() {
        String respBody = "" +
                "<BucketInfo>\n" +
                "  <Bucket>\n" +
                "           <CreationDate>2013-07-31T10:56:21.000Z</CreationDate>\n" +
                "            <ExtranetEndpoint>oss-cn-hangzhou.aliyuncs.com</ExtranetEndpoint>\n" +
                "            <IntranetEndpoint>oss-cn-hangzhou-internal.aliyuncs.com</IntranetEndpoint>\n" +
                "            <Location>oss-cn-hangzhou</Location>\n" +
                "            <Name>oss-example</Name>\n" +
                "            <Owner>\n" +
                "              <DisplayName>username</DisplayName>\n" +
                "              <ID>27183473914****</ID>\n" +
                "            </Owner>\n" +
                "            <AccessControlList>\n" +
                "              <Grant>private</Grant>\n" +
                "            </AccessControlList>\n" +
                "            <Comment>test</Comment>\n" +
                "            <DataRedundancyType>LRS</DataRedundancyType>\n" +
                "          </Bucket>\n" +
                " </BucketInfo>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        BucketInfo result = null;
        try {
            result = ResponseParsers.parseGetBucketInfo(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Assertions.assertEquals("test", result.getComment());
        Assertions.assertEquals(DataRedundancyType.LRS, result.getDataRedundancyType());
        Assertions.assertEquals(CannedAccessControlList.Private, result.getCannedACL());
        Assertions.assertEquals("oss-cn-hangzhou", result.getBucket().getLocation());
        Assertions.assertEquals("oss-example", result.getBucket().getName());
        Assertions.assertEquals(null, result.getBucket().getHnsStatus());
        Assertions.assertEquals(null, result.getBucket().getResourceGroupId());

        respBody = "" +
                "<BucketInfo>\n" +
                "  <Bucket>\n" +
                "           <CreationDate>2013-07-31T10:56:21.000Z</CreationDate>\n" +
                "            <ExtranetEndpoint>oss-cn-hangzhou.aliyuncs.com</ExtranetEndpoint>\n" +
                "            <HierarchicalNamespace>Enabled</HierarchicalNamespace>\n" +
                "            <IntranetEndpoint>oss-cn-hangzhou-internal.aliyuncs.com</IntranetEndpoint>\n" +
                "            <Location>oss-cn-hangzhou</Location>\n" +
                "            <ResourceGroupId>xxx-id-123</ResourceGroupId>\n" +
                "            <Name>oss-example</Name>\n" +
                "            <Owner>\n" +
                "              <DisplayName>username</DisplayName>\n" +
                "              <ID>27183473914****</ID>\n" +
                "            </Owner>\n" +
                "            <AccessControlList>\n" +
                "              <Grant>private</Grant>\n" +
                "            </AccessControlList>\n" +
                "            <Comment>test</Comment>\n" +
                "            <DataRedundancyType>LRS</DataRedundancyType>\n" +
                "          </Bucket>\n" +
                " </BucketInfo>";

        instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        result = null;
        try {
            result = ResponseParsers.parseGetBucketInfo(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Assertions.assertEquals("test", result.getComment());
        Assertions.assertEquals(DataRedundancyType.LRS, result.getDataRedundancyType());
        Assertions.assertEquals(CannedAccessControlList.Private, result.getCannedACL());
        Assertions.assertEquals("oss-cn-hangzhou", result.getBucket().getLocation());
        Assertions.assertEquals("oss-example", result.getBucket().getName());
        Assertions.assertEquals(HnsStatus.Enabled.toString(), result.getBucket().getHnsStatus());
        Assertions.assertEquals("xxx-id-123", result.getBucket().getResourceGroupId());
    }

    @Test
    public void testParseListVpcip() {
        String respBody = "" +
                "<ListVpcipResult>\n" +
                "  <Vpcip>\n" +
                "           <Region>test-region-1</Region>\n" +
                "            <VpcId>test-vpcid-1</VpcId>\n" +
                "            <Vip>test-vip-1</Vip>\n" +
                "            <Label>test-label-1</Label>\n" +
                " </Vpcip>\n" +
                "  <Vpcip>\n" +
                "           <Region>test-region-2</Region>\n" +
                "            <VpcId>test-vpcid-2</VpcId>\n" +
                "            <Vip>test-vip-2</Vip>\n" +
                "            <Label>test-label-2</Label>\n" +
                " </Vpcip>\n" +
                "</ListVpcipResult>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<Vpcip>  result = null;
        try {
            result = ResponseParsers.parseListVpcipResult(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Assertions.assertEquals(2, result.size());
        for (Vpcip v: result) {
            Assertions.assertTrue(v.getRegion().startsWith("test-region-"));
            Assertions.assertTrue(v.getVpcId().startsWith("test-vpcid-"));
            Assertions.assertTrue(v.getVip().startsWith("test-vip-"));
            Assertions.assertTrue(v.getLabel().startsWith("test-label-"));
            Assertions.assertTrue(v.toString().contains("test-region-"));
        }
    }

    @Test
    public void testParseGetBucketVpcip() {
        String respBody = "" +
                "<ListVpcPolicyResult>\n" +
                "  <Vpcip>\n" +
                "           <Region>test-region-1</Region>\n" +
                "            <VpcId>test-vpcid-1</VpcId>\n" +
                "            <Vip>test-vip-1</Vip>\n" +
                " </Vpcip>\n" +
                "  <Vpcip>\n" +
                "           <Region>test-region-2</Region>\n" +
                "            <VpcId>test-vpcid-2</VpcId>\n" +
                "            <Vip>test-vip-2</Vip>\n" +
                " </Vpcip>\n" +
                "</ListVpcPolicyResult>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        List<VpcPolicy>  result = null;
        try {
            result = ResponseParsers.parseListVpcPolicyResult(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Assertions.assertEquals(2, result.size());
        for (VpcPolicy v: result) {
            Assertions.assertTrue(v.getRegion().startsWith("test-region-"));
            Assertions.assertTrue(v.getVpcId().startsWith("test-vpcid-"));
            Assertions.assertTrue(v.getVip().startsWith("test-vip-"));
        }
    }

    @Test
    public void testparseGetCreateVpcipResult() {
        String respBody = "" +
                "<Vpcip>\n" +
                "     <Region>test-region-1</Region>\n" +
                "     <VpcId>test-vpcid-1</VpcId>\n" +
                "     <Vip>test-vip-1</Vip>\n" +
                "     <Label>test-label-1</Label>\n" +
                "</Vpcip>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        Vpcip vpcip = null;
        try {
            vpcip = ResponseParsers.parseGetCreateVpcipResult(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse bucket replication response body fail!");
        }

        Assertions.assertTrue(vpcip.getRegion().startsWith("test-region-"));
        Assertions.assertTrue(vpcip.getVpcId().startsWith("test-vpcid-"));
        Assertions.assertTrue(vpcip.getVip().startsWith("test-vip-"));
        Assertions.assertTrue(vpcip.getLabel().startsWith("test-label-"));
        Assertions.assertTrue(vpcip.toString().contains("test-region-"));

        // test error body
        try {
            instream = new ByteArrayInputStream((respBody + "error-body").getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetCreateVpcipResult(instream);
        } catch (ResponseParseException e) {
           // expected exception.
        }
    }

    @Test
    public void testListImageStyleResponseParser() {
        String respBody = null;
        InputStream instream = null;

        Date dt = new Date();
        DateUtil.formatIso8601Date(dt);

        respBody = "" +
            "<ListImageStyleResult>\n" +
            "  <Style>\n" +
            "    <Name>name1</Name>\n" +
            "    <Content>content1</Content>\n" +
            "    <LastModifyTime>"+ DateUtil.formatRfc822Date(dt) + "</LastModifyTime>\n" +
            "    <CreateTime>"+ DateUtil.formatRfc822Date(dt) + "</CreateTime>\n" +
            "  </Style>\n" +
            "  <Style>\n" +
            "    <Name>name2</Name>\n" +
            "    <Content>content2</Content>\n" +
            "    <LastModifyTime>" + DateUtil.formatRfc822Date(dt) + "</LastModifyTime>\n" +
            "    <CreateTime>"+ DateUtil.formatRfc822Date(dt) + "</CreateTime>\n" +
            "  </Style>\n" +
            "</ListImageStyleResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.ListImageStyleResponseParser parser = new ResponseParsers.ListImageStyleResponseParser();
            List<Style> value = parser.parse(responseMessage);
            Assertions.assertEquals(value.size(), 2);
            Assertions.assertEquals(value.get(0).GetStyleName(), "name1");
        } catch (ResponseParseException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        respBody = "invalid xml";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.ListImageStyleResponseParser parser = new ResponseParsers.ListImageStyleResponseParser();
            List<Style> value = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }

        respBody = "" +
                "<ListImageStyleResult>\n" +
                "  <Style>\n" +
                "    <Name>name1</Name>\n" +
                "    <Content>content1</Content>\n" +
                "    <LastModifyTime>invalid</LastModifyTime>\n" +
                "    <CreateTime>"+ DateUtil.formatRfc822Date(dt) + "</CreateTime>\n" +
                "  </Style>\n" +
                "</ListImageStyleResult>";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.ListImageStyleResponseParser parser = new ResponseParsers.ListImageStyleResponseParser();
            List<Style> value = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testGetBucketImageResponseParser() {
        String respBody = null;
        InputStream instream = null;

        respBody = "" +
                "<ListImageStyleResult>\n" +
                "    <Name>name</Name>\n" +
                "    <Default404Pic>default</Default404Pic>\n" +
                "    <StyleDelimiters>value</StyleDelimiters>\n" +
                "    <Status>Status</Status>\n" +
                "    <AutoSetContentType>True</AutoSetContentType>\n" +
                "    <OrigPicForbidden>True</OrigPicForbidden>\n" +
                "    <SetAttachName>True</SetAttachName>\n" +
                "    <UseStyleOnly>True</UseStyleOnly>\n" +
                "    <UseSrcFormat>True</UseSrcFormat>\n" +
                "</ListImageStyleResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketImageResponseParser parser = new ResponseParsers.GetBucketImageResponseParser();
            GetBucketImageResult result = parser.parse(responseMessage);
            Assertions.assertEquals(result.GetBucketName(), "name");
        } catch (ResponseParseException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        respBody = "invalid xml";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketImageResponseParser parser = new ResponseParsers.GetBucketImageResponseParser();
            GetBucketImageResult result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }

        respBody = "" +
                "<ListImageStyleResult>\n" +
                "    <Name>name</Name>\n" +
                "    <Default404Pic>default</Default404Pic>\n" +
                "    <StyleDelimiters>value</StyleDelimiters>\n" +
                "    <Status>Status</Status>\n" +
                "    <OrigPicForbidden>True</OrigPicForbidden>\n" +
                "    <SetAttachName>True</SetAttachName>\n" +
                "    <UseStyleOnly>True</UseStyleOnly>\n" +
                "    <UseSrcFormat>True</UseSrcFormat>\n" +
                "</ListImageStyleResult>";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketImageResponseParser parser = new ResponseParsers.GetBucketImageResponseParser();
            GetBucketImageResult result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testGetImageStyleResponseParser() {
        String respBody = null;
        InputStream instream = null;
        Date dt = new Date();
        DateUtil.formatIso8601Date(dt);

        respBody = "" +
                "  <Style>\n" +
                "    <Name>name</Name>\n" +
                "    <Content>content</Content>\n" +
                "    <LastModifyTime>" + DateUtil.formatRfc822Date(dt) + "</LastModifyTime>\n" +
                "    <CreateTime>"+ DateUtil.formatRfc822Date(dt) + "</CreateTime>\n" +
                "  </Style>\n";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetImageStyleResponseParser parser = new ResponseParsers.GetImageStyleResponseParser();
            GetImageStyleResult result = parser.parse(responseMessage);
            Assertions.assertEquals(result.GetStyleName(), "name");
        } catch (ResponseParseException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        respBody = "invalid xml";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetImageStyleResponseParser parser = new ResponseParsers.GetImageStyleResponseParser();
            GetImageStyleResult result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }

        respBody = "" +
                "  <Style>\n" +
                "    <Name>name1</Name>\n" +
                "    <Content>content1</Content>\n" +
                "    <LastModifyTime>invalid</LastModifyTime>\n" +
                "    <CreateTime>"+ DateUtil.formatIso8601Date(dt) + "</CreateTime>\n" +
                "  </Style>\n";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetImageStyleResponseParser parser = new ResponseParsers.GetImageStyleResponseParser();
            GetImageStyleResult result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testGetBucketCnameResponseParser() {
        String respBody = null;
        InputStream instream = null;
        Date dt = new Date();
        DateUtil.formatIso8601Date(dt);

        respBody = "" +
                "<CnameConfigurationListResult>\n" +
                "  <Cname>\n" +
                "    <Domain>name</Domain>\n" +
                "    <Status>Enabled</Status>\n" +
                "    <LastModified>" + DateUtil.formatIso8601Date(dt) + "</LastModified>\n" +
                "    <IsPurgeCdnCache>True</IsPurgeCdnCache>\n" +
                "  </Cname>\n" +
                "  <Cname>\n" +
                "    <Domain>name1</Domain>\n" +
                "    <Status>Disabled</Status>\n" +
                "    <LastModified>" + DateUtil.formatIso8601Date(dt) + "</LastModified>\n" +
                "  </Cname>\n" +
                "</CnameConfigurationListResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketCnameResponseParser parser = new ResponseParsers.GetBucketCnameResponseParser();
            List<CnameConfiguration> result = parser.parse(responseMessage);
            Assertions.assertEquals(result.size(), 2);
            Assertions.assertEquals(result.get(0).getDomain(), "name");
        } catch (ResponseParseException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        respBody = "invalid xml";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketCnameResponseParser parser = new ResponseParsers.GetBucketCnameResponseParser();
            List<CnameConfiguration> result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }

        respBody = "" +
                "<CnameConfigurationListResult>\n" +
                "  <Cname>\n" +
                "    <Domain>name</Domain>\n" +
                "    <Status>Enabled</Status>\n" +
                "    <LastModified>" + DateUtil.formatRfc822Date(dt) + "</LastModified>\n" +
                "    <IsPurgeCdnCache>True</IsPurgeCdnCache>\n" +
                "  </Cname>\n" +
                "</CnameConfigurationListResult>";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketCnameResponseParser parser = new ResponseParsers.GetBucketCnameResponseParser();
            List<CnameConfiguration> result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testGetBucketReplicationResponseParser() {
        String respBody = null;
        InputStream instream = null;

        respBody = "" +
                "<ReplicationConfiguration>\n" +
                "  <Rule>\n" +
                "    <ID>name</ID>\n" +
                "    <PrefixSet>\n" +
                "       <Prefix>source_image</Prefix>\n" +
                "       <Prefix>video</Prefix>\n" +
                "    </PrefixSet>\n" +
                "    <Action>PUT</Action>\n" +
                "    <Destination>\n" +
                "       <Bucket>target-bucket</Bucket>\n" +
                "       <Location>oss-cn-beijing</Location>\n" +
                "    </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                "    <EncryptionConfiguration>\n" +
                "       <ReplicaKmsKeyID>kmsid</ReplicaKmsKeyID>\n" +
                "    </EncryptionConfiguration>\n" +
                "    <SourceSelectionCriteria>\n" +
                "        <SseKmsEncryptedObjects>\n" +
                "           <Status>status</Status>\n" +
                "        </SseKmsEncryptedObjects>\n" +
                "    </SourceSelectionCriteria>\n" +
                "  </Rule>\n" +
                "  <Rule>\n" +
                "    <ID>name2</ID>\n" +
                "    <Destination>\n" +
                "       <Bucket>target-bucket</Bucket>\n" +
                "       <Location>oss-cn-beijing</Location>\n" +
                "    </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>disable</HistoricalObjectReplication>\n" +
                "    <SourceSelectionCriteria>\n" +
                "    </SourceSelectionCriteria>\n" +
                "  </Rule>\n" +
                "  <Rule>\n" +
                "    <ID>name3</ID>\n" +
                "    <Destination>\n" +
                "       <Bucket>target-bucket</Bucket>\n" +
                "       <Location>oss-cn-beijing</Location>\n" +
                "    </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>disable</HistoricalObjectReplication>\n" +
                "  </Rule>\n" +
                "</ReplicationConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketReplicationResponseParser parser = new ResponseParsers.GetBucketReplicationResponseParser();
            List<ReplicationRule> result = parser.parse(responseMessage);
            Assertions.assertEquals(result.size(), 3);
            Assertions.assertEquals(result.get(0).getReplicationRuleID(), "name");
        } catch (ResponseParseException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        respBody = "invalid xml";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketReplicationResponseParser parser = new ResponseParsers.GetBucketReplicationResponseParser();
            List<ReplicationRule> result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }

        respBody = "" +
                "<ReplicationConfiguration>\n" +
                "  <Rule>\n" +
                "    <ID>name3</ID>\n" +
                "    <Destination>\n" +
                "       <Bucket>target-bucket</Bucket>\n" +
                "       <Location>oss-cn-beijing</Location>\n" +
                "    </Destination>\n" +
                "    <Status>invalid</Status>\n" +
                "    <HistoricalObjectReplication>disable</HistoricalObjectReplication>\n" +
                "  </Rule>\n" +
                "</ReplicationConfiguration>";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketReplicationResponseParser parser = new ResponseParsers.GetBucketReplicationResponseParser();
            List<ReplicationRule> result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testGetBucketReplicationProgressResponseParser() {
        String respBody = null;
        InputStream instream = null;

        respBody = "" +
                "<ReplicationProgress>\n" +
                "  <Rule>\n" +
                "    <ID>name</ID>\n" +
                "    <Destination>\n" +
                "       <Bucket>target-bucket</Bucket>\n" +
                "       <Location>oss-cn-beijing</Location>\n" +
                "    </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>disable</HistoricalObjectReplication>\n" +
                "    <Progress>\n" +
                "       <HistoricalObject>0.85</HistoricalObject>\n" +
                "       <NewObject>2015-09-24T15:28:14.000Z</NewObject>\n" +
                "    </Progress>\n" +
                "  </Rule>\n" +
                "</ReplicationProgress>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketReplicationProgressResponseParser parser = new ResponseParsers.GetBucketReplicationProgressResponseParser();
            BucketReplicationProgress result = parser.parse(responseMessage);
            Assertions.assertEquals(result.getReplicationRuleID(), "name");
        } catch (ResponseParseException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        respBody = "" +
                "<ReplicationProgress>\n" +
                "  <Rule>\n" +
                "    <ID>name</ID>\n" +
                "    <Destination>\n" +
                "       <Bucket>target-bucket</Bucket>\n" +
                "       <Location>oss-cn-beijing</Location>\n" +
                "    </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>enabled</HistoricalObjectReplication>\n" +
                "  </Rule>\n" +
                "</ReplicationProgress>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketReplicationProgressResponseParser parser = new ResponseParsers.GetBucketReplicationProgressResponseParser();
            BucketReplicationProgress result = parser.parse(responseMessage);
            Assertions.assertEquals(result.getReplicationRuleID(), "name");
        } catch (ResponseParseException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        respBody = "invalid xml";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketReplicationProgressResponseParser parser = new ResponseParsers.GetBucketReplicationProgressResponseParser();
            BucketReplicationProgress result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }

        respBody = "" +
                "<ReplicationProgress>\n" +
                "  <Rule>\n" +
                "    <ID>name</ID>\n" +
                "    <Destination>\n" +
                "       <Bucket>target-bucket</Bucket>\n" +
                "       <Location>oss-cn-beijing</Location>\n" +
                "    </Destination>\n" +
                "    <Status>doing</Status>\n" +
                "    <HistoricalObjectReplication>disable</HistoricalObjectReplication>\n" +
                "    <Progress>\n" +
                "       <NewObject>invalid</NewObject>\n" +
                "    </Progress>\n" +
                "  </Rule>\n" +
                "</ReplicationProgress>";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketReplicationProgressResponseParser parser = new ResponseParsers.GetBucketReplicationProgressResponseParser();
            BucketReplicationProgress result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testGetBucketReplicationLocationResponseParser() {
        String respBody = null;
        InputStream instream = null;

        respBody = "" +
                "<ReplicationLocation>\n" +
                "  <Location>oss-cn-beijing</Location>\n" +
                "  <Location>oss-cn-shenzhen</Location>\n" +
                "</ReplicationLocation>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketReplicationLocationResponseParser parser = new ResponseParsers.GetBucketReplicationLocationResponseParser();
            List<String> result = parser.parse(responseMessage);
            Assertions.assertEquals(result.size(), 2);
            Assertions.assertEquals(result.get(0), "oss-cn-beijing");
        } catch (ResponseParseException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        respBody = "invalid xml";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(instream);
            ResponseParsers.GetBucketReplicationLocationResponseParser parser = new ResponseParsers.GetBucketReplicationLocationResponseParser();
            List<String> result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            responseMessage.setContent(null);
            ResponseParsers.GetBucketReplicationLocationResponseParser parser = new ResponseParsers.GetBucketReplicationLocationResponseParser();
            List<String> result = parser.parse(responseMessage);
            Assertions.fail("should not here");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testAppendObjectResponseParser() {
        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            Map<String, String> headers = new HashMap<String, String>();
            responseMessage.setHeaders(headers);
            ResponseParsers.AppendObjectResponseParser parser = new ResponseParsers.AppendObjectResponseParser();
            AppendObjectResult result = parser.parse(responseMessage);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testGetObjectResponseParser() {
        try {
            ResponseMessage responseMessage = new ResponseMessage(null);;
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Last-Modified", "invalid");
            responseMessage.setHeaders(headers);
            ResponseParsers.GetObjectResponseParser parser = new ResponseParsers.GetObjectResponseParser("bucket", "key");
            OSSObject result = parser.parse(responseMessage);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testDeleteVersionsResponseParser() {
        InputStream instream = null;

        try {
            instream = new ByteArrayInputStream("".getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage(null);
            responseMessage.setContent(null);
            responseMessage.setContentLength(0);
            ResponseParsers.DeleteVersionsResponseParser parser = new ResponseParsers.DeleteVersionsResponseParser();
            DeleteVersionsResult result = parser.parse(responseMessage);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseListObjects() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<ListBucketResult>\n" +
                "  <Name>oss-example</Name>\n" +
                "  <MaxKeys>100</MaxKeys>\n" +
                "  <IsTruncated>false</IsTruncated>\n" +
                "  <Prefix></Prefix>\n" +
                "  <Marker></Marker>\n" +
                "  <Delimiter></Delimiter>\n" +
                "  <NextMarker></NextMarker>\n" +
                "  <EncodingType></EncodingType>\n" +
                "  <CommonPrefixes>\n" +
                "    <Prefix></Prefix>\n" +
                "  </CommonPrefixes>\n" +
                "</ListBucketResult>";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ObjectListing result = ResponseParsers.parseListObjects(instream);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<ListBucketResult>\n" +
                "  <Name>oss-example</Name>\n" +
                "  <MaxKeys>100</MaxKeys>\n" +
                "  <IsTruncated>false</IsTruncated>\n" +
                "</ListBucketResult>";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ObjectListing result = ResponseParsers.parseListObjects(instream);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ObjectListing result = ResponseParsers.parseListObjects(null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testparseListVersions() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<ListBucketResult>\n" +
                "  <Name>oss-example</Name>\n" +
                "  <MaxKeys>100</MaxKeys>\n" +
                "  <IsTruncated>false</IsTruncated>\n" +
                "  <Prefix></Prefix>\n" +
                "  <Marker></Marker>\n" +
                "  <Delimiter></Delimiter>\n" +
                "  <NextMarker></NextMarker>\n" +
                "  <NextKeyMarker></NextKeyMarker>\n" +
                "  <EncodingType>invalid</EncodingType>\n" +
                "  <VersionIdMarker></VersionIdMarker>\n" +
                "  <NextVersionIdMarker></NextVersionIdMarker>\n" +
                "  <CommonPrefixes>\n" +
                "    <Prefix></Prefix>\n" +
                "  </CommonPrefixes>\n" +
                "</ListBucketResult>";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            VersionListing result = ResponseParsers.parseListVersions(instream);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<ListBucketResult>\n" +
                "  <Name>oss-example</Name>\n" +
                "  <MaxKeys>100</MaxKeys>\n" +
                "  <IsTruncated>false</IsTruncated>\n" +
                "  <Prefix></Prefix>\n" +
                "  <Marker></Marker>\n" +
                "  <Delimiter></Delimiter>\n" +
                "  <NextMarker></NextMarker>\n" +
                "  <NextKeyMarker></NextKeyMarker>\n" +
                "  <EncodingType></EncodingType>\n" +
                "  <VersionIdMarker></VersionIdMarker>\n" +
                "  <NextVersionIdMarker></NextVersionIdMarker>\n" +
                "  <CommonPrefixes>\n" +
                "    <Prefix></Prefix>\n" +
                "  </CommonPrefixes>\n" +
                "</ListBucketResult>";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            VersionListing result = ResponseParsers.parseListVersions(instream);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<ListBucketResult>\n" +
                "  <Name>oss-example</Name>\n" +
                "  <MaxKeys>100</MaxKeys>\n" +
                "  <IsTruncated>false</IsTruncated>\n" +
                "</ListBucketResult>";
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            VersionListing result = ResponseParsers.parseListVersions(instream);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            VersionListing result = ResponseParsers.parseListVersions(null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testparseGetBucketAcl() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            AccessControlList result = ResponseParsers.parseGetBucketAcl(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            AccessControlList result = ResponseParsers.parseGetBucketAcl(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetObjectAcl() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ObjectAcl result = ResponseParsers.parseGetObjectAcl(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ObjectAcl result = ResponseParsers.parseGetObjectAcl(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketReferer() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<RefererConfiguration>\n" +
                "  <AllowEmptyReferer>true</AllowEmptyReferer>\n" +
                "  <RefererList>\n" +
                "  </RefererList>\n" +
                "</RefererConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            BucketReferer result = ResponseParsers.parseGetBucketReferer(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<RefererConfiguration>\n" +
                "  <AllowEmptyReferer>true</AllowEmptyReferer>\n" +
                "</RefererConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            BucketReferer result = ResponseParsers.parseGetBucketReferer(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            BucketReferer result = ResponseParsers.parseGetBucketReferer(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            BucketReferer result = ResponseParsers.parseGetBucketReferer(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }
    @Test
    public void testparseUploadPartCopy() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseUploadPartCopy(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseUploadPartCopy(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseListBucket() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<ListAllMyBucketsResult>\n" +
                "  <MaxKeys></MaxKeys>\n" +
                "  <IsTruncated></IsTruncated>\n" +
                "  <AllowEmptyReferer>true</AllowEmptyReferer>\n" +
                "  <Owner>\n" +
                "    <ID></ID>\n" +
                "    <DisplayName></DisplayName>\n" +
                "  </Owner>\n" +
                "</ListAllMyBucketsResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListBucket(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<ListAllMyBucketsResult>\n" +
                "  <MaxKeys>10</MaxKeys>\n" +
                "  <IsTruncated>true</IsTruncated>\n" +
                "  <AllowEmptyReferer>true</AllowEmptyReferer>\n" +
                "  <Owner>\n" +
                "    <ID></ID>\n" +
                "    <DisplayName></DisplayName>\n" +
                "  </Owner>\n" +
                "</ListAllMyBucketsResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListBucket(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListBucket(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseListBucket(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        //Parser Region
        respBody = "" +
                "<ListAllMyBucketsResult>\n" +
                "  <MaxKeys>10</MaxKeys>\n" +
                "  <IsTruncated>true</IsTruncated>\n" +
                "  <AllowEmptyReferer>true</AllowEmptyReferer>\n" +
                "  <Owner>\n" +
                "    <ID>id</ID>\n" +
                "    <DisplayName>name</DisplayName>\n" +
                "  </Owner>\n" +
                "  <Buckets>\n" +
                "    <Bucket>\n" +
                "      <Comment></Comment>\n" +
                "      <CreationDate>2020-03-12T02:18:25.000Z</CreationDate>\n" +
                "      <ExtranetEndpoint>oss-cn-hangzhou.aliyuncs.com</ExtranetEndpoint>\n" +
                "      <IntranetEndpoint>oss-cn-hangzhou-internal.aliyuncs.com</IntranetEndpoint>\n" +
                "      <Location>oss-cn-hangzhou</Location>\n" +
                "      <Name>oss-bucket</Name>\n" +
                "      <Region>cn-hangzhou</Region>\n" +
                "      <StorageClass>Standard</StorageClass>\n" +
                "    </Bucket>\n" +
                "  </Buckets>\n" +
                "</ListAllMyBucketsResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            BucketList result = ResponseParsers.parseListBucket(instream);
            Assertions.assertEquals(result.getBucketList().size(), 1);
            Assertions.assertEquals(result.getBucketList().get(0).getLocation(), "oss-cn-hangzhou");
            Assertions.assertEquals(result.getBucketList().get(0).getRegion(), "cn-hangzhou");
            Assertions.assertEquals(result.getBucketList().get(0).getHnsStatus(), null);
            Assertions.assertEquals(result.getBucketList().get(0).getResourceGroupId(), null);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        //Parser Region
        respBody = "" +
                "<ListAllMyBucketsResult>\n" +
                "  <MaxKeys>10</MaxKeys>\n" +
                "  <IsTruncated>true</IsTruncated>\n" +
                "  <AllowEmptyReferer>true</AllowEmptyReferer>\n" +
                "  <Owner>\n" +
                "    <ID>id</ID>\n" +
                "    <DisplayName>name</DisplayName>\n" +
                "  </Owner>\n" +
                "  <Buckets>\n" +
                "    <Bucket>\n" +
                "      <Comment></Comment>\n" +
                "      <CreationDate>2020-03-12T02:18:25.000Z</CreationDate>\n" +
                "      <ExtranetEndpoint>oss-cn-hangzhou.aliyuncs.com</ExtranetEndpoint>\n" +
                "      <IntranetEndpoint>oss-cn-hangzhou-internal.aliyuncs.com</IntranetEndpoint>\n" +
                "      <Location>oss-cn-hangzhou</Location>\n" +
                "      <Name>oss-bucket</Name>\n" +
                "      <HierarchicalNamespace>status</HierarchicalNamespace>\n" +
                "      <ResourceGroupId>xxx-id-123</ResourceGroupId>\n" +
                "      <StorageClass>Standard</StorageClass>\n" +
                "    </Bucket>\n" +
                "  </Buckets>\n" +
                "</ListAllMyBucketsResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            BucketList result = ResponseParsers.parseListBucket(instream);
            Assertions.assertEquals(result.getBucketList().size(), 1);
            Assertions.assertEquals(result.getBucketList().get(0).getLocation(), "oss-cn-hangzhou");
            Assertions.assertEquals(result.getBucketList().get(0).getRegion(), null);
            Assertions.assertEquals(result.getBucketList().get(0).getHnsStatus(), "status");
            Assertions.assertEquals(result.getBucketList().get(0).getResourceGroupId(), "xxx-id-123");
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListBucket(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseListImageStyle() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListImageStyle(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseListImageStyle(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketLocation() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketLocation(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetBucketLocation(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseBucketMetadata() {

        try {
            ResponseParsers.parseBucketMetadata(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseSimplifiedObjectMeta() {

        try {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Last-Modified", "invalid");
            ResponseParsers.parseSimplifiedObjectMeta(headers);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseSimplifiedObjectMeta(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseSymbolicLink() {

        try {
            ResponseMessage response = new ResponseMessage(null);
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Last-Modified", "invalid");
            response.setHeaders(headers);
            ResponseParsers.parseSymbolicLink(response);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setHeaders(null);
            ResponseParsers.parseSymbolicLink(response);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseInitiateMultipartUpload() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<InitiateMultipartUploadResult>\n" +
                "</InitiateMultipartUploadResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseInitiateMultipartUpload(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseInitiateMultipartUpload(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseInitiateMultipartUpload(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseListMultipartUploads() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<ListMultipartUploadsResult>\n" +
                "  <Bucket></Bucket>\n" +
                "  <MaxUploads>100</MaxUploads>\n" +
                "  <IsTruncated>true</IsTruncated>\n" +
                "  <KeyMarker>marker</KeyMarker>\n" +
                "  <UploadIdMarker>idmarker</UploadIdMarker>\n" +
                "  <Upload>\n" +
                "    <Key></Key>\n" +
                "  </Upload>\n" +
                "  <CommonPrefixes>\n" +
                "    <Prefix></Prefix>\n" +
                "  </CommonPrefixes>\n" +
                "</ListMultipartUploadsResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListMultipartUploads(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<ListMultipartUploadsResult>\n" +
                "  <Bucket></Bucket>\n" +
                "  <MaxUploads>100</MaxUploads>\n" +
                "  <IsTruncated>true</IsTruncated>\n" +
                "</ListMultipartUploadsResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListMultipartUploads(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListMultipartUploads(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseListMultipartUploads(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseListParts() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<ListPartsResult>\n" +
                "<StorageClass>Standard</StorageClass>\n" +
                "<MaxParts>100</MaxParts>\n" +
                "<IsTruncated>true</IsTruncated>\n" +
                "<PartNumberMarker></PartNumberMarker>\n" +
                "<PartNumberMarker></PartNumberMarker>\n" +
                "</ListPartsResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListParts(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListParts(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseListParts(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseCompleteMultipartUpload() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseCompleteMultipartUpload(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseCompleteMultipartUpload(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseBucketLogging() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<BucketLogging>\n" +
                "</BucketLogging>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseBucketLogging(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }


        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseBucketLogging(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseBucketLogging(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }


    @Test
    public void testparseGetBucketImageProcessConf() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<BucketImageProcessConf>\n" +
                "<SourceFileProtect>Enabled</SourceFileProtect>\n" +
                "</BucketImageProcessConf>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketImageProcessConf(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }


        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketImageProcessConf(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetBucketImageProcessConf(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseBucketWebsite() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<WebsiteConfiguration>\n" +
                "  <RoutingRules>\n" +
                "    <RoutingRule>\n" +
                "      <RuleNumber>1</RuleNumber>\n" +
                "      <Condition>\n" +
                "        <KeyPrefixEquals>prefix</KeyPrefixEquals>\n" +
                "      </Condition>\n" +
                "      <Redirect>\n" +
                "      </Redirect>\n" +
                "      <HostName>HostName</HostName>\n" +
                "      <ReplaceKeyPrefixWith>prefix</ReplaceKeyPrefixWith>\n" +
                "      <ReplaceKeyWith>prefix</ReplaceKeyWith>\n" +
                "      <MirrorURL>prefix</MirrorURL>\n" +
                "      <MirrorURLSlave>prefix</MirrorURLSlave>\n" +
                "      <MirrorURLProbe>prefix</MirrorURLProbe>\n" +
                "      <MirrorPassQueryString>prefix</MirrorPassQueryString>\n" +
                "      <MirrorPassOriginalSlashes>prefix</MirrorPassOriginalSlashes>\n" +
                "    </RoutingRule>\n" +
                "    <RoutingRule>\n" +
                "      <RuleNumber>2</RuleNumber>\n" +
                "      <Redirect>\n" +
                "      </Redirect>\n" +
                "      <HostName>HostName</HostName>\n" +
                "      <ReplaceKeyPrefixWith>prefix</ReplaceKeyPrefixWith>\n" +
                "      <ReplaceKeyWith>prefix</ReplaceKeyWith>\n" +
                "      <MirrorURL>prefix</MirrorURL>\n" +
                "      <MirrorURLSlave>prefix</MirrorURLSlave>\n" +
                "      <MirrorURLProbe>prefix</MirrorURLProbe>\n" +
                "      <MirrorPassQueryString>prefix</MirrorPassQueryString>\n" +
                "      <MirrorPassOriginalSlashes>prefix</MirrorPassOriginalSlashes>\n" +
                "    </RoutingRule>\n" +
                "  </RoutingRules>\n" +
                "</WebsiteConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseBucketWebsite(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<WebsiteConfiguration>\n" +
                "  <RoutingRules>\n" +
                "    <RoutingRule>\n" +
                "      <RuleNumber>2</RuleNumber>\n" +
                "      <Redirect>\n" +
                "      </Redirect>\n" +
                "      <HostName>HostName</HostName>\n" +
                "      <ReplaceKeyPrefixWith>prefix</ReplaceKeyPrefixWith>\n" +
                "      <ReplaceKeyWith>prefix</ReplaceKeyWith>\n" +
                "      <MirrorURL>prefix</MirrorURL>\n" +
                "      <MirrorURLSlave>prefix</MirrorURLSlave>\n" +
                "      <MirrorURLProbe>prefix</MirrorURLProbe>\n" +
                "      <MirrorPassQueryString>prefix</MirrorPassQueryString>\n" +
                "      <MirrorPassOriginalSlashes>prefix</MirrorPassOriginalSlashes>\n" +
                "    </RoutingRule>\n" +
                "  </RoutingRules>\n" +
                "</WebsiteConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseBucketWebsite(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseBucketWebsite(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseBucketWebsite(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseCopyObjectResult() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseCopyObjectResult(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseCopyObjectResult(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseDeleteObjectsResult() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<DeleteObjectsResult>\n" +
                "  <EncodingType>invalid</EncodingType>\n" +
                "</DeleteObjectsResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseDeleteObjectsResult(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<DeleteObjectsResult>\n" +
                "  <EncodingType></EncodingType>\n" +
                "</DeleteObjectsResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseDeleteObjectsResult(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseDeleteObjectsResult(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseDeleteObjectsResult(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseDeleteVersionsResult() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<DeleteObjectsResult>\n" +
                "  <Deleted>\n" +
                "    <Key>key</Key>\n" +
                "  </Deleted>\n" +
                "</DeleteObjectsResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseDeleteVersionsResult(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }


        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseDeleteVersionsResult(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseDeleteVersionsResult(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseListBucketCORS() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListBucketCORS(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseListBucketCORS(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketTagging() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<BucketTagging>\n" +
                "  <TagSet>\n" +
                "    <Tag></Tag>\n" +
                "  </TagSet>\n" +
                "</BucketTagging>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketTagging(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketTagging(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetBucketTagging(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketInfo() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketInfo(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetBucketInfo(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketStat() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketStat(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetBucketStat(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseCreateLiveChannel() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseCreateLiveChannel(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseCreateLiveChannel(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetLiveChannelInfo() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetLiveChannelInfo(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetLiveChannelInfo(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetLiveChannelStat() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetLiveChannelStat(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetLiveChannelStat(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetLiveChannelHistory() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetLiveChannelHistory(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetLiveChannelHistory(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseListLiveChannels() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<ListLiveChannelsResult>\n" +
                "  <IsTruncated>true</IsTruncated>\n" +
                "</ListLiveChannelsResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListLiveChannels(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListLiveChannels(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseListLiveChannels(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetUserQos() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<UserQos>\n" +
                "</UserQos>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetUserQos(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetUserQos(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetUserQos(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketVersioning() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketVersioning(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetBucketVersioning(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketEncryption() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketEncryption(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetBucketEncryption(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketPolicy() {

        try {
            ResponseParsers.parseGetBucketPolicy(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketRequestPayment() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<RequestPayment>\n" +
                "</RequestPayment>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketRequestPayment(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketRequestPayment(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetBucketRequestPayment(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetUserQosInfo() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetUserQosInfo(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetUserQosInfo(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketQosInfo() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketQosInfo(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetBucketQosInfo(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketLifecycle() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<LifecycleConfiguration>\n" +
                "  <Rule>\n" +
                "    <Tag>\n" +
                "    </Tag>\n" +
                "  </Rule>\n" +
                "  <Rule>\n" +
                "  </Rule>\n" +
                "  <Rule>\n" +
                "    <Expiration>\n" +
                "    </Expiration>\n" +
                "    <Transition>\n" +
                "      <Days>1</Days>\n" +
                "    </Transition>\n" +
                "    <NoncurrentVersionExpiration>\n" +
                "    </NoncurrentVersionExpiration>\n" +
                "    <NoncurrentVersionTransition>\n" +
                "    </NoncurrentVersionTransition>\n" +
                "  </Rule>\n" +
                "</LifecycleConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketLifecycle(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }


        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketLifecycle(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetBucketLifecycle(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseSetAsyncFetchTaskResult() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseSetAsyncFetchTaskResult(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseSetAsyncFetchTaskResult(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetAsyncFetchTaskResult() {
        InputStream instream = null;
        String respBody;

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetAsyncFetchTaskResult(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetAsyncFetchTaskResult(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testCreateVpcipResultResponseParser() {
        InputStream instream = null;
        String respBody;

        respBody = "<Vpcip></Vpcip>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.CreateVpcipResultResponseParser parser = new ResponseParsers.CreateVpcipResultResponseParser();
            parser.parse(response);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.CreateVpcipResultResponseParser parser = new ResponseParsers.CreateVpcipResultResponseParser();
            parser.parse(response);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(null);
            ResponseParsers.CreateVpcipResultResponseParser parser = new ResponseParsers.CreateVpcipResultResponseParser();
            parser.parse(response);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testListVpcipResultResponseParser() {
        InputStream instream = null;
        String respBody;

        respBody = "<Vpcip></Vpcip>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.ListVpcipResultResponseParser parser = new ResponseParsers.ListVpcipResultResponseParser();
            parser.parse(response);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.ListVpcipResultResponseParser parser = new ResponseParsers.ListVpcipResultResponseParser();
            parser.parse(response);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(null);
            ResponseParsers.ListVpcipResultResponseParser parser = new ResponseParsers.ListVpcipResultResponseParser();
            parser.parse(response);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testListVpcPolicyResultResponseParser() {
        InputStream instream = null;
        String respBody;

        respBody = "<Vpcip></Vpcip>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.ListVpcPolicyResultResponseParser parser = new ResponseParsers.ListVpcPolicyResultResponseParser();
            parser.parse(response);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.ListVpcPolicyResultResponseParser parser = new ResponseParsers.ListVpcPolicyResultResponseParser();
            parser.parse(response);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(null);
            ResponseParsers.ListVpcPolicyResultResponseParser parser = new ResponseParsers.ListVpcPolicyResultResponseParser();
            parser.parse(response);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseGetBucketInventoryConfig() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<InventoryConfiguration>\n" +
                "</InventoryConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketInventoryConfig(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<InventoryConfiguration>\n" +
                "  <Filter>\n" +
                "  </Filter>\n" +
                "  <Schedule>\n" +
                "  </Schedule>\n" +
                "  <Destination>\n" +
                "  </Destination>\n" +
                "</InventoryConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketInventoryConfig(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<InventoryConfiguration>\n" +
                "  <Filter>\n" +
                "  </Filter>\n" +
                "  <Schedule>\n" +
                "  </Schedule>\n" +
                "  <Destination>\n" +
                "    <OSSBucketDestination>\n" +
                "    </OSSBucketDestination>\n" +
                "  </Destination>\n" +
                "</InventoryConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketInventoryConfig(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<InventoryConfiguration>\n" +
                "  <Filter>\n" +
                "  </Filter>\n" +
                "  <Schedule>\n" +
                "  </Schedule>\n" +
                "  <Destination>\n" +
                "    <OSSBucketDestination>\n" +
                "      <Encryption>\n" +
                "      </Encryption>\n" +
                "    </OSSBucketDestination>\n" +
                "  </Destination>\n" +
                "</InventoryConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketInventoryConfig(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseGetBucketInventoryConfig(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseGetBucketInventoryConfig(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testparseListBucketInventoryConfigurations() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<ListInventoryConfiguration>\n" +
                "</ListInventoryConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListBucketInventoryConfigurations(instream);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseParsers.parseListBucketInventoryConfigurations(instream);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseParsers.parseListBucketInventoryConfigurations(null);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testGetBucketEncryptionResponseParser() {
        InputStream instream = null;
        String respBody;

        respBody = "" +
                "<ServerSideEncryptionRule>\n" +
                "  <ApplyServerSideEncryptionByDefault>\n" +
                "    <SSEAlgorithm>KMS</SSEAlgorithm>\n" +
                "    <KMSMasterKeyID>id</KMSMasterKeyID>\n" +
                "    <KMSDataEncryption>SM4</KMSDataEncryption>\n" +
                "  </ApplyServerSideEncryptionByDefault>\n" +
                "</ServerSideEncryptionRule>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.GetBucketEncryptionResponseParser parser = new ResponseParsers.GetBucketEncryptionResponseParser();
            ServerSideEncryptionConfiguration config = parser.parse(response);
            Assertions.assertEquals(config.getApplyServerSideEncryptionByDefault().getSSEAlgorithm(), "KMS");
            Assertions.assertEquals(config.getApplyServerSideEncryptionByDefault().getKMSMasterKeyID(), "id");
            Assertions.assertEquals(config.getApplyServerSideEncryptionByDefault().getKMSDataEncryption(), "SM4");
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "" +
                "<ServerSideEncryptionRule>\n" +
                "  <ApplyServerSideEncryptionByDefault>\n" +
                "    <SSEAlgorithm>KMS</SSEAlgorithm>\n" +
                "    <KMSMasterKeyID>id</KMSMasterKeyID>\n" +
                "  </ApplyServerSideEncryptionByDefault>\n" +
                "</ServerSideEncryptionRule>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.GetBucketEncryptionResponseParser parser = new ResponseParsers.GetBucketEncryptionResponseParser();
            ServerSideEncryptionConfiguration config = parser.parse(response);
            Assertions.assertEquals(config.getApplyServerSideEncryptionByDefault().getSSEAlgorithm(), "KMS");
            Assertions.assertEquals(config.getApplyServerSideEncryptionByDefault().getKMSMasterKeyID(), "id");
            Assertions.assertEquals(config.getApplyServerSideEncryptionByDefault().getKMSDataEncryption(), null);
            Assertions.assertTrue(true);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        respBody = "invalid";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.GetBucketEncryptionResponseParser parser = new ResponseParsers.GetBucketEncryptionResponseParser();
            parser.parse(response);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }

        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(null);
            ResponseParsers.GetBucketEncryptionResponseParser parser = new ResponseParsers.GetBucketEncryptionResponseParser();
            parser.parse(response);
            Assertions.assertTrue(false);
        } catch (ResponseParseException e) {
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testParseDeleteDirectory() {
        String respBody = "" +
                "<DeleteDirectoryResult>\n" +
                "    <DirectoryName>a/b/c</DirectoryName>\n" +
                "    <DeleteNumber>1</DeleteNumber>\n" +
                "</DeleteDirectoryResult>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        DeleteDirectoryResult result = null;
        try {
            result = ResponseParsers.parseDeleteDirectoryResult(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse delete directory response body fail!");
        }

        Assertions.assertEquals("a/b/c", result.getDirectoryName());
        Assertions.assertEquals(1, result.getDeleteNumber());
        Assertions.assertNull(result.getNextDeleteToken());

        respBody = "" +
                "<DeleteDirectoryResult>\n" +
                "    <DirectoryName>a/b/c</DirectoryName>\n" +
                "    <DeleteNumber>1</DeleteNumber>\n" +
                "    <NextDeleteToken>CgJiYw--</NextDeleteToken>\n" +
                "</DeleteDirectoryResult>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        result = null;
        try {
            result = ResponseParsers.parseDeleteDirectoryResult(instream);
        } catch (ResponseParseException e) {
            Assertions.fail("parse delete directory response body fail!");
        }

        Assertions.assertEquals("a/b/c", result.getDirectoryName());
        Assertions.assertEquals(1, result.getDeleteNumber());
        Assertions.assertEquals("CgJiYw--", result.getNextDeleteToken());
    }

    @Test
    public void testParseBucketResourceGroup() {
        String respBody = "" +
                "<BucketResourceGroupConfiguration>\n" +
                "    <ResourceGroupId>xxx-id-123</ResourceGroupId>\n" +
                "</BucketResourceGroupConfiguration>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        GetBucketResourceGroupResult result = null;
        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.GetBucketResourceGroupResponseParser parser = new ResponseParsers.GetBucketResourceGroupResponseParser();
            result = parser.parse(response);
        } catch (ResponseParseException e) {
            Assertions.fail("parse delete directory response body fail!");
        }

        Assertions.assertEquals("xxx-id-123", result.getResourceGroupId());


        respBody = "" +
                "<BucketResourceGroupConfiguration>\n" +
                "</BucketResourceGroupConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        result = null;
        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.GetBucketResourceGroupResponseParser parser = new ResponseParsers.GetBucketResourceGroupResponseParser();
            result = parser.parse(response);
        } catch (ResponseParseException e) {
            Assertions.fail("parse delete directory response body fail!");
        }

        Assertions.assertEquals(null, result.getResourceGroupId());
    }

    @Test
    public void testGetBucketTransferAccelerationResponseParser() {
        String respBody = "" +
                "<TransferAccelerationConfiguration>\n" +
                "    <Enabled>true</Enabled>\n" +
                "</TransferAccelerationConfiguration>";

        InputStream instream = null;
        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        TransferAcceleration result = null;
        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.GetBucketTransferAccelerationResponseParser parser = new ResponseParsers.GetBucketTransferAccelerationResponseParser();
            result = parser.parse(response);
        } catch (ResponseParseException e) {
            Assertions.fail("parse delete directory response body fail!");
        }

        Assertions.assertEquals(true, result.isEnabled());


        respBody = "" +
                "<TransferAccelerationConfiguration>\n" +
                "    <Enabled>false</Enabled>\n" +
                "</TransferAccelerationConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        result = null;
        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.GetBucketTransferAccelerationResponseParser parser = new ResponseParsers.GetBucketTransferAccelerationResponseParser();
            result = parser.parse(response);
        } catch (ResponseParseException e) {
            Assertions.fail("parse delete directory response body fail!");
        }
        Assertions.assertEquals(false, result.isEnabled());



        respBody = "" +
                "<TransferAccelerationConfiguration>\n" +
                "</TransferAccelerationConfiguration>";

        try {
            instream = new ByteArrayInputStream(respBody.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail("UnsupportedEncodingException");
        }

        result = null;
        try {
            ResponseMessage response = new ResponseMessage(null);
            response.setContent(instream);
            ResponseParsers.GetBucketTransferAccelerationResponseParser parser = new ResponseParsers.GetBucketTransferAccelerationResponseParser();
            result = parser.parse(response);
        } catch (ResponseParseException e) {
            Assertions.fail("parse delete directory response body fail!");
        }
        Assertions.assertEquals(false, result.isEnabled());
    }
}
