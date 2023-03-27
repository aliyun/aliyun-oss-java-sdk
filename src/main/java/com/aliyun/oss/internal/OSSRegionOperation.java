package com.aliyun.oss.internal;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.model.GetDescribeRegionsRequest;
import com.aliyun.oss.model.GetDescribeRegionsResult;
import java.util.HashMap;
import java.util.Map;

import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.internal.RequestParameters.*;
import static com.aliyun.oss.internal.ResponseParsers.*;

public class OSSRegionOperation extends OSSOperation {
    public OSSRegionOperation(ServiceClient client, CredentialsProvider credsProvider) {
        super(client, credsProvider);
    }

    public GetDescribeRegionsResult getDescribeRegions(GetDescribeRegionsRequest getDescribeRegionsRequest) throws OSSException, ClientException {
        assertParameterNotNull(getDescribeRegionsRequest, "getDescribeRegionsRequest");

        String bucketName = getDescribeRegionsRequest.getBucketName();
        String region = getDescribeRegionsRequest.getRegion();

        Map<String, String> params = new HashMap<String, String>();
        params.put(REGIONS, region);


        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(getDescribeRegionsRequest).build();

        return doOperation(request, getDescribeRegionsResponseParser, bucketName, null, true);
    }
}
