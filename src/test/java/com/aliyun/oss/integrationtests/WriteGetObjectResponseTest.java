package com.aliyun.oss.integrationtests;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Test;
import java.io.InputStream;
import static com.aliyun.oss.integrationtests.TestUtils.genFixedLengthInputStream;

public class WriteGetObjectResponseTest extends TestBase{

    @Test
    public void testWriteGetObjectResponse() {
        final int instreamLength = 128 * 1024;
        InputStream instream = null;
        String route = "test-ap-process-name-12836***16515-opap.oss-cn-beijing-internal.oss-object-process.aliyuncs.com";
        String token = "OSSV1#UMoA43+Bi9b6Q1Lu6UjhLXnmq4I/wIFac3uZfBkgJtg2xtHkZJ4bZglDWyOgWRlGTrA8y/i6D9eH8PmAiq2NL2R/MD/UX6zvRhT8WMHUewgc9QWPs9LPHiZytkUZnGa39mnv/73cyPWTuxgxyk4dNhlzEE6U7PdzmCCu8gIrjuYLPrA9psRn0ZC8J2/DCZGVx0BE7AmIJTcNtLKTSjxsJyTts/******";
        int status = 200;

        try {
            instream = genFixedLengthInputStream(instreamLength);
            WriteGetObjectResponseRequest writeGetObjectResponseRequest = new WriteGetObjectResponseRequest(route, token, status, instream);
            writeGetObjectResponseRequest.addHeader("x-oss-fwd-header-ETag", "D41D8CD98F00B204E9800998ECF8****");
            ossClient.writeGetObjectResponse(writeGetObjectResponseRequest);
        } catch (ClientException e) {
            Assert.assertEquals(e.getErrorCode(), "UnknownHost");
            e.printStackTrace();
        }
    }
}
