/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.oss.common.model;

import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.*;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;

public class LivechannelRelatedTest {

    @Test
    public void testCreateLiveChannelRequest() {
        CreateLiveChannelRequest request = new CreateLiveChannelRequest("bucket", "live", "desc");
        request = new CreateLiveChannelRequest("bucket", "live", "desc", new LiveChannelTarget());
        request = new CreateLiveChannelRequest("bucket", "live", "desc", LiveChannelStatus.Enabled);

        Assertions.assertEquals("desc", request.getLiveChannelDescription());
        request.setLiveChannelDescription("new desc");
        Assertions.assertEquals("new desc", request.getLiveChannelDescription());

        Assertions.assertEquals(LiveChannelStatus.Enabled, request.getLiveChannelStatus());
        request.setLiveChannelStatus(LiveChannelStatus.Disabled);
        Assertions.assertEquals(LiveChannelStatus.Disabled, request.getLiveChannelStatus());

        LiveChannelTarget target = new LiveChannelTarget();
        request.setLiveChannelTarget(target);
        Assertions.assertEquals(target, request.getLiveChannelTarget());
    }

    @Test
    public void testGenerateRtmpUriRequest() {
        GenerateRtmpUriRequest request = new GenerateRtmpUriRequest("", "", "", 10);
        request.setBucketName("bucket");
        request.setLiveChannelName("livename");
        request.setPlaylistName("playlistname");
        request.setExpires(30);
        Assertions.assertEquals("bucket", request.getBucketName());
        Assertions.assertEquals("livename", request.getLiveChannelName());
        Assertions.assertEquals("playlistname", request.getPlaylistName());
        Assertions.assertEquals(new Long(30), request.getExpires());
    }

    @Test
    public void testGenerateVodPlaylistRequest() {
        GenerateVodPlaylistRequest request = new GenerateVodPlaylistRequest("bucket", "livename", "playlist-name");
        request.setPlaylistName("new name");
        request.setStartTime(10);
        request.setEndTime(20);

        Assertions.assertEquals("new name", request.getPlaylistName());
        Assertions.assertEquals(new Long(10), request.getStartTime());
        Assertions.assertEquals(new Long(20), request.getEndTime());
    }

    @Test
    public void testGetVodPlaylistRequest() {
        GetVodPlaylistRequest request = new GetVodPlaylistRequest("bucket", "livename", 10, 20);
        request.setStartTime(40);
        request.setEndTime(80);
        Assertions.assertEquals(new Long(40), request.getStartTime());
        Assertions.assertEquals(new Long(80), request.getEndTime());
    }

    @Test
    public void testListLiveChannelsRequest() {
        ListLiveChannelsRequest request = new ListLiveChannelsRequest("bucket", "prefix", "marker", 10);
        Assertions.assertEquals("marker", request.getMarker());

        request = new ListLiveChannelsRequest("bucket1", "prefix1", "marker1");
        Assertions.assertEquals("marker1", request.getMarker());

        request = new ListLiveChannelsRequest("bucket2").withMarker("marker2").withPrefix("prefix2").withMaxKeys(50);
        Assertions.assertEquals("marker2", request.getMarker());

        try {
            request.setMaxKeys(3000);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            request.setMaxKeys(-1);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testLiveChannel() {
        LiveChannel channel = new LiveChannel("name", "desc", LiveChannelStatus.Enabled, null, null, null);
        channel.setLastModified(new Date());
        List<String> list = new ArrayList<String>();
        list.add("test");
        channel.setPlayUrls(list);
        channel.setPublishUrls(list);
        String name = channel.getName();
        Assertions.assertEquals("name", name);
        String str = channel.toString();
        Assertions.assertFalse(str.isEmpty());
    }

    @Test
    public void testliveChannelListing() {
        List<LiveChannel> liveChannels = new ArrayList<LiveChannel>();
        LiveChannelListing list = new LiveChannelListing();
        list.setObjectSummaries(null);
        list.setObjectSummaries(liveChannels);
        liveChannels.add(new LiveChannel());
        list.setObjectSummaries(liveChannels);
    }

    @Test
    public void testLiveChannelStat() {
        LiveChannelStat stat = new LiveChannelStat();
        LiveChannelStat.VideoStat vstat = new LiveChannelStat.VideoStat(100, 50, 30, 100, "h264");
        LiveChannelStat.AudioStat astat = new LiveChannelStat.AudioStat(100, 100, "aac");

        Assertions.assertEquals(100, vstat.getWidth());
        Assertions.assertEquals(50, vstat.getHeight());
        Assertions.assertEquals(30, vstat.getFrameRate());
        Assertions.assertEquals(100, vstat.getBandWidth());
        Assertions.assertEquals("h264", vstat.getCodec());

        Assertions.assertEquals(100, astat.getBandWidth());
        Assertions.assertEquals(100, astat.getSampleRate());
        Assertions.assertEquals("aac", astat.getCodec());

        vstat = new LiveChannelStat.VideoStat();
        astat = new LiveChannelStat.AudioStat();

        vstat.setWidth(10);
        vstat.setHeight(20);
        vstat.setFrameRate(30);
        vstat.setBandWidth(40);
        vstat.setCodec("h265");

        astat.setSampleRate(10);
        astat.setBandWidth(20);
        astat.setCodec("pcm");

        stat.setAudioStat(astat);
        stat.setVideoStat(vstat);
        stat.setConnectedDate(null);
        stat.setRemoteAddress("remote");

        LiveChannelStatus status = LiveChannelStatus.parse("enabled");
        Assertions.assertEquals(LiveChannelStatus.Enabled, status);
    }

    @Test
    public void testLiveRecord() {
        Date date = null;
        LiveRecord record = new LiveRecord();
        try {
            date = DateUtil.parseRfc822Date("Wed, 15 Mar 2017 02:23:45 GMT");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        record.setStartDate(date);
        record.setEndDate(date);
        record.setRemoteAddress("remote");

        record = new LiveRecord(date, date, "address");

        Assertions.assertEquals(date, record.getStartDate());
        Assertions.assertEquals(date, record.getEndDate());
        Assertions.assertEquals("address", record.getRemoteAddress());
    }

    @Test
    public void testPushflowStatus() {

        try {
            PushflowStatus.parse("UN");
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

}
