package com.aliyun.oss.common.comm;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.endpoint.EndpointResolver;
import com.aliyun.oss.integrationtests.TestBase;
import com.aliyun.oss.integrationtests.TestConfig;
import junit.framework.Assert;
import org.junit.Test;

public class EndpointRefreshTest extends TestBase {

	@Test
    public void testEndpointRefresh() {
        class CustomEndpointResolver implements EndpointResolver {
            private String endpoint = null;

            public String getEndpoint() {
                return endpoint;
            }

            public void setEndpoint(String endpoint) {
                this.endpoint = endpoint;
            }

            @Override
            public String resolveGeneralApiEndpoint(String generalApiEndpoint) {
                return this.endpoint;
            }

            @Override
            public String resolveGetServiceApiEndpoint(String getServiceApiEndpoint) {
                return this.endpoint;
            }
        }

        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
        OSS client = new OSSClient(TestConfig.OSS_TEST_ENDPOINT, new DefaultCredentialProvider(credentials), conf);
        Assert.assertEquals(TestConfig.OSS_TEST_ENDPOINT, ((OSSClient) client).getEndpoint().toString());

        CustomEndpointResolver customEndpointResolver = new CustomEndpointResolver();

        // Custom Domain
        customEndpointResolver.setEndpoint("www.abbbc.com");
        conf.setSupportCname(true);
        conf.setRefreshEndpointAddr(true);
        conf.setEndpointResolver(customEndpointResolver);

        try {
            client.getObject("bucket","object");
            System.out.println("ok");
        } catch (ClientException e) {
            Assert.assertEquals("www.abbbc.com\n" +
                    "[ErrorCode]: UnknownHost\n" +
                    "[RequestId]: Unknown", e.getMessage());
        }

        // ip
        customEndpointResolver.setEndpoint("127.0.0.1");
        conf.setSupportCname(true);
        conf.setEndpointResolver(customEndpointResolver);

        try {
            client.getObject("bucket","object");
            System.out.println("ok");
        } catch (ClientException e) {
            Assert.assertEquals("Connect to 127.0.0.1:80 [/127.0.0.1] failed: Connection refused: connect\n" +
                    "[ErrorCode]: SocketException\n" +
                    "[RequestId]: Unknown", e.getMessage());
        }

        // ip:port
        customEndpointResolver.setEndpoint("127.0.0.1:8080");
        conf.setSupportCname(true);
        conf.setEndpointResolver(customEndpointResolver);

        try {
            client.getObject("bucket","object");
            System.out.println("ok");
        } catch (ClientException e) {
            Assert.assertEquals("Connect to 127.0.0.1:8080 [/127.0.0.1] failed: Connection refused: connect\n" +
                    "[ErrorCode]: SocketException\n" +
                    "[RequestId]: Unknown", e.getMessage());
        }

        // bucket.Domain
        conf.setSupportCname(false);
        customEndpointResolver.setEndpoint("aaa.aliyuncs.com");
        conf.setEndpointResolver(customEndpointResolver);
        try {
            client.getObject("bucket","object");
        } catch (ClientException e) {
            Assert.assertEquals("aaa.aliyuncs.com\n" +
                    "[ErrorCode]: UnknownHost\n" +
                    "[RequestId]: Unknown", e.getMessage());
        }
    }
}
