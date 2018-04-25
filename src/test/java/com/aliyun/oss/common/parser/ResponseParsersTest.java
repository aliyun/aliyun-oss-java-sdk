package com.aliyun.oss.common.parser;

import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.internal.ResponseParsers;
import com.aliyun.oss.model.BucketReplicationProgress;
import com.aliyun.oss.model.ReplicationRule;
import junit.framework.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * Created by zhoufeng.chen on 2018/1/10.
 */
public class ResponseParsersTest {
    @Test
    public void testParseGetBucketReplicationWithCloudLocation()  {
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
            e.printStackTrace();
            Assert.fail("UnsupportedEncodingException");
        }

        List<ReplicationRule>  rules = null;
        try {
            rules = ResponseParsers.parseGetBucketReplication(instream);
        } catch (ResponseParseException e) {
            e.printStackTrace();
            Assert.fail("parse bucket replication response body fail!");
        }
        Assert.assertTrue(rules.size() > 0);

        ReplicationRule rule = rules.get(0);
        Assert.assertEquals("12345678", rule.getReplicationRuleID());
        Assert.assertEquals("testBucketName", rule.getTargetBucketName());
        Assert.assertNull(rule.getTargetBucketLocation());
        Assert.assertEquals("testCloud", rule.getTargetCloud());
        Assert.assertEquals("testCloudLocation", rule.getTargetCloudLocation());
        Assert.assertEquals(true, rule.isEnableHistoricalObjectReplication());
    }

    @Test
    public void testParseGetBucketReplicationWithoutCloudLocation()
    {
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
            e.printStackTrace();
            Assert.fail("UnsupportedEncodingException");
        }

        List<ReplicationRule>  rules = null;
        try {
            rules = ResponseParsers.parseGetBucketReplication(instream);
        } catch (ResponseParseException e) {
            e.printStackTrace();
            Assert.fail("parse bucket replication response body fail!");
        }
        Assert.assertTrue(rules.size() > 0);

        ReplicationRule rule = rules.get(0);
        Assert.assertEquals("12345678", rule.getReplicationRuleID());
        Assert.assertEquals("testBucketName", rule.getTargetBucketName());
        Assert.assertEquals("testLocation", rule.getTargetBucketLocation());
        Assert.assertNull(rule.getTargetCloud());
        Assert.assertNull(rule.getTargetCloudLocation());
        Assert.assertEquals(false, rule.isEnableHistoricalObjectReplication());
    }

    @Test
    public void testParseGetBucketReplicationProgressWithCloudLocation()
    {
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
            e.printStackTrace();
            Assert.fail("UnsupportedEncodingException");
        }

        BucketReplicationProgress progress = null;
        try {
            progress =  ResponseParsers.parseGetBucketReplicationProgress(instream);
        } catch (ResponseParseException e) {
            e.printStackTrace();
            Assert.fail("parse bucket replication process response body fail!");
        }

        Assert.assertEquals("12345678", progress.getReplicationRuleID());
        Assert.assertEquals("testBucketName", progress.getTargetBucketName());
        Assert.assertNull(progress.getTargetBucketLocation());
        Assert.assertEquals("testCloud", progress.getTargetCloud());
        Assert.assertEquals("testCloudLocation", progress.getTargetCloudLocation());
        Assert.assertEquals(0.8f, progress.getHistoricalObjectProgress());
        Assert.assertEquals(dt, progress.getNewObjectProgress());

    }

    @Test
    public void testParseGetBucketReplicationProgressWithoutCloudLocation()
    {
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
            e.printStackTrace();
            Assert.fail("UnsupportedEncodingException");
        }

        BucketReplicationProgress progress = null;
        try {
            progress =  ResponseParsers.parseGetBucketReplicationProgress(instream);
        } catch (ResponseParseException e) {
            e.printStackTrace();
            Assert.fail("parse bucket replication process response body fail!");
        }

        Assert.assertEquals("12345678", progress.getReplicationRuleID());
        Assert.assertEquals("testBucketName", progress.getTargetBucketName());
        Assert.assertEquals("testLocation", progress.getTargetBucketLocation());
        Assert.assertNull(progress.getTargetCloud());
        Assert.assertNull(progress.getTargetCloudLocation());
        Assert.assertEquals(0.9f, progress.getHistoricalObjectProgress());
        Assert.assertEquals(dt, progress.getNewObjectProgress());
    }
}
