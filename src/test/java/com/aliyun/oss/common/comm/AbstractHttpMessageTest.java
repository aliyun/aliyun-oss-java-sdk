package com.aliyun.oss.common.comm;

import com.aliyun.oss.internal.OSSHeaders;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * AbstractHttpMessageTest
 *
 * @author xiaodongli
 * @date 2019-11-05 16:24
 */
public class AbstractHttpMessageTest {

    @Test
    public void addHeaderOnETagLowercaseOriginal() {
        AbstractHttpMessage httpMessage = new ResponseMessage(null);

        String value = UUID.randomUUID().toString();

        httpMessage.addHeader(OSSHeaders.ETAG.toLowerCase(), value);

        Assert.assertEquals(value, httpMessage.getHeaders().get(OSSHeaders.ETAG.toLowerCase()));
        Assert.assertEquals(value, httpMessage.getHeaders().get(OSSHeaders.ETAG));
    }

}
