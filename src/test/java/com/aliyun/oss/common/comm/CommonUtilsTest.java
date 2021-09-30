/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.aliyun.oss.common.comm;

import com.aliyun.oss.common.utils.CommonUtils;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author zhouao
 * @version $Id: CommonUtilsTest.java, v 0.1 2021年09月30日 2:09 PM zhouao Exp $
 */
public class CommonUtilsTest {
    @Test
    public void testToURI() {
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        String defaultProtocol = "https";

        URI res = CommonUtils.toURI(endpoint, defaultProtocol);
        assertEquals(endpoint, res.toString());
    }

    @Test
    public void testToURI2() {
        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
        String defaultProtocol = "https";

        URI res = CommonUtils.toURI(endpoint, defaultProtocol);
        assertEquals("https://oss-cn-hangzhou.aliyuncs.com", res.toString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testToURI3() {
        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
        String defaultProtocol = "";

        URI res = CommonUtils.toURI(endpoint, defaultProtocol);
        assertEquals("https://oss-cn-hangzhou.aliyuncs.com", res.toString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testToURI4() {
        String endpoint = null;
        String defaultProtocol = "http";

        URI res = CommonUtils.toURI(endpoint, defaultProtocol);
        assertEquals("https://oss-cn-hangzhou.aliyuncs.com", res.toString());
    }
}