package com.aliyun.oss.internal;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.ServerSideEncryptionRule;
import com.aliyun.oss.model.SetBucketEncryptionRequest;

import java.util.HashMap;
import java.util.Map;

import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketEncryptionRequestMarshaller;
import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_ENCRYPTION;
import static com.aliyun.oss.internal.ResponseParsers.getBucketEncryptionResponseParser;

/**
 * Created by jingdan on 2018/12/13.
 * Bucket Encryption Operation
 */
public class OSSEncryptionOperation extends OSSOperation{

	public OSSEncryptionOperation(ServiceClient client, CredentialsProvider credsProvider) {
		super(client, credsProvider);
	}

	/**
	 * Set Bucket Encryption.
	 */
	public void setBucketEncryption(SetBucketEncryptionRequest setBucketEncryptionRequest)
			throws OSSException, ClientException {

		assertParameterNotNull(setBucketEncryptionRequest, "setBucketEncryptionRequest");

		String bucketName = setBucketEncryptionRequest.getBucketName();
		assertParameterNotNull(bucketName, "bucketName");
		ensureBucketNameValid(bucketName);

		Map<String, String> params = new HashMap<String, String>();
		params.put(SUBRESOURCE_ENCRYPTION, null);

		RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
				.setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
				.setInputStreamWithLength(setBucketEncryptionRequestMarshaller.marshall(setBucketEncryptionRequest))
				.setOriginalRequest(setBucketEncryptionRequest).build();

		doOperation(request, emptyResponseParser, bucketName, null);
	}

	/**
	 * Delete Bucket Encryption.
	 */
	public void deleteBucketEncryption(GenericRequest genericRequest)
			throws OSSException, ClientException {

		assertParameterNotNull(genericRequest, "genericRequest");

		String bucketName = genericRequest.getBucketName();
		assertParameterNotNull(bucketName, "bucketName");
		ensureBucketNameValid(bucketName);

		Map<String, String> params = new HashMap<String, String>();
		params.put(SUBRESOURCE_ENCRYPTION, null);

		RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
				.setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
				.setOriginalRequest(genericRequest).build();

		doOperation(request, emptyResponseParser, bucketName, null);
	}

	/**
	 * Get Bucket Encryption
	 */
	public ServerSideEncryptionRule getBucketEncryption(GenericRequest genericRequest) throws OSSException, ClientException {

		assertParameterNotNull(genericRequest, "genericRequest");

		String bucketName = genericRequest.getBucketName();
		assertParameterNotNull(bucketName, "bucketName");
		ensureBucketNameValid(bucketName);

		Map<String, String> params = new HashMap<String, String>();
		params.put(SUBRESOURCE_ENCRYPTION, null);

		RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
				.setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
				.setOriginalRequest(genericRequest).build();

		return doOperation(request, getBucketEncryptionResponseParser, bucketName, null, true);
	}
}
