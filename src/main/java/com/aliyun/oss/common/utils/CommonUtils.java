/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.aliyun.oss.common.utils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author zhouao
 * @version $Id: CommonUtils.java, v 0.1 2021年09月30日 2:01 PM zhouao Exp $
 */
public class CommonUtils {
    public static URI toURI(String endpoint, String defaultProtocol) throws IllegalArgumentException {
        if (StringUtils.isNullOrEmpty(endpoint)) {
            return null;
        }

        if (!endpoint.contains("://")) {
            endpoint = defaultProtocol + "://" + endpoint;
        }

        try {
            return new URI(endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}