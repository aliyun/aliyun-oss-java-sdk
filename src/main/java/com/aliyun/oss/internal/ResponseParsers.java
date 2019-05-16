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

package com.aliyun.oss.internal;

import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.parser.ResponseParseException;
import com.aliyun.oss.common.parser.ResponseParser;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.common.utils.HttpUtil;
import com.aliyun.oss.common.utils.StringUtils;
import com.aliyun.oss.model.*;
import com.aliyun.oss.model.AddBucketReplicationRequest.ReplicationAction;
import com.aliyun.oss.model.LifecycleRule.RuleStatus;
import com.aliyun.oss.model.LifecycleRule.StorageTransition;
import com.aliyun.oss.model.LiveChannelStat.AudioStat;
import com.aliyun.oss.model.LiveChannelStat.VideoStat;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXBuilder;

import java.io.InputStream;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;
import java.util.zip.CheckedInputStream;

import static com.aliyun.oss.common.utils.CodingUtils.isNullOrEmpty;
import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;
import static com.aliyun.oss.internal.OSSUtils.trimQuotes;

/*
 * A collection of parsers that parse HTTP reponses into corresponding human-readable results.
 */
public final class ResponseParsers {

    public static final ListUserRegionResponseParser listUserRegionResponseParser = new ListUserRegionResponseParser();
    public static final ListBucketResponseParser listBucketResponseParser = new ListBucketResponseParser();
    public static final ListImageStyleResponseParser listImageStyleResponseParser = new ListImageStyleResponseParser();
    public static final ListBucketVpcIdResponseParser listBucketVpcIdResponseParser = new ListBucketVpcIdResponseParser();
    public static final GetBucketRefererResponseParser getBucketRefererResponseParser = new GetBucketRefererResponseParser();
    public static final GetBucketAclResponseParser getBucketAclResponseParser = new GetBucketAclResponseParser();
    public static final GetBucketMetadataResponseParser getBucketMetadataResponseParser = new GetBucketMetadataResponseParser();
    public static final GetBucketLocationResponseParser getBucketLocationResponseParser = new GetBucketLocationResponseParser();
    public static final GetBucketLoggingResponseParser getBucketLoggingResponseParser = new GetBucketLoggingResponseParser();
    public static final GetBucketWebsiteResponseParser getBucketWebsiteResponseParser = new GetBucketWebsiteResponseParser();
    public static final GetBucketLifecycleResponseParser getBucketLifecycleResponseParser = new GetBucketLifecycleResponseParser();
    public static final GetBucketCorsResponseParser getBucketCorsResponseParser = new GetBucketCorsResponseParser();
    public static final GetBucketImageResponseParser getBucketImageResponseParser = new GetBucketImageResponseParser();
    public static final GetImageStyleResponseParser getImageStyleResponseParser = new GetImageStyleResponseParser();
    public static final GetBucketImageProcessConfResponseParser getBucketImageProcessConfResponseParser = new GetBucketImageProcessConfResponseParser();
    public static final GetBucketTaggingResponseParser getBucketTaggingResponseParser = new GetBucketTaggingResponseParser();
    public static final GetBucketReplicationResponseParser getBucketReplicationResponseParser = new GetBucketReplicationResponseParser();
    public static final GetBucketReplicationProgressResponseParser getBucketReplicationProgressResponseParser = new GetBucketReplicationProgressResponseParser();
    public static final GetBucketReplicationLocationResponseParser getBucketReplicationLocationResponseParser = new GetBucketReplicationLocationResponseParser();
    public static final GetBucketCnameResponseParser getBucketCnameResponseParser = new GetBucketCnameResponseParser();
    public static final GetBucketInfoResponseParser getBucketInfoResponseParser = new GetBucketInfoResponseParser();
    public static final GetBucketStatResponseParser getBucketStatResponseParser = new GetBucketStatResponseParser();
    public static final GetBucketQosResponseParser getBucketQosResponseParser = new GetBucketQosResponseParser();
    public static final GetBucketRequestPaymentResponseParser getBucketRequestPaymentResponseParser = new GetBucketRequestPaymentResponseParser();
    public static final GetBucketEncryptionResponseParser getBucketEncryptionResponseParser = new GetBucketEncryptionResponseParser();
    public static final InitiateWormConfigurationResponseParser initiateWormConfigurationResponseParser = new InitiateWormConfigurationResponseParser();
    public static final GetWormConfigurationResponseParser getWormConfigurationResponseParser = new GetWormConfigurationResponseParser();
    public static final GetBucketNotificationResponseParser getBucketNotificationResponseParser = new GetBucketNotificationResponseParser();
    public static final GetBucketVersioningResponseParser getBucketVersioningResponseParser = new GetBucketVersioningResponseParser();

    public static final ListObjectsReponseParser listObjectsReponseParser = new ListObjectsReponseParser();
    public static final ListObjectVersionsReponseParser listObjectVersionsReponseParser = new ListObjectVersionsReponseParser();
    public static final PutObjectReponseParser putObjectReponseParser = new PutObjectReponseParser();
    public static final PutObjectProcessReponseParser putObjectProcessReponseParser = new PutObjectProcessReponseParser();
    public static final AppendObjectResponseParser appendObjectResponseParser = new AppendObjectResponseParser();
    public static final GetObjectMetadataResponseParser getObjectMetadataResponseParser = new GetObjectMetadataResponseParser();
    public static final CopyObjectResponseParser copyObjectResponseParser = new CopyObjectResponseParser();
    public static final DeleteObjectsResponseParser deleteObjectsResponseParser = new DeleteObjectsResponseParser();
    public static final GetObjectAclResponseParser getObjectAclResponseParser = new GetObjectAclResponseParser();
    public static final GetSimplifiedObjectMetaResponseParser getSimplifiedObjectMetaResponseParser = new GetSimplifiedObjectMetaResponseParser();
    public static final RestoreObjectResponseParser restoreObjectResponseParser = new RestoreObjectResponseParser();
    public static final ProcessObjectResponseParser processObjectResponseParser = new ProcessObjectResponseParser();

    public static final CompleteMultipartUploadResponseParser completeMultipartUploadResponseParser = new CompleteMultipartUploadResponseParser();
    public static final CompleteMultipartUploadProcessResponseParser completeMultipartUploadProcessResponseParser = new CompleteMultipartUploadProcessResponseParser();
    public static final InitiateMultipartUploadResponseParser initiateMultipartUploadResponseParser = new InitiateMultipartUploadResponseParser();
    public static final ListMultipartUploadsResponseParser listMultipartUploadsResponseParser = new ListMultipartUploadsResponseParser();
    public static final ListPartsResponseParser listPartsResponseParser = new ListPartsResponseParser();

    public static final CreateLiveChannelResponseParser createLiveChannelResponseParser = new CreateLiveChannelResponseParser();
    public static final GetLiveChannelInfoResponseParser getLiveChannelInfoResponseParser = new GetLiveChannelInfoResponseParser();
    public static final GetLiveChannelStatResponseParser getLiveChannelStatResponseParser = new GetLiveChannelStatResponseParser();
    public static final GetLiveChannelHistoryResponseParser getLiveChannelHistoryResponseParser = new GetLiveChannelHistoryResponseParser();
    public static final ListLiveChannelsReponseParser listLiveChannelsReponseParser = new ListLiveChannelsReponseParser();

    public static final GetSymbolicLinkResponseParser getSymbolicLinkResponseParser = new GetSymbolicLinkResponseParser();

    public static final GetUdfInfoResponseParser getUdfInfoResponseParser = new GetUdfInfoResponseParser();
    public static final ListUdfResponseParser listUdfResponseParser = new ListUdfResponseParser();
    public static final GetUdfImageInfoResponseParser getUdfImageInfoResponseParser = new GetUdfImageInfoResponseParser();
    public static final GetUdfApplicationInfoResponseParser getUdfApplicationInfoResponseParser = new GetUdfApplicationInfoResponseParser();
    public static final ListUdfApplicationInfoResponseParser listUdfApplicationInfoResponseParser = new ListUdfApplicationInfoResponseParser();
    public static final GetObjectTaggingResponseParser getObjectTaggingResponseParser = new GetObjectTaggingResponseParser();

    public static final class EmptyResponseParser implements ResponseParser<ResponseMessage> {

        @Override
        public ResponseMessage parse(ResponseMessage response) throws ResponseParseException {
            // Close response and return it directly without parsing.
            safeCloseResponse(response);
            return response;
        }

    }

    public static final class ListUserRegionResponseParser implements ResponseParser<ListUserRegionsResult> {

        @Override
        public ListUserRegionsResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                ListUserRegionsResult result = parseListUserRegion(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListBucketResponseParser implements ResponseParser<BucketList> {

        @Override
        public BucketList parse(ResponseMessage response) throws ResponseParseException {
            try {
                BucketList result = parseListBucket(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListImageStyleResponseParser implements ResponseParser<List<Style>> {
        @Override
        public List<Style> parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseListImageStyle(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static final class ListBucketVpcIdResponseParser implements ResponseParser<BucketVpcIdList> {

        @Override
        public BucketVpcIdList parse(ResponseMessage response) throws ResponseParseException {
            try {
                BucketVpcIdList result = parseListBucketVpcId(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketRefererResponseParser implements ResponseParser<BucketReferer> {

        @Override
        public BucketReferer parse(ResponseMessage response) throws ResponseParseException {
            try {
                BucketReferer result = parseGetBucketReferer(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketAclResponseParser implements ResponseParser<AccessControlList> {

        @Override
        public AccessControlList parse(ResponseMessage response) throws ResponseParseException {
            try {
                AccessControlList result = parseGetBucketAcl(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }
	
    public static final class GetBucketMetadataResponseParser implements ResponseParser<BucketMetadata> {

        @Override
        public BucketMetadata parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseBucketMetadata(response.getHeaders());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketLocationResponseParser implements ResponseParser<String> {

        @Override
        public String parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetBucketLocation(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketLoggingResponseParser implements ResponseParser<BucketLoggingResult> {

        @Override
        public BucketLoggingResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                BucketLoggingResult result = parseBucketLogging(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketImageResponseParser implements ResponseParser<GetBucketImageResult> {
        @Override
        public GetBucketImageResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseBucketImage(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static final class GetImageStyleResponseParser implements ResponseParser<GetImageStyleResult> {
        @Override
        public GetImageStyleResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseImageStyle(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static final class GetBucketImageProcessConfResponseParser implements ResponseParser<BucketProcess> {

        @Override
        public BucketProcess parse(ResponseMessage response) throws ResponseParseException {
            try {
                BucketProcess result = parseGetBucketImageProcessConf(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketWebsiteResponseParser implements ResponseParser<BucketWebsiteResult> {

        @Override
        public BucketWebsiteResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                BucketWebsiteResult result = parseBucketWebsite(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketLifecycleResponseParser implements ResponseParser<List<LifecycleRule>> {

        @Override
        public List<LifecycleRule> parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetBucketLifecycle(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketCnameResponseParser implements ResponseParser<List<CnameConfiguration>> {

        @Override
        public List<CnameConfiguration> parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetBucketCname(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketInfoResponseParser implements ResponseParser<BucketInfo> {

        @Override
        public BucketInfo parse(ResponseMessage response) throws ResponseParseException {
            try {
                BucketInfo result = parseGetBucketInfo(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketStatResponseParser implements ResponseParser<BucketStat> {

        @Override
        public BucketStat parse(ResponseMessage response) throws ResponseParseException {
            try {
                BucketStat result = parseGetBucketStat(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketQosResponseParser implements ResponseParser<UserQos> {

        @Override
        public UserQos parse(ResponseMessage response) throws ResponseParseException {
            try {
                UserQos result = parseGetUserQos(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketEncryptionResponseParser implements ResponseParser<ServerSideEncryptionRule> {

        @Override
        public ServerSideEncryptionRule parse(ResponseMessage response) throws ResponseParseException {
            try {
                ServerSideEncryptionRule result = parseGetBucketEncryption(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class CreateLiveChannelResponseParser implements ResponseParser<CreateLiveChannelResult> {

        @Override
        public CreateLiveChannelResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                CreateLiveChannelResult result = parseCreateLiveChannel(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetLiveChannelInfoResponseParser implements ResponseParser<LiveChannelInfo> {

        @Override
        public LiveChannelInfo parse(ResponseMessage response) throws ResponseParseException {
            try {
                LiveChannelInfo result = parseGetLiveChannelInfo(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetLiveChannelStatResponseParser implements ResponseParser<LiveChannelStat> {

        @Override
        public LiveChannelStat parse(ResponseMessage response) throws ResponseParseException {
            try {
                LiveChannelStat result = parseGetLiveChannelStat(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetLiveChannelHistoryResponseParser implements ResponseParser<List<LiveRecord>> {

        @Override
        public List<LiveRecord> parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetLiveChannelHistory(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListLiveChannelsReponseParser implements ResponseParser<LiveChannelListing> {

        @Override
        public LiveChannelListing parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseListLiveChannels(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketCorsResponseParser implements ResponseParser<List<CORSRule>> {

        @Override
        public List<CORSRule> parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseListBucketCORS(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketTaggingResponseParser implements ResponseParser<TagSet> {

        @Override
        public TagSet parse(ResponseMessage response) throws ResponseParseException {
            try {
                TagSet result = parseGetBucketTagging(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketReplicationResponseParser implements ResponseParser<List<ReplicationRule>> {

        @Override
        public List<ReplicationRule> parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetBucketReplication(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketReplicationProgressResponseParser
            implements ResponseParser<BucketReplicationProgress> {

        @Override
        public BucketReplicationProgress parse(ResponseMessage response) throws ResponseParseException {
            try {
                BucketReplicationProgress result = parseGetBucketReplicationProgress(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketReplicationLocationResponseParser implements ResponseParser<List<String>> {

        @Override
        public List<String> parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetBucketReplicationLocation(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketNotificationResponseParser implements ResponseParser<NotificationConfiguration> {

        @Override
        public NotificationConfiguration parse(ResponseMessage response)
            throws ResponseParseException {
            try {
                return parseNotificationConfiguration(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static final class ListObjectsReponseParser implements ResponseParser<ObjectListing> {

        @Override
        public ObjectListing parse(ResponseMessage response) throws ResponseParseException {
            try {
                ObjectListing result = parseListObjects(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListObjectVersionsReponseParser implements ResponseParser<ObjectVersionsListing> {

        @Override
        public ObjectVersionsListing parse(ResponseMessage response) throws ResponseParseException {
            try {
                ObjectVersionsListing result = parseListObjectVersions(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class PutObjectReponseParser implements ResponseParser<PutObjectResult> {

        @Override
        public PutObjectResult parse(ResponseMessage response) throws ResponseParseException {
            PutObjectResult result = new PutObjectResult();
            try {
                result.setETag(trimQuotes(response.getHeaders().get(OSSHeaders.ETAG)));
                result.setRequestId(response.getRequestId());
                result.setVersionId(response.getVersionId());
                setCRC(result, response);
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class PutObjectProcessReponseParser implements ResponseParser<PutObjectResult> {

        @Override
        public PutObjectResult parse(ResponseMessage response) throws ResponseParseException {
            PutObjectResult result = new PutObjectResult();
            result.setRequestId(response.getRequestId());
            result.setETag(trimQuotes(response.getHeaders().get(OSSHeaders.ETAG)));
            result.setCallbackResponseBody(response.getContent());
            result.setResponse(response);
            return result;
        }

    }

    public static final class AppendObjectResponseParser implements ResponseParser<AppendObjectResult> {

        @Override
        public AppendObjectResult parse(ResponseMessage response) throws ResponseParseException {
            AppendObjectResult result = new AppendObjectResult();
            result.setRequestId(response.getRequestId());
            try {
                String nextPosition = response.getHeaders().get(OSSHeaders.OSS_NEXT_APPEND_POSITION);
                if (nextPosition != null) {
                    result.setNextPosition(Long.valueOf(nextPosition));
                }
                result.setObjectCRC(response.getHeaders().get(OSSHeaders.OSS_HASH_CRC64_ECMA));
                setCRC(result, response);
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetObjectResponseParser implements ResponseParser<OSSObject> {
        private String bucketName;
        private String key;

        public GetObjectResponseParser(final String bucketName, final String key) {
            this.bucketName = bucketName;
            this.key = key;
        }

        @Override
        public OSSObject parse(ResponseMessage response) throws ResponseParseException {
            OSSObject ossObject = new OSSObject();
            ossObject.setBucketName(this.bucketName);
            ossObject.setKey(this.key);
            ossObject.setObjectContent(response.getContent());
            ossObject.setRequestId(response.getRequestId());
            ossObject.setResponse(response);
            try {
                ossObject.setObjectMetadata(parseObjectMetadata(response.getHeaders()));
                setServerCRC(ossObject, response);
                return ossObject;
            } catch (ResponseParseException e) {
                // Close response only when parsing exception thrown. Otherwise,
                // just hand over to SDK users and remain them close it when no
                // longer in use.
                safeCloseResponse(response);

                // Rethrow
                throw e;
            }
        }

    }

    public static final class GetUdfApplicationLogResponseParser implements ResponseParser<UdfApplicationLog> {

        public GetUdfApplicationLogResponseParser(String udfName) {
            super();
            this.udfName = udfName;
        }

        @Override
        public UdfApplicationLog parse(ResponseMessage response) throws ResponseParseException {
            UdfApplicationLog appLog = new UdfApplicationLog(udfName);
            appLog.setLogContent(response.getContent());
            appLog.setRequestId(response.getRequestId());
            appLog.setResponse(response);
            setServerCRC(appLog, response);
            return appLog;
        }

        private String udfName;

    }

    public static final class GetObjectAclResponseParser implements ResponseParser<ObjectAcl> {

        @Override
        public ObjectAcl parse(ResponseMessage response) throws ResponseParseException {
            try {
                ObjectAcl result = parseGetObjectAcl(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetSimplifiedObjectMetaResponseParser implements ResponseParser<SimplifiedObjectMeta> {

        @Override
        public SimplifiedObjectMeta parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseSimplifiedObjectMeta(response.getHeaders());
            } finally {
                OSSUtils.mandatoryCloseResponse(response);
            }
        }

    }

    public static final class RestoreObjectResponseParser implements ResponseParser<RestoreObjectResult> {

        @Override
        public RestoreObjectResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                RestoreObjectResult result = new RestoreObjectResult(response.getStatusCode());
                result.setRequestId(response.getRequestId());
                result.setVersionId(response.getVersionId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ProcessObjectResponseParser implements ResponseParser<GenericResult> {

        @Override
        public GenericResult parse(ResponseMessage response) throws ResponseParseException {
            GenericResult result = new PutObjectResult();
            result.setRequestId(response.getRequestId());
            result.setResponse(response);
            return result;
        }

    }

    public static final class GetObjectMetadataResponseParser implements ResponseParser<ObjectMetadata> {

        @Override
        public ObjectMetadata parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseObjectMetadata(response.getHeaders());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class CopyObjectResponseParser implements ResponseParser<CopyObjectResult> {

        @Override
        public CopyObjectResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                CopyObjectResult result = parseCopyObjectResult(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class DeleteObjectsResponseParser implements ResponseParser<DeleteObjectsResult> {

        @Override
        public DeleteObjectsResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                DeleteObjectsResult result = null;

                // Occurs when deleting multiple objects in quiet mode.
                if (response.getContentLength() == 0) {
                    result = new DeleteObjectsResult(null);
                } else {
                    result = parseDeleteObjectsResult(response.getContent());
                }
                result.setRequestId(response.getRequestId());

                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class CompleteMultipartUploadResponseParser
            implements ResponseParser<CompleteMultipartUploadResult> {

        @Override
        public CompleteMultipartUploadResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                CompleteMultipartUploadResult result = parseCompleteMultipartUpload(response.getContent());
                result.setRequestId(response.getRequestId());
                setServerCRC(result, response);
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class CompleteMultipartUploadProcessResponseParser
            implements ResponseParser<CompleteMultipartUploadResult> {

        @Override
        public CompleteMultipartUploadResult parse(ResponseMessage response) throws ResponseParseException {
            CompleteMultipartUploadResult result = new CompleteMultipartUploadResult();
            result.setRequestId(response.getRequestId());
            result.setCallbackResponseBody(response.getContent());
            result.setResponse(response);
            return result;
        }

    }

    public static final class InitiateMultipartUploadResponseParser
            implements ResponseParser<InitiateMultipartUploadResult> {

        @Override
        public InitiateMultipartUploadResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                InitiateMultipartUploadResult result = parseInitiateMultipartUpload(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListMultipartUploadsResponseParser implements ResponseParser<MultipartUploadListing> {

        @Override
        public MultipartUploadListing parse(ResponseMessage response) throws ResponseParseException {
            try {
                MultipartUploadListing result = parseListMultipartUploads(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListPartsResponseParser implements ResponseParser<PartListing> {

        @Override
        public PartListing parse(ResponseMessage response) throws ResponseParseException {
            try {
                PartListing result = parseListParts(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class UploadPartCopyResponseParser implements ResponseParser<UploadPartCopyResult> {

        private int partNumber;

        public UploadPartCopyResponseParser(int partNumber) {
            this.partNumber = partNumber;
        }

        @Override
        public UploadPartCopyResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                UploadPartCopyResult result = new UploadPartCopyResult();
                result.setPartNumber(partNumber);
                result.setETag(trimQuotes(parseUploadPartCopy(response.getContent())));
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetSymbolicLinkResponseParser implements ResponseParser<OSSSymlink> {

        @Override
        public OSSSymlink parse(ResponseMessage response) throws ResponseParseException {
            try {
                OSSSymlink result = parseSymbolicLink(response);
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                OSSUtils.mandatoryCloseResponse(response);
            }
        }

    }

    public static final class GetUdfInfoResponseParser implements ResponseParser<UdfInfo> {

        @Override
        public UdfInfo parse(ResponseMessage response) throws ResponseParseException {
            try {
                UdfInfo result = parseGetUdfInfo(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                OSSUtils.mandatoryCloseResponse(response);
            }
        }

    }

    public static final class ListUdfResponseParser implements ResponseParser<List<UdfInfo>> {

        @Override
        public List<UdfInfo> parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseListUdf(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetUdfImageInfoResponseParser implements ResponseParser<List<UdfImageInfo>> {

        @Override
        public List<UdfImageInfo> parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetUdfImageInfo(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetUdfApplicationInfoResponseParser implements ResponseParser<UdfApplicationInfo> {

        @Override
        public UdfApplicationInfo parse(ResponseMessage response) throws ResponseParseException {
            try {
                UdfApplicationInfo result = parseGetUdfApplicationInfo(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListUdfApplicationInfoResponseParser implements ResponseParser<List<UdfApplicationInfo>> {

        @Override
        public List<UdfApplicationInfo> parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseListUdfApplicationInfo(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }


    public static final class GetBucketRequestPaymentResponseParser implements ResponseParser<RequestPayer> {

        @Override
        public RequestPayer parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetBucketRequestPayment(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static final class InitiateWormConfigurationResponseParser implements ResponseParser<String> {

        @Override
        public String parse(ResponseMessage response) throws ResponseParseException {
            try {
                return response.getHeaders().get("x-oss-worm-id");
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static final class GetWormConfigurationResponseParser implements ResponseParser<WormConfiguration> {

        @Override
        public WormConfiguration parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetWormConfiguration(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static <ResultType extends GenericResult> void setCRC(ResultType result, ResponseMessage response) {
        InputStream inputStream = response.getRequest().getContent();
        if (inputStream instanceof CheckedInputStream) {
            CheckedInputStream checkedInputStream = (CheckedInputStream) inputStream;
            result.setClientCRC(checkedInputStream.getChecksum().getValue());
        }

        String strSrvCrc = response.getHeaders().get(OSSHeaders.OSS_HASH_CRC64_ECMA);
        if (strSrvCrc != null) {
            BigInteger bi = new BigInteger(strSrvCrc);
            result.setServerCRC(bi.longValue());
        }
    }

    public static <ResultType extends GenericResult> void setServerCRC(ResultType result, ResponseMessage response) {
        String strSrvCrc = response.getHeaders().get(OSSHeaders.OSS_HASH_CRC64_ECMA);
        if (strSrvCrc != null) {
            BigInteger bi = new BigInteger(strSrvCrc);
            result.setServerCRC(bi.longValue());
        }
    }

    private static Element getXmlRootElement(InputStream responseBody) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(responseBody);
        return doc.getRootElement();
    }

    /**
     * Unmarshall list objects response body to object listing.
     */
    @SuppressWarnings("unchecked")
    public static ObjectListing parseListObjects(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            ObjectListing objectListing = new ObjectListing();
            objectListing.setBucketName(root.getChildText("Name"));
            objectListing.setMaxKeys(Integer.valueOf(root.getChildText("MaxKeys")));
            objectListing.setTruncated(Boolean.valueOf(root.getChildText("IsTruncated")));

            if (root.getChild("Prefix") != null) {
                String prefix = root.getChildText("Prefix");
                objectListing.setPrefix(isNullOrEmpty(prefix) ? null : prefix);
            }

            if (root.getChild("Marker") != null) {
                String marker = root.getChildText("Marker");
                objectListing.setMarker(isNullOrEmpty(marker) ? null : marker);
            }

            if (root.getChild("Delimiter") != null) {
                String delimiter = root.getChildText("Delimiter");
                objectListing.setDelimiter(isNullOrEmpty(delimiter) ? null : delimiter);
            }

            if (root.getChild("NextMarker") != null) {
                String nextMarker = root.getChildText("NextMarker");
                objectListing.setNextMarker(isNullOrEmpty(nextMarker) ? null : nextMarker);
            }

            if (root.getChild("EncodingType") != null) {
                String encodingType = root.getChildText("EncodingType");
                objectListing.setEncodingType(isNullOrEmpty(encodingType) ? null : encodingType);
            }

            List<Element> objectSummaryElems = root.getChildren("Contents");
            for (Element elem : objectSummaryElems) {
                OSSObjectSummary ossObjectSummary = new OSSObjectSummary();

                ossObjectSummary.setKey(elem.getChildText("Key"));
                ossObjectSummary.setETag(trimQuotes(elem.getChildText("ETag")));
                String type = trimQuotes(elem.getChildText("Type"));
                if (!StringUtils.isNullOrEmpty(type)) {
                    ossObjectSummary.setType(ObjectTypeList.parse(type));
                }
                ossObjectSummary.setLastModified(DateUtil.parseIso8601Date(elem.getChildText("LastModified")));
                ossObjectSummary.setSize(Long.valueOf(elem.getChildText("Size")));
                ossObjectSummary.setStorageClass(elem.getChildText("StorageClass"));
                ossObjectSummary.setBucketName(objectListing.getBucketName());

                String id = elem.getChild("Owner").getChildText("ID");
                String displayName = elem.getChild("Owner").getChildText("DisplayName");
                ossObjectSummary.setOwner(new Owner(id, displayName));

                objectListing.addObjectSummary(ossObjectSummary);
            }

            List<Element> commonPrefixesElems = root.getChildren("CommonPrefixes");
            for (Element elem : commonPrefixesElems) {
                String prefix = elem.getChildText("Prefix");
                if (!isNullOrEmpty(prefix)) {
                    objectListing.addCommonPrefix(prefix);
                }
            }

            return objectListing;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    public static ObjectVersionsListing parseListObjectVersions(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            ObjectVersionsListing objectListing = new ObjectVersionsListing();
            objectListing.setBucketName(root.getChildText("Name"));
            objectListing.setMaxKeys(Integer.valueOf(root.getChildText("MaxKeys")));
            objectListing.setTruncated(Boolean.valueOf(root.getChildText("IsTruncated")));

            if (root.getChild("Prefix") != null) {
                String prefix = root.getChildText("Prefix");
                objectListing.setPrefix(isNullOrEmpty(prefix) ? null : prefix);
            }

            if (root.getChild("KeyMarker") != null) {
                String keyMarker = root.getChildText("KeyMarker");
                objectListing.setKeyMarker(isNullOrEmpty(keyMarker) ? null : keyMarker);
            }

            if (root.getChild("VersionIdMarker") != null) {
                String versionIdMarker = root.getChildText("VersionIdMarker");
                objectListing.setVersionIdMarker(isNullOrEmpty(versionIdMarker) ? null : versionIdMarker);
            }

            if (root.getChild("Delimiter") != null) {
                String delimiter = root.getChildText("Delimiter");
                objectListing.setDelimiter(isNullOrEmpty(delimiter) ? null : delimiter);
            }

            if (root.getChild("NextKeyMarker") != null) {
                String nextKeyMarker = root.getChildText("NextKeyMarker");
                objectListing.setNextKeyMarker(isNullOrEmpty(nextKeyMarker) ? null : nextKeyMarker);
            }

            if (root.getChild("NextVersionIdMarker") != null) {
                String nextVersionIdMarker = root.getChildText("NextVersionIdMarker");
                objectListing.setNextVersionIdMarker(isNullOrEmpty(nextVersionIdMarker) ? null : nextVersionIdMarker);
            }

            if (root.getChild("EncodingType") != null) {
                String encodingType = root.getChildText("EncodingType");
                objectListing.setEncodingType(isNullOrEmpty(encodingType) ? null : encodingType);
            }

            List<Element> versionElems = root.getChildren();
            for (Element elem: versionElems) {
                if (elem.getName().equals("Version") || elem.getName().equals("DeleteMarker")) {
                    OSSObjectVersionSummary ossObjectSummary = new OSSObjectVersionSummary();
                    ossObjectSummary.setKey(elem.getChildText("Key"));
                    ossObjectSummary.setVersionId(elem.getChildText("VersionId"));
                    ossObjectSummary.setIsLatest(elem.getChildText("IsLatest"));
                    ossObjectSummary.setETag(trimQuotes(elem.getChildText("ETag")));
                    String type = trimQuotes(elem.getChildText("Type"));
                    if (!StringUtils.isNullOrEmpty(type)) {
                        ossObjectSummary.setType(ObjectTypeList.parse(type));
                    }
                    ossObjectSummary.setLastModified(DateUtil.parseIso8601Date(elem.getChildText("LastModified")));
                    if (elem.getChildText("Size") != null) {
                        ossObjectSummary.setSize(Long.valueOf(elem.getChildText("Size")));
                    }
                    ossObjectSummary.setStorageClass(elem.getChildText("StorageClass"));
                    ossObjectSummary.setBucketName(objectListing.getBucketName());

                    String id = elem.getChild("Owner").getChildText("ID");
                    String displayName = elem.getChild("Owner").getChildText("DisplayName");
                    ossObjectSummary.setOwner(new Owner(id, displayName));

                    if (elem.getName().equals("Version")) {
                        ossObjectSummary.setDeleteMarker(false);
                    }

                    if (elem.getName().equals("DeleteMarker")) {
                        ossObjectSummary.setDeleteMarker(true);
                    }
                    objectListing.addObjectSummary(ossObjectSummary);
                }
            }

            List<Element> commonPrefixesElems = root.getChildren("CommonPrefixes");
            for (Element elem : commonPrefixesElems) {
                String prefix = elem.getChildText("Prefix");
                if (!isNullOrEmpty(prefix)) {
                    objectListing.addCommonPrefix(prefix);
                }
            }

            return objectListing;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }
    /**
     * Unmarshall get bucket acl response body to ACL.
     */
    public static AccessControlList parseGetBucketAcl(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            AccessControlList acl = new AccessControlList();

            String id = root.getChild("Owner").getChildText("ID");
            String displayName = root.getChild("Owner").getChildText("DisplayName");
            Owner owner = new Owner(id, displayName);
            acl.setOwner(owner);

            String aclString = root.getChild("AccessControlList").getChildText("Grant");
            CannedAccessControlList cacl = CannedAccessControlList.parse(aclString);
            acl.setCannedACL(cacl);

            switch (cacl) {
            case PublicRead:
                acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
                break;
            case PublicReadWrite:
                acl.grantPermission(GroupGrantee.AllUsers, Permission.FullControl);
                break;
            default:
                break;
            }

            return acl;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall object acl response body to object ACL.
     */
    public static ObjectAcl parseGetObjectAcl(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            ObjectAcl acl = new ObjectAcl();

            String id = root.getChild("Owner").getChildText("ID");
            String displayName = root.getChild("Owner").getChildText("DisplayName");
            Owner owner = new Owner(id, displayName);
            acl.setOwner(owner);

            String grantString = root.getChild("AccessControlList").getChildText("Grant");
            acl.setPermission(ObjectPermission.parsePermission(grantString));

            return acl;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket referer response body to bucket referer list.
     */
    @SuppressWarnings("unchecked")
    public static BucketReferer parseGetBucketReferer(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            boolean allowEmptyReferer = Boolean.valueOf(root.getChildText("AllowEmptyReferer"));
            List<String> refererList = new ArrayList<String>();
            if (root.getChild("RefererList") != null) {
                Element refererListElem = root.getChild("RefererList");
                List<Element> refererElems = refererListElem.getChildren("Referer");
                if (refererElems != null && !refererElems.isEmpty()) {
                    for (Element e : refererElems) {
                        refererList.add(e.getText());
                    }
                }
            }
            return new BucketReferer(allowEmptyReferer, refererList);
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall upload part copy response body to uploaded part's ETag.
     */
    public static String parseUploadPartCopy(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            return root.getChildText("ETag");
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall list user regions response body to region list.
     */
    @SuppressWarnings("unchecked")
    public static ListUserRegionsResult parseListUserRegion(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            ListUserRegionsResult result = new ListUserRegionsResult();

            List<Region> regions = new ArrayList<Region>();
            if (root.getChild("Regions") != null) {
                List<Element> bucketElems = root.getChild("Regions").getChildren("Region");
                for (Element e : bucketElems) {
                    Region region = new Region();
                    region.setRegion(e.getText());
                    regions.add(region);
                }
            }
            result.setRegions(regions);

            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall list bucket response body to bucket list.
     */
    @SuppressWarnings("unchecked")
    public static BucketList parseListBucket(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            BucketList bucketList = new BucketList();
            if (root.getChild("Prefix") != null) {
                bucketList.setPrefix(root.getChildText("Prefix"));
            }
            if (root.getChild("Marker") != null) {
                bucketList.setMarker(root.getChildText("Marker"));
            }
            if (root.getChild("MaxKeys") != null) {
                String value = root.getChildText("MaxKeys");
                bucketList.setMaxKeys(isNullOrEmpty(value) ? null : Integer.valueOf(value));
            }
            if (root.getChild("IsTruncated") != null) {
                String value = root.getChildText("IsTruncated");
                bucketList.setTruncated(isNullOrEmpty(value) ? false : Boolean.valueOf(value));
            }
            if (root.getChild("NextMarker") != null) {
                bucketList.setNextMarker(root.getChildText("NextMarker"));
            }

            Element ownerElem = root.getChild("Owner");
            String id = ownerElem.getChildText("ID");
            String displayName = ownerElem.getChildText("DisplayName");
            Owner owner = new Owner(id, displayName);

            List<Bucket> buckets = new ArrayList<Bucket>();
            if (root.getChild("Buckets") != null) {
                List<Element> bucketElems = root.getChild("Buckets").getChildren("Bucket");
                for (Element e : bucketElems) {
                    Bucket bucket = new Bucket();
                    bucket.setOwner(owner);
                    bucket.setName(e.getChildText("Name"));
                    bucket.setLocation(e.getChildText("Location"));
                    bucket.setCreationDate(DateUtil.parseIso8601Date(e.getChildText("CreationDate")));
                    if (e.getChild("StorageClass") != null) {
                        bucket.setStorageClass(StorageClass.parse(e.getChildText("StorageClass")));
                    }
                    bucket.setExtranetEndpoint(e.getChildText("ExtranetEndpoint"));
                    bucket.setIntranetEndpoint(e.getChildText("IntranetEndpoint"));

                    Element taggingElement = e.getChild("Tagging");

                    if (taggingElement != null) {
                        Element tagSetElement = taggingElement.getChild("TagSet");
                        if (tagSetElement != null) {
                            TagSet tagSet = new TagSet();
                            List<Element> tagElems = tagSetElement.getChildren("Tag");

                            processTaggingElem(tagSet, tagElems);

                            bucket.setTagSet(tagSet);
                        }
                    }

                    buckets.add(bucket);
                }
            }
            bucketList.setBucketList(buckets);

            return bucketList;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    private static void processTaggingElem(TagSet tagSet, List<Element> tagElems) {
        for (Element tagElem : tagElems) {
            String key = null;
            String value = null;

            if (tagElem.getChild("Key") != null) {
                key = tagElem.getChildText("Key");
            }

            if (tagElem.getChild("Value") != null) {
                value = tagElem.getChildText("Value");
            }

            tagSet.setTag(key, value);
        }
    }

    /**
     * Unmarshall list image style response body to style list.
     */
    @SuppressWarnings("unchecked")
    public static List<Style> parseListImageStyle(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            List<Style> styleList = new ArrayList<Style>();
            List<Element> styleElems = root.getChildren("Style");
            for (Element e : styleElems) {
                Style style = new Style();
                style.SetStyleName(e.getChildText("Name"));
                style.SetStyle(e.getChildText("Content"));
                style.SetLastModifyTime(DateUtil.parseRfc822Date(e.getChildText("LastModifyTime")));
                style.SetCreationDate(DateUtil.parseRfc822Date(e.getChildText("CreateTime")));
                styleList.add(style);
            }
            return styleList;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall list bucket response body to bucket list.
     */
    @SuppressWarnings("unchecked")
    public static BucketVpcIdList parseListBucketVpcId(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            BucketVpcIdList bucketVpcIdList = new BucketVpcIdList();
            List<BucketVpcIdList.VpcInfo> vpcInfos = new ArrayList<BucketVpcIdList.VpcInfo>();
            List<Element> vpcInfoElems = root.getChildren("VpcInfo");
            for (Element e : vpcInfoElems) {
                BucketVpcIdList.VpcInfo vpcInfo = new BucketVpcIdList.VpcInfo();
                if (e.getChildren("VpcRegion") != null) {
                    vpcInfo.setVpcRegion(e.getChildText("VpcRegion"));
                }
                if (e.getChildren("TunnelId") != null) {
                    vpcInfo.setTunnelId(e.getChildText("TunnelId"));
                }
                if (e.getChildren("VpcId") != null) {
                    vpcInfo.setVpcId(e.getChildText("VpcId"));
                }
                if (e.getChildren("VpcTag") != null) {
                    vpcInfo.setVpcTag(e.getChildText("VpcTag"));
                }
                vpcInfos.add(vpcInfo);
            }

            bucketVpcIdList.setList(vpcInfos);
            return bucketVpcIdList;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall get bucket location response body to bucket location.
     */
    public static String parseGetBucketLocation(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            return root.getText();
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall bucket metadata from response headers.
     */
    public static BucketMetadata parseBucketMetadata(Map<String, String> headers) throws ResponseParseException {

        try {
            BucketMetadata bucketMetadata = new BucketMetadata();

            for (Iterator<String> it = headers.keySet().iterator(); it.hasNext();) {
                String key = it.next();

                if (key.equals(OSSHeaders.OSS_BUCKET_REGION)) {
                    bucketMetadata.setBucketRegion(headers.get(key));
                } else {
                    bucketMetadata.addHttpMetadata(key, headers.get(key));
                }
            }

            return bucketMetadata;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall simplified object meta from response headers.
     */
    public static SimplifiedObjectMeta parseSimplifiedObjectMeta(Map<String, String> headers)
            throws ResponseParseException {

        try {
            SimplifiedObjectMeta objectMeta = new SimplifiedObjectMeta();

            for (Iterator<String> it = headers.keySet().iterator(); it.hasNext();) {
                String key = it.next();

                if (key.equals(OSSHeaders.LAST_MODIFIED)) {
                    try {
                        objectMeta.setLastModified(DateUtil.parseRfc822Date(headers.get(key)));
                    } catch (ParseException pe) {
                        throw new ResponseParseException(pe.getMessage(), pe);
                    }
                } else if (key.equals(OSSHeaders.CONTENT_LENGTH)) {
                    Long value = Long.valueOf(headers.get(key));
                    objectMeta.setSize(value);
                } else if (key.equals(OSSHeaders.ETAG)) {
                    objectMeta.setETag(trimQuotes(headers.get(key)));
                } else if (key.equals(OSSHeaders.OSS_HEADER_REQUEST_ID)) {
                    objectMeta.setRequestId(headers.get(key));
                }
            }

            return objectMeta;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall symlink link from response headers.
     */
    public static OSSSymlink parseSymbolicLink(ResponseMessage response) throws ResponseParseException {

        try {
            OSSSymlink smyLink = null;

            String targetObject = response.getHeaders().get(OSSHeaders.OSS_HEADER_SYMLINK_TARGET);
            if (targetObject != null) {
                targetObject = HttpUtil.urlDecode(targetObject, "UTF-8");
                smyLink = new OSSSymlink(null, targetObject);
            }

            smyLink.setMetadata(parseObjectMetadata(response.getHeaders()));

            return smyLink;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get udf info response body to udf info.
     */
    public static UdfInfo parseGetUdfInfo(InputStream responseBody) throws ResponseParseException {
        try {
            Element root = getXmlRootElement(responseBody);

            String name = root.getChildText("Name");
            String id = root.getChildText("ID");
            String owner = root.getChildText("Owner");
            String desc = root.getChildText("Description");
            Date date = DateUtil.parseIso8601Date(root.getChildText("CreationDate"));
            CannedUdfAcl acl = CannedUdfAcl.parse(root.getChildText("ACL"));

            return new UdfInfo(name, owner, id, desc, acl, date);
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall list udf response body to udf info list.
     */
    @SuppressWarnings("unchecked")
    public static List<UdfInfo> parseListUdf(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            List<UdfInfo> udfs = new ArrayList<UdfInfo>();

            if (root.getChild("UDFInfo") != null) {
                List<Element> udfElems = root.getChildren("UDFInfo");
                for (Element elem : udfElems) {
                    String name = elem.getChildText("Name");
                    String id = elem.getChildText("ID");
                    String owner = elem.getChildText("Owner");
                    String desc = elem.getChildText("Description");
                    Date date = DateUtil.parseIso8601Date(elem.getChildText("CreationDate"));
                    CannedUdfAcl acl = CannedUdfAcl.parse(elem.getChildText("ACL"));
                    udfs.add(new UdfInfo(name, owner, id, desc, acl, date));
                }
            }

            return udfs;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get udf info response body to udf image info list.
     */
    @SuppressWarnings("unchecked")
    public static List<UdfImageInfo> parseGetUdfImageInfo(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            List<UdfImageInfo> udfImages = new ArrayList<UdfImageInfo>();

            if (root.getChild("Item") != null) {
                List<Element> udfImageElems = root.getChildren("Item");
                for (Element elem : udfImageElems) {
                    Integer version = Integer.valueOf(elem.getChildText("Version"));
                    String status = elem.getChildText("Status");
                    String region = elem.getChildText("CanonicalRegion");
                    String desc = elem.getChildText("Description");
                    Date date = DateUtil.parseIso8601Date(elem.getChildText("CreationDate"));
                    udfImages.add(new UdfImageInfo(version, status, desc, region, date));
                }
            }

            return udfImages;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get udf info response body to udf application info.
     */
    public static UdfApplicationInfo parseGetUdfApplicationInfo(InputStream responseBody)
            throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            String id = root.getChildText("ID");
            String name = root.getChildText("Name");
            String status = root.getChildText("Status");
            String region = root.getChildText("Region");
            Integer imageVersion = Integer.valueOf(root.getChildText("ImageVersion"));
            Integer instanceNum = Integer.valueOf(root.getChildText("InstanceNum"));
            Date date = DateUtil.parseIso8601Date(root.getChildText("CreationDate"));

            String instanceType = root.getChild("Flavor").getChildText("InstanceType");
            InstanceFlavor flavor = new InstanceFlavor(instanceType);

            return new UdfApplicationInfo(name, id, region, status, imageVersion, instanceNum, date, flavor);
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get udf info response body to udf image info list.
     */
    @SuppressWarnings("unchecked")
    public static List<UdfApplicationInfo> parseListUdfApplicationInfo(InputStream responseBody)
            throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            List<UdfApplicationInfo> udfApps = new ArrayList<UdfApplicationInfo>();

            if (root.getChild("UDFApplicationInfo") != null) {
                List<Element> udfImageElems = root.getChildren("UDFApplicationInfo");
                for (Element elem : udfImageElems) {
                    String id = elem.getChildText("ID");
                    String name = elem.getChildText("Name");
                    String status = elem.getChildText("Status");
                    String region = elem.getChildText("Region");
                    Integer imageVersion = Integer.valueOf(elem.getChildText("ImageVersion"));
                    Integer instanceNum = Integer.valueOf(elem.getChildText("InstanceNum"));
                    Date date = DateUtil.parseIso8601Date(elem.getChildText("CreationDate"));

                    String instanceType = elem.getChild("Flavor").getChildText("InstanceType");
                    InstanceFlavor flavor = new InstanceFlavor(instanceType);

                    UdfApplicationInfo udfApp = new UdfApplicationInfo(name, id, region, status, imageVersion,
                            instanceNum, date, flavor);
                    udfApps.add(udfApp);
                }
            }

            return udfApps;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall object metadata from response headers.
     */
    public static ObjectMetadata parseObjectMetadata(Map<String, String> headers) throws ResponseParseException {

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();

            for (Iterator<String> it = headers.keySet().iterator(); it.hasNext();) {
                String key = it.next();

                if (key.indexOf(OSSHeaders.OSS_USER_METADATA_PREFIX) >= 0) {
                    key = key.substring(OSSHeaders.OSS_USER_METADATA_PREFIX.length());
                    objectMetadata.addUserMetadata(key, headers.get(OSSHeaders.OSS_USER_METADATA_PREFIX + key));
                } else if (key.equals(OSSHeaders.LAST_MODIFIED) || key.equals(OSSHeaders.DATE)) {
                    try {
                        objectMetadata.setHeader(key, DateUtil.parseRfc822Date(headers.get(key)));
                    } catch (ParseException pe) {
                        throw new ResponseParseException(pe.getMessage(), pe);
                    }
                } else if (key.equals(OSSHeaders.CONTENT_LENGTH)) {
                    Long value = Long.valueOf(headers.get(key));
                    objectMetadata.setHeader(key, value);
                } else if (key.equals(OSSHeaders.ETAG)) {
                    objectMetadata.setHeader(key, trimQuotes(headers.get(key)));
                } else if (key.equals(OSSHeaders.OSS_HEADER_OBJECT_TAGGING_COUNT)) {
                    Integer value = Integer.valueOf(headers.get(key));
                    objectMetadata.setHeader(key, value);
                } else {
                    objectMetadata.setHeader(key, headers.get(key));
                }
            }

            return objectMetadata;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall initiate multipart upload response body to corresponding
     * result.
     */
    public static InitiateMultipartUploadResult parseInitiateMultipartUpload(InputStream responseBody)
            throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            InitiateMultipartUploadResult result = new InitiateMultipartUploadResult();
            if (root.getChild("Bucket") != null) {
                result.setBucketName(root.getChildText("Bucket"));
            }

            if (root.getChild("Key") != null) {
                result.setKey(root.getChildText("Key"));
            }

            if (root.getChild("UploadId") != null) {
                result.setUploadId(root.getChildText("UploadId"));
            }

            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall list multipart uploads response body to multipart upload
     * listing.
     */
    @SuppressWarnings("unchecked")
    public static MultipartUploadListing parseListMultipartUploads(InputStream responseBody)
            throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            MultipartUploadListing multipartUploadListing = new MultipartUploadListing();
            multipartUploadListing.setBucketName(root.getChildText("Bucket"));
            multipartUploadListing.setMaxUploads(Integer.valueOf(root.getChildText("MaxUploads")));
            multipartUploadListing.setTruncated(Boolean.valueOf(root.getChildText("IsTruncated")));

            if (root.getChild("Delimiter") != null) {
                String delimiter = root.getChildText("Delimiter");
                if (!isNullOrEmpty(delimiter)) {
                    multipartUploadListing.setDelimiter(delimiter);
                }
            }

            if (root.getChild("Prefix") != null) {
                String prefix = root.getChildText("Prefix");
                if (!isNullOrEmpty(prefix)) {
                    multipartUploadListing.setPrefix(prefix);
                }
            }

            if (root.getChild("KeyMarker") != null) {
                String keyMarker = root.getChildText("KeyMarker");
                if (!isNullOrEmpty(keyMarker)) {
                    multipartUploadListing.setKeyMarker(keyMarker);
                }
            }

            if (root.getChild("UploadIdMarker") != null) {
                String uploadIdMarker = root.getChildText("UploadIdMarker");
                if (!isNullOrEmpty(uploadIdMarker)) {
                    multipartUploadListing.setUploadIdMarker(uploadIdMarker);
                }
            }

            if (root.getChild("NextKeyMarker") != null) {
                String nextKeyMarker = root.getChildText("NextKeyMarker");
                if (!isNullOrEmpty(nextKeyMarker)) {
                    multipartUploadListing.setNextKeyMarker(nextKeyMarker);
                }
            }

            if (root.getChild("NextUploadIdMarker") != null) {
                String nextUploadIdMarker = root.getChildText("NextUploadIdMarker");
                if (!isNullOrEmpty(nextUploadIdMarker)) {
                    multipartUploadListing.setNextUploadIdMarker(nextUploadIdMarker);
                }
            }

            List<Element> uploadElems = root.getChildren("Upload");
            for (Element elem : uploadElems) {
                if (elem.getChild("Initiated") == null) {
                    continue;
                }

                MultipartUpload mu = new MultipartUpload();
                mu.setKey(elem.getChildText("Key"));
                mu.setUploadId(elem.getChildText("UploadId"));
                mu.setStorageClass(elem.getChildText("StorageClass"));
                mu.setInitiated(DateUtil.parseIso8601Date(elem.getChildText("Initiated")));
                multipartUploadListing.addMultipartUpload(mu);
            }

            List<Element> commonPrefixesElems = root.getChildren("CommonPrefixes");
            for (Element elem : commonPrefixesElems) {
                String prefix = elem.getChildText("Prefix");
                if (!isNullOrEmpty(prefix)) {
                    multipartUploadListing.addCommonPrefix(prefix);
                }
            }

            return multipartUploadListing;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall list parts response body to part listing.
     */
    @SuppressWarnings("unchecked")
    public static PartListing parseListParts(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            PartListing partListing = new PartListing();
            partListing.setBucketName(root.getChildText("Bucket"));
            partListing.setKey(root.getChildText("Key"));
            partListing.setUploadId(root.getChildText("UploadId"));
            partListing.setStorageClass(root.getChildText("StorageClass"));
            partListing.setMaxParts(Integer.valueOf(root.getChildText("MaxParts")));
            partListing.setTruncated(Boolean.valueOf(root.getChildText("IsTruncated")));

            if (root.getChild("PartNumberMarker") != null) {
                String partNumberMarker = root.getChildText("PartNumberMarker");
                if (!isNullOrEmpty(partNumberMarker)) {
                    partListing.setPartNumberMarker(Integer.valueOf(partNumberMarker));
                }
            }

            if (root.getChild("NextPartNumberMarker") != null) {
                String nextPartNumberMarker = root.getChildText("NextPartNumberMarker");
                if (!isNullOrEmpty(nextPartNumberMarker)) {
                    partListing.setNextPartNumberMarker(Integer.valueOf(nextPartNumberMarker));
                }
            }

            List<Element> partElems = root.getChildren("Part");
            for (Element elem : partElems) {
                PartSummary ps = new PartSummary();

                ps.setPartNumber(Integer.valueOf(elem.getChildText("PartNumber")));
                ps.setLastModified(DateUtil.parseIso8601Date(elem.getChildText("LastModified")));
                ps.setETag(trimQuotes(elem.getChildText("ETag")));
                ps.setSize(Integer.valueOf(elem.getChildText("Size")));

                partListing.addPart(ps);
            }

            return partListing;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall complete multipart upload response body to corresponding
     * result.
     */
    public static CompleteMultipartUploadResult parseCompleteMultipartUpload(InputStream responseBody)
            throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            CompleteMultipartUploadResult result = new CompleteMultipartUploadResult();
            result.setBucketName(root.getChildText("Bucket"));
            result.setETag(trimQuotes(root.getChildText("ETag")));
            result.setKey(root.getChildText("Key"));
            result.setLocation(root.getChildText("Location"));

            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket logging response body to corresponding result.
     */
    public static BucketLoggingResult parseBucketLogging(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            BucketLoggingResult result = new BucketLoggingResult();
            if (root.getChild("LoggingEnabled") != null) {
                result.setTargetBucket(root.getChild("LoggingEnabled").getChildText("TargetBucket"));
            }
            if (root.getChild("LoggingEnabled") != null) {
                result.setTargetPrefix(root.getChild("LoggingEnabled").getChildText("TargetPrefix"));
            }

            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket image response body to corresponding result.
     */
    public static GetBucketImageResult parseBucketImage(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            GetBucketImageResult result = new GetBucketImageResult();
            result.SetBucketName(root.getChildText("Name"));
            result.SetDefault404Pic(root.getChildText("Default404Pic"));
            result.SetStyleDelimiters(root.getChildText("StyleDelimiters"));
            result.SetStatus(root.getChildText("Status"));
            result.SetIsAutoSetContentType(root.getChildText("AutoSetContentType").equals("True"));
            result.SetIsForbidOrigPicAccess(root.getChildText("OrigPicForbidden").equals("True"));
            result.SetIsSetAttachName(root.getChildText("SetAttachName").equals("True"));
            result.SetIsUseStyleOnly(root.getChildText("UseStyleOnly").equals("True"));
            result.SetIsUseSrcFormat(root.getChildText("UseSrcFormat").equals("True"));
            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get image style response body to corresponding result.
     */
    public static GetImageStyleResult parseImageStyle(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            GetImageStyleResult result = new GetImageStyleResult();
            result.SetStyleName(root.getChildText("Name"));
            result.SetStyle(root.getChildText("Content"));
            result.SetLastModifyTime(DateUtil.parseRfc822Date(root.getChildText("LastModifyTime")));
            result.SetCreationDate(DateUtil.parseRfc822Date(root.getChildText("CreateTime")));
            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket process response body to bucket process.
     */
    public static BucketProcess parseGetBucketImageProcessConf(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            String compliedHost = root.getChildText("CompliedHost");
            boolean sourceFileProtect = false;
            if (root.getChildText("SourceFileProtect").equals("Enabled")) {
                sourceFileProtect = true;
            }
            String sourceFileProtectSuffix = root.getChildText("SourceFileProtectSuffix");
            String styleDelimiters = root.getChildText("StyleDelimiters");

            ImageProcess imageProcess = new ImageProcess(compliedHost, sourceFileProtect, sourceFileProtectSuffix,
                    styleDelimiters);
            if (root.getChildText("Version") != null) {
                imageProcess.setVersion(Integer.parseInt(root.getChildText("Version")));
            }

            return new BucketProcess(imageProcess);
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket website response body to corresponding result.
     */
    @SuppressWarnings("unchecked")
    public static BucketWebsiteResult parseBucketWebsite(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            BucketWebsiteResult result = new BucketWebsiteResult();
            if (root.getChild("IndexDocument") != null) {
                result.setIndexDocument(root.getChild("IndexDocument").getChildText("Suffix"));
            }
            if (root.getChild("ErrorDocument") != null) {
                result.setErrorDocument(root.getChild("ErrorDocument").getChildText("Key"));
            }
            if (root.getChild("RoutingRules") != null) {
                List<Element> ruleElements = root.getChild("RoutingRules").getChildren("RoutingRule");
                for (Element ruleElem : ruleElements) {
                    RoutingRule rule = new RoutingRule();

                    rule.setNumber(Integer.parseInt(ruleElem.getChildText("RuleNumber")));

                    Element condElem = ruleElem.getChild("Condition");
                    if (condElem != null) {
                        rule.getCondition().setKeyPrefixEquals(condElem.getChildText("KeyPrefixEquals"));
                        if (condElem.getChild("HttpErrorCodeReturnedEquals") != null) {
                            rule.getCondition().setHttpErrorCodeReturnedEquals(
                                Integer.parseInt(condElem.getChildText("HttpErrorCodeReturnedEquals")));
                        }
                        List<Element> includeHeadersElem = condElem.getChildren("IncludeHeader");
                        if (includeHeadersElem != null && includeHeadersElem.size() > 0) {
                            List<RoutingRule.IncludeHeader> includeHeaders = new ArrayList<RoutingRule.IncludeHeader>();
                            for (Element includeHeaderElem : includeHeadersElem) {
                                RoutingRule.IncludeHeader includeHeader = new RoutingRule.IncludeHeader();
                                includeHeader.setKey(includeHeaderElem.getChildText("Key"));
                                includeHeader.setEquals(includeHeaderElem.getChildText("Equals"));
                                includeHeader.setStartsWith(includeHeaderElem.getChildText("StartsWith"));
                                includeHeader.setEndsWith(includeHeaderElem.getChildText("EndsWith"));
                                includeHeaders.add(includeHeader);
                            }
                            rule.getCondition().setIncludeHeaders(includeHeaders);
                        }
                    }

                    Element redirectElem = ruleElem.getChild("Redirect");
                    if (redirectElem.getChild("RedirectType") != null) {
                        rule.getRedirect().setRedirectType(
                                RoutingRule.RedirectType.parse(redirectElem.getChildText("RedirectType")));
                    }
                    rule.getRedirect().setHostName(redirectElem.getChildText("HostName"));
                    if (redirectElem.getChild("Protocol") != null) {
                        rule.getRedirect()
                                .setProtocol(RoutingRule.Protocol.parse(redirectElem.getChildText("Protocol")));
                    }
                    rule.getRedirect().setReplaceKeyPrefixWith(redirectElem.getChildText("ReplaceKeyPrefixWith"));
                    rule.getRedirect().setReplaceKeyWith(redirectElem.getChildText("ReplaceKeyWith"));
                    if (redirectElem.getChild("HttpRedirectCode") != null) {
                        rule.getRedirect()
                                .setHttpRedirectCode(Integer.parseInt(redirectElem.getChildText("HttpRedirectCode")));
                    }
                    rule.getRedirect().setMirrorURL(redirectElem.getChildText("MirrorURL"));
                    rule.getRedirect().setMirrorSecondaryURL(redirectElem.getChildText("MirrorURLSlave"));
                    rule.getRedirect().setMirrorProbeURL(redirectElem.getChildText("MirrorURLProbe"));
                    if (redirectElem.getChildText("MirrorPassQueryString") != null) {
                        rule.getRedirect().setMirrorPassQueryString(
                                Boolean.valueOf(redirectElem.getChildText("MirrorPassQueryString")));
                    }
                    if (redirectElem.getChildText("MirrorPassOriginalSlashes") != null) {
                        rule.getRedirect().setMirrorPassOriginalSlashes(
                                Boolean.valueOf(redirectElem.getChildText("MirrorPassOriginalSlashes")));
                    }
                    if (redirectElem.getChildText("PassQueryString") != null) {
                        rule.getRedirect().setPassQueryString(
                            Boolean.valueOf(redirectElem.getChildText("PassQueryString")));
                    }
                    if (redirectElem.getChildText("MirrorFollowRedirect") != null) {
                        rule.getRedirect().setMirrorFollowRedirect(
                            Boolean.valueOf(redirectElem.getChildText("MirrorFollowRedirect")));
                    }
                    if (redirectElem.getChildText("MirrorUserLastModified") != null) {
                        rule.getRedirect().setMirrorUserLastModified(
                            Boolean.valueOf(redirectElem.getChildText("MirrorUserLastModified")));
                    }
                    if (redirectElem.getChildText("MirrorIsExpressTunnel") != null) {
                        rule.getRedirect().setMirrorIsExpressTunnel(
                            Boolean.valueOf(redirectElem.getChildText("MirrorIsExpressTunnel")));
                    }
                    if (redirectElem.getChildText("MirrorDstRegion") != null) {
                        rule.getRedirect().setMirrorDstRegion(
                            redirectElem.getChildText("MirrorDstRegion"));
                    }
                    if (redirectElem.getChildText("MirrorDstVpcId") != null) {
                        rule.getRedirect().setMirrorDstVpcId(
                            redirectElem.getChildText("MirrorDstVpcId"));
                    }

                    Element mirrorURLsElem = redirectElem.getChild("MirrorMultiAlternates");
                    if (mirrorURLsElem != null) {
                        List<Element> mirrorURLElementList = mirrorURLsElem.getChildren("MirrorMultiAlternate");
                        if (mirrorURLElementList != null && mirrorURLElementList.size() > 0) {
                            List<RoutingRule.Redirect.MirrorMultiAlternate> mirrorURLsList = new ArrayList<RoutingRule.Redirect.MirrorMultiAlternate>();
                            for (Element setElement : mirrorURLElementList) {
                                RoutingRule.Redirect.MirrorMultiAlternate mirrorMultiAlternate = new RoutingRule.Redirect.MirrorMultiAlternate();
                                mirrorMultiAlternate.setPrior(Integer.parseInt(setElement.getChildText("MirrorMultiAlternateNumber")));
                                mirrorMultiAlternate.setUrl(setElement.getChildText("MirrorMultiAlternateURL"));
                                mirrorURLsList.add(mirrorMultiAlternate);
                            }
                            rule.getRedirect().setMirrorMultiAlternates(mirrorURLsList);
                        }
                    }

                    Element mirrorHeadersElem = redirectElem.getChild("MirrorHeaders");
                    if (mirrorHeadersElem != null) {
                        RoutingRule.MirrorHeaders mirrorHeaders = new RoutingRule.MirrorHeaders();
                        mirrorHeaders.setPassAll(Boolean.valueOf(mirrorHeadersElem.getChildText("PassAll")));
                        mirrorHeaders.setPass(parseStringListFromElemet(mirrorHeadersElem.getChildren("Pass")));
                        mirrorHeaders.setRemove(parseStringListFromElemet(mirrorHeadersElem.getChildren("Remove")));
                        List<Element> setElementList = mirrorHeadersElem.getChildren("Set");
                        if (setElementList != null && setElementList.size() > 0) {
                            List<Map<String, String>> setList = new ArrayList<Map<String, String>>();
                            for (Element setElement : setElementList) {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("Key", setElement.getChildText("Key"));
                                map.put("Value", setElement.getChildText("Value"));
                                setList.add(map);
                            }
                            mirrorHeaders.setSet(setList);
                        }

                        rule.getRedirect().setMirrorHeaders(mirrorHeaders);
                    }

                    result.AddRoutingRule(rule);
                }
            }

            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * 转换ElementList to StringList
     *
     * @param elementList
     * @return
     */
    private static List<String> parseStringListFromElemet(List<Element> elementList) {
        if (elementList != null && elementList.size() > 0) {
            List<String> list = new ArrayList<String>();
            for (Element element : elementList) {
                list.add(element.getText());
            }
            return list;
        }
        return null;
    }

    /**
     * Unmarshall copy object response body to corresponding result.
     */
    public static CopyObjectResult parseCopyObjectResult(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            CopyObjectResult result = new CopyObjectResult();
            result.setLastModified(DateUtil.parseIso8601Date(root.getChildText("LastModified")));
            result.setEtag(trimQuotes(root.getChildText("ETag")));

            return result;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall delete objects response body to corresponding result.
     */
    @SuppressWarnings("unchecked")
    public static DeleteObjectsResult parseDeleteObjectsResult(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            DeleteObjectsResult deleteObjectsResult = new DeleteObjectsResult();
            if (root.getChild("EncodingType") != null) {
                String encodingType = root.getChildText("EncodingType");
                deleteObjectsResult.setEncodingType(isNullOrEmpty(encodingType) ? null : encodingType);
            }

            List<DeleteObjectsResult.DeletedObject> deletedObjects = new ArrayList<DeleteObjectsResult.DeletedObject>();
            List<Element> deletedElements = root.getChildren("Deleted");
            for (Element elem : deletedElements) {
                DeleteObjectsResult.DeletedObject deletedObject = new DeleteObjectsResult.DeletedObject();
                deletedObject.setKey(elem.getChildText("Key"));
                if(elem.getChildText("VersionId") != null) {
                    deletedObject.setVersionId(elem.getChildText("VersionId"));
                }

                if (elem.getChildText("DeleteMarker") != null) {
                    deletedObject.setDeleteMarker(Boolean.valueOf(elem.getChildText("DeleteMarker")));
                }
                if (elem.getChildText("DeleteMarkerVersionId") != null ) {
                    deletedObject.setDeleteMarkerVersionId(elem.getChildText("DeleteMarkerVersionId"));
                }
                deletedObjects.add(deletedObject);
            }
            deleteObjectsResult.setDeletedObjects(deletedObjects);


            return deleteObjectsResult;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket cors response body to cors rules.
     */
    @SuppressWarnings("unchecked")
    public static List<CORSRule> parseListBucketCORS(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            List<CORSRule> corsRules = new ArrayList<CORSRule>();
            List<Element> corsRuleElems = root.getChildren("CORSRule");

            for (Element corsRuleElem : corsRuleElems) {
                CORSRule rule = new CORSRule();

                List<Element> allowedOriginElems = corsRuleElem.getChildren("AllowedOrigin");
                for (Element allowedOriginElement : allowedOriginElems) {
                    rule.getAllowedOrigins().add(allowedOriginElement.getValue());
                }

                List<Element> allowedMethodElems = corsRuleElem.getChildren("AllowedMethod");
                for (Element allowedMethodElement : allowedMethodElems) {
                    rule.getAllowedMethods().add(allowedMethodElement.getValue());
                }

                List<Element> allowedHeaderElems = corsRuleElem.getChildren("AllowedHeader");
                for (Element allowedHeaderElement : allowedHeaderElems) {
                    rule.getAllowedHeaders().add(allowedHeaderElement.getValue());
                }

                List<Element> exposeHeaderElems = corsRuleElem.getChildren("ExposeHeader");
                for (Element exposeHeaderElement : exposeHeaderElems) {
                    rule.getExposeHeaders().add(exposeHeaderElement.getValue());
                }

                Element maxAgeSecondsElem = corsRuleElem.getChild("MaxAgeSeconds");
                if (maxAgeSecondsElem != null) {
                    rule.setMaxAgeSeconds(Integer.parseInt(maxAgeSecondsElem.getValue()));
                }

                corsRules.add(rule);
            }

            return corsRules;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket tagging response body to cors rules.
     */
    @SuppressWarnings("unchecked")
    public static TagSet parseGetBucketTagging(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            TagSet tagSet = new TagSet();
            List<Element> tagElems = root.getChild("TagSet").getChildren("Tag");

            processTaggingElem(tagSet, tagElems);

            return tagSet;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket replication response body to replication result.
     */
    @SuppressWarnings("unchecked")
    public static List<ReplicationRule> parseGetBucketReplication(InputStream responseBody)
            throws ResponseParseException {

        try {
            List<ReplicationRule> repRules = new ArrayList<ReplicationRule>();

            Element root = getXmlRootElement(responseBody);
            List<Element> ruleElems = root.getChildren("Rule");

            for (Element ruleElem : ruleElems) {
                ReplicationRule repRule = new ReplicationRule();

                repRule.setReplicationRuleID(ruleElem.getChildText("ID"));

                Element destination = ruleElem.getChild("Destination");
                repRule.setTargetBucketName(destination.getChildText("Bucket"));
                repRule.setTargetBucketLocation(destination.getChildText("Location"));

                repRule.setTargetCloud(destination.getChildText("Cloud"));
                repRule.setTargetCloudLocation(destination.getChildText("CloudLocation"));

                repRule.setReplicationStatus(ReplicationStatus.parse(ruleElem.getChildText("Status")));

                if (ruleElem.getChildText("HistoricalObjectReplication").equals("enabled")) {
                    repRule.setEnableHistoricalObjectReplication(true);
                } else {
                    repRule.setEnableHistoricalObjectReplication(false);
                }

                if (ruleElem.getChild("PrefixSet") != null) {
                    List<String> objectPrefixes = new ArrayList<String>();
                    List<Element> prefixElems = ruleElem.getChild("PrefixSet").getChildren("Prefix");
                    for (Element prefixElem : prefixElems) {
                        objectPrefixes.add(prefixElem.getText());
                    }
                    repRule.setObjectPrefixList(objectPrefixes);
                }

                if (ruleElem.getChild("Action") != null) {
                    String[] actionStrs = ruleElem.getChildText("Action").split(",");
                    List<ReplicationAction> repActions = new ArrayList<ReplicationAction>();
                    for (String actionStr : actionStrs) {
                        repActions.add(ReplicationAction.parse(actionStr));
                    }
                    repRule.setReplicationActionList(repActions);
                }

                repRules.add(repRule);
            }

            return repRules;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket replication response body to replication progress.
     */
    public static BucketReplicationProgress parseGetBucketReplicationProgress(InputStream responseBody)
            throws ResponseParseException {
        try {
            BucketReplicationProgress progress = new BucketReplicationProgress();

            Element root = getXmlRootElement(responseBody);
            Element ruleElem = root.getChild("Rule");

            progress.setReplicationRuleID(ruleElem.getChildText("ID"));

            Element destination = ruleElem.getChild("Destination");
            progress.setTargetBucketName(destination.getChildText("Bucket"));
            progress.setTargetBucketLocation(destination.getChildText("Location"));
            progress.setTargetCloud(destination.getChildText("Cloud"));
            progress.setTargetCloudLocation(destination.getChildText("CloudLocation"));

            progress.setReplicationStatus(ReplicationStatus.parse(ruleElem.getChildText("Status")));

            if (ruleElem.getChildText("HistoricalObjectReplication").equals("enabled")) {
                progress.setEnableHistoricalObjectReplication(true);
            } else {
                progress.setEnableHistoricalObjectReplication(false);
            }

            Element progressElem = ruleElem.getChild("Progress");
            if (progressElem != null) {
                if (progressElem.getChild("HistoricalObject") != null) {
                    progress.setHistoricalObjectProgress(
                            Float.parseFloat(progressElem.getChildText("HistoricalObject")));
                }
                progress.setNewObjectProgress(DateUtil.parseIso8601Date(progressElem.getChildText("NewObject")));
            }

            return progress;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket replication response body to replication location.
     */
    @SuppressWarnings("unchecked")
    public static List<String> parseGetBucketReplicationLocation(InputStream responseBody)
            throws ResponseParseException {
        try {
            Element root = getXmlRootElement(responseBody);

            List<String> locationList = new ArrayList<String>();
            List<Element> locElements = root.getChildren("Location");

            for (Element locElem : locElements) {
                locationList.add(locElem.getText());
            }

            return locationList;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket info response body to bucket info.
     */
    public static BucketInfo parseGetBucketInfo(InputStream responseBody) throws ResponseParseException {
        try {
            Element root = getXmlRootElement(responseBody);
            Element bucketElem = root.getChild("Bucket");
            BucketInfo bucketInfo = new BucketInfo();

            // owner
            Bucket bucket = new Bucket();
            String id = bucketElem.getChild("Owner").getChildText("ID");
            String displayName = bucketElem.getChild("Owner").getChildText("DisplayName");
            Owner owner = new Owner(id, displayName);
            bucket.setOwner(owner);

            // bucket
            bucket.setName(bucketElem.getChildText("Name"));
            bucket.setLocation(bucketElem.getChildText("Location"));
            bucket.setExtranetEndpoint(bucketElem.getChildText("ExtranetEndpoint"));
            bucket.setIntranetEndpoint(bucketElem.getChildText("IntranetEndpoint"));
            bucket.setCreationDate(DateUtil.parseIso8601Date(bucketElem.getChildText("CreationDate")));
            if (bucketElem.getChild("StorageClass") != null) {
                bucket.setStorageClass(StorageClass.parse(bucketElem.getChildText("StorageClass")));
            }
            if (bucketElem.getChildText("DataRedundancyType") != null) {
                bucket.setDataRedundancyType(DataRedundancyType.parse(bucketElem.getChildText("DataRedundancyType")));
            }
            bucketInfo.setBucket(bucket);

            // encryptionRule
            Element encryptionElement = bucketElem.getChild("ServerSideEncryptionRule");
            if (encryptionElement != null) {
                String algorithm = encryptionElement.getChildText("SSEAlgorithm");
                ServerSideEncryptionRule rule = new ServerSideEncryptionRule();
                rule.setAlgorithm(SSEAlgorithm.parse(algorithm));
                if (rule.getAlgorithm() == SSEAlgorithm.KMS) {
                    rule.setkMSMasterKeyID(encryptionElement.getChildText("KMSMasterKeyID"));
                }
                bucketInfo.setEncryptionRule(rule);
            }

            // acl
            String aclString = bucketElem.getChild("AccessControlList").getChildText("Grant");
            CannedAccessControlList acl = CannedAccessControlList.parse(aclString);
            bucketInfo.setCannedACL(acl);
            switch (acl) {
            case PublicRead:
                bucketInfo.grantPermission(GroupGrantee.AllUsers, Permission.Read);
                break;
            case PublicReadWrite:
                bucketInfo.grantPermission(GroupGrantee.AllUsers, Permission.FullControl);
                break;
            default:
                break;
            }

            // bucket versioninig
            Element bucketVersionInfoElement = bucketElem.getChild("Versioning");
            if (bucketVersionInfoElement != null) {
                // new set bucket version on bucket
                bucket.setBucketVersion(bucketElem.getChildText("Versioning"));
            }
            
            return bucketInfo;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket info response body to bucket stat.
     */
    public static BucketStat parseGetBucketStat(InputStream responseBody) throws ResponseParseException {
        try {
            Element root = getXmlRootElement(responseBody);
            Long storage = Long.parseLong(root.getChildText("Storage"));
            Long objectCount = Long.parseLong(root.getChildText("ObjectCount"));
            Long multipartUploadCount = Long.parseLong(root.getChildText("MultipartUploadCount"));
            BucketStat bucketStat = new BucketStat(storage, objectCount, multipartUploadCount);
            return bucketStat;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket info response body to bucket stat.
     */
    public static ServerSideEncryptionRule parseGetBucketEncryption(InputStream responseBody) throws ResponseParseException {
        try {
            Element root = getXmlRootElement(responseBody);

            String algorithm = root.getChild("ApplyServerSideEncryptionByDefault").getChildText("SSEAlgorithm");

            ServerSideEncryptionRule encryptionRule = new ServerSideEncryptionRule();
            encryptionRule.setAlgorithm(SSEAlgorithm.parse(algorithm));
            if (encryptionRule.getAlgorithm() == SSEAlgorithm.KMS) {
                String kMSMasterKeyID = root.getChild("ApplyServerSideEncryptionByDefault").getChildText("KMSMasterKeyID");
                encryptionRule.setkMSMasterKeyID(kMSMasterKeyID);
            }

            return encryptionRule;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall create live channel response body to corresponding result.
     */
    @SuppressWarnings("unchecked")
    public static CreateLiveChannelResult parseCreateLiveChannel(InputStream responseBody)
            throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            CreateLiveChannelResult result = new CreateLiveChannelResult();

            List<String> publishUrls = new ArrayList<String>();
            List<Element> publishElems = root.getChild("PublishUrls").getChildren("Url");
            for (Element urlElem : publishElems) {
                publishUrls.add(urlElem.getText());
            }
            result.setPublishUrls(publishUrls);

            List<String> playUrls = new ArrayList<String>();
            List<Element> playElems = root.getChild("PlayUrls").getChildren("Url");
            for (Element urlElem : playElems) {
                playUrls.add(urlElem.getText());
            }
            result.setPlayUrls(playUrls);

            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall get live channel info response body to corresponding result.
     */
    public static LiveChannelInfo parseGetLiveChannelInfo(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            LiveChannelInfo result = new LiveChannelInfo();

            result.setDescription(root.getChildText("Description"));
            result.setStatus(LiveChannelStatus.parse(root.getChildText("Status")));

            Element targetElem = root.getChild("Target");
            LiveChannelTarget target = new LiveChannelTarget();
            target.setType(targetElem.getChildText("Type"));
            target.setFragDuration(Integer.parseInt(targetElem.getChildText("FragDuration")));
            target.setFragCount(Integer.parseInt(targetElem.getChildText("FragCount")));
            target.setPlaylistName(targetElem.getChildText("PlaylistName"));
            result.setTarget(target);

            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall get live channel stat response body to corresponding result.
     */
    public static LiveChannelStat parseGetLiveChannelStat(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            LiveChannelStat result = new LiveChannelStat();

            result.setPushflowStatus(PushflowStatus.parse(root.getChildText("Status")));

            if (root.getChild("ConnectedTime") != null) {
                result.setConnectedDate(DateUtil.parseIso8601Date(root.getChildText("ConnectedTime")));
            }

            if (root.getChild("RemoteAddr") != null) {
                result.setRemoteAddress(root.getChildText("RemoteAddr"));
            }

            Element videoElem = root.getChild("Video");
            if (videoElem != null) {
                VideoStat videoStat = new VideoStat();
                videoStat.setWidth(Integer.parseInt(videoElem.getChildText("Width")));
                videoStat.setHeight(Integer.parseInt(videoElem.getChildText("Height")));
                videoStat.setFrameRate(Integer.parseInt(videoElem.getChildText("FrameRate")));
                videoStat.setBandWidth(Integer.parseInt(videoElem.getChildText("Bandwidth")));
                videoStat.setCodec(videoElem.getChildText("Codec"));
                result.setVideoStat(videoStat);
            }

            Element audioElem = root.getChild("Audio");
            if (audioElem != null) {
                AudioStat audioStat = new AudioStat();
                audioStat.setBandWidth(Integer.parseInt(audioElem.getChildText("Bandwidth")));
                audioStat.setSampleRate(Integer.parseInt(audioElem.getChildText("SampleRate")));
                audioStat.setCodec(audioElem.getChildText("Codec"));
                result.setAudioStat(audioStat);
            }

            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall get live channel history response body to corresponding
     * result.
     */
    @SuppressWarnings("unchecked")
    public static List<LiveRecord> parseGetLiveChannelHistory(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            List<LiveRecord> liveRecords = new ArrayList<LiveRecord>();
            List<Element> recordElements = root.getChildren("LiveRecord");

            for (Element recordElem : recordElements) {
                LiveRecord record = new LiveRecord();
                record.setStartDate(DateUtil.parseIso8601Date(recordElem.getChildText("StartTime")));
                record.setEndDate(DateUtil.parseIso8601Date(recordElem.getChildText("EndTime")));
                record.setRemoteAddress(recordElem.getChildText("RemoteAddr"));
                liveRecords.add(record);
            }

            return liveRecords;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall list live channels response body to live channel listing.
     */
    @SuppressWarnings("unchecked")
    public static LiveChannelListing parseListLiveChannels(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            LiveChannelListing liveChannelListing = new LiveChannelListing();
            liveChannelListing.setTruncated(Boolean.valueOf(root.getChildText("IsTruncated")));

            if (root.getChild("Prefix") != null) {
                String prefix = root.getChildText("Prefix");
                liveChannelListing.setPrefix(isNullOrEmpty(prefix) ? null : prefix);
            }

            if (root.getChild("Marker") != null) {
                String marker = root.getChildText("Marker");
                liveChannelListing.setMarker(isNullOrEmpty(marker) ? null : marker);
            }

            if (root.getChild("MaxKeys") != null) {
                String maxKeys = root.getChildText("MaxKeys");
                liveChannelListing.setMaxKeys(Integer.valueOf(maxKeys));
            }

            if (root.getChild("NextMarker") != null) {
                String nextMarker = root.getChildText("NextMarker");
                liveChannelListing.setNextMarker(isNullOrEmpty(nextMarker) ? null : nextMarker);
            }

            List<Element> liveChannelElems = root.getChildren("LiveChannel");
            for (Element elem : liveChannelElems) {
                LiveChannel liveChannel = new LiveChannel();

                liveChannel.setName(elem.getChildText("Name"));
                liveChannel.setDescription(elem.getChildText("Description"));
                liveChannel.setStatus(LiveChannelStatus.parse(elem.getChildText("Status")));
                liveChannel.setLastModified(DateUtil.parseIso8601Date(elem.getChildText("LastModified")));

                List<String> publishUrls = new ArrayList<String>();
                List<Element> publishElems = elem.getChild("PublishUrls").getChildren("Url");
                for (Element urlElem : publishElems) {
                    publishUrls.add(urlElem.getText());
                }
                liveChannel.setPublishUrls(publishUrls);

                List<String> playUrls = new ArrayList<String>();
                List<Element> playElems = elem.getChild("PlayUrls").getChildren("Url");
                for (Element urlElem : playElems) {
                    playUrls.add(urlElem.getText());
                }
                liveChannel.setPlayUrls(playUrls);

                liveChannelListing.addLiveChannel(liveChannel);
            }

            return liveChannelListing;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall get user qos response body to user qos.
     */
    public static UserQos parseGetUserQos(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            UserQos userQos = new UserQos();

            if (root.getChild("StorageCapacity") != null) {
                userQos.setStorageCapacity(Integer.parseInt(root.getChildText("StorageCapacity")));
            }

            return userQos;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall get bucket lifecycle response body to lifecycle rules.
     */
    @SuppressWarnings("unchecked")
    public static List<LifecycleRule> parseGetBucketLifecycle(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            List<LifecycleRule> lifecycleRules = new ArrayList<LifecycleRule>();
            List<Element> ruleElements = root.getChildren("Rule");

            for (Element ruleElem : ruleElements) {
                LifecycleRule rule = new LifecycleRule();

                if (ruleElem.getChild("ID") != null) {
                    rule.setId(ruleElem.getChildText("ID"));
                }

                if (ruleElem.getChild("Prefix") != null) {
                    rule.setPrefix(ruleElem.getChildText("Prefix"));
                }

                if (ruleElem.getChild("Status") != null) {
                    rule.setStatus(RuleStatus.valueOf(ruleElem.getChildText("Status")));
                }

                if (ruleElem.getChild("Expiration") != null) {
                    if (ruleElem.getChild("Expiration").getChild("Date") != null) {
                        Date expirationDate = DateUtil
                                .parseIso8601Date(ruleElem.getChild("Expiration").getChildText("Date"));
                        rule.setExpirationTime(expirationDate);
                    } else if (ruleElem.getChild("Expiration").getChild("Days") != null) {
                        rule.setExpirationDays(Integer.parseInt(ruleElem.getChild("Expiration").getChildText("Days")));
                    } else if(ruleElem.getChild("Expiration").getChild("ExpiredObjectDeleteMarker") != null){
                        rule.setExpiredObjectDeleteMarker(Boolean.valueOf(ruleElem.getChild("Expiration").getChildText("ExpiredObjectDeleteMarker")));
                    } else {
                        Date createdBeforeDate = DateUtil
                                .parseIso8601Date(ruleElem.getChild("Expiration").getChildText("CreatedBeforeDate"));
                        rule.setCreatedBeforeDate(createdBeforeDate);
                    }
                }

                // 解析历史版本过期规则
                if (ruleElem.getChild("NoncurrentVersionExpiration") != null ) {
                    if (ruleElem.getChild("NoncurrentVersionExpiration").getChild("NoncurrentDays") != null) {
                        rule.setNoncurrentVersionExpirationInDays(Integer.parseInt(ruleElem.getChild("NoncurrentVersionExpiration").getChildText("NoncurrentDays")));
                    }
                }

                if (ruleElem.getChild("AbortMultipartUpload") != null) {
                    LifecycleRule.AbortMultipartUpload abortMultipartUpload = new LifecycleRule.AbortMultipartUpload();
                    if (ruleElem.getChild("AbortMultipartUpload").getChild("Days") != null) {
                        abortMultipartUpload.setExpirationDays(
                                Integer.parseInt(ruleElem.getChild("AbortMultipartUpload").getChildText("Days")));
                    } else {
                        Date createdBeforeDate = DateUtil.parseIso8601Date(
                                ruleElem.getChild("AbortMultipartUpload").getChildText("CreatedBeforeDate"));
                        abortMultipartUpload.setCreatedBeforeDate(createdBeforeDate);
                    }
                    rule.setAbortMultipartUpload(abortMultipartUpload);
                }

                List<Element> transitionElements = ruleElem.getChildren("Transition");
                List<StorageTransition> storageTransitions = new ArrayList<StorageTransition>();
                for (Element transitionElem : transitionElements) {
                    LifecycleRule.StorageTransition storageTransition = new LifecycleRule.StorageTransition();
                    if (transitionElem.getChild("Days") != null) {
                        storageTransition.setExpirationDays(Integer.parseInt(transitionElem.getChildText("Days")));
                    } else {
                        Date createdBeforeDate = DateUtil
                                .parseIso8601Date(transitionElem.getChildText("CreatedBeforeDate"));
                        storageTransition.setCreatedBeforeDate(createdBeforeDate);
                    }
                    if (transitionElem.getChild("StorageClass") != null) {
                        storageTransition
                                .setStorageClass(StorageClass.parse(transitionElem.getChildText("StorageClass")));
                    }
                    storageTransitions.add(storageTransition);
                }

                rule.setStorageTransition(storageTransitions);

                // 解析历史版本存储
                List<Element> versionTransitionElements = ruleElem.getChildren("NoncurrentVersionTransition");
                List<LifecycleRule.NoncurrentVersionTransition> versionStorageTransitions = new ArrayList<LifecycleRule.NoncurrentVersionTransition>();
                for (Element transitionElem : versionTransitionElements) {
                    LifecycleRule.NoncurrentVersionTransition storageTransition = new LifecycleRule.NoncurrentVersionTransition();
                    if (transitionElem.getChild("NoncurrentDays") != null) {
                        storageTransition.setExpirationDays(Integer.parseInt(transitionElem.getChildText("NoncurrentDays")));
                    }

                    if (transitionElem.getChild("StorageClass") != null) {
                        storageTransition
                                .setStorageClass(StorageClass.parse(transitionElem.getChildText("StorageClass")));
                    }
                    versionStorageTransitions.add(storageTransition);
                }
                rule.setNoncurrentVersionTransitions(versionStorageTransitions);

                // 解析Tag标签生命周期
                List<Element> tagElements = ruleElem.getChildren("Tag");
                List<Tag> objectTags = new ArrayList<Tag>();
                for (Element tagElement : tagElements) {
                    final String tagKey = tagElement.getChildText("Key");
                    final String tagValue = tagElement.getChildText("Value");
                    if (tagKey != null || tagValue != null) {
                        Tag objectTag = new Tag(tagKey, tagValue);
                        objectTags.add(objectTag);
                    }
                }

                rule.setObjectTags(objectTags);

                lifecycleRules.add(rule);
            }

            return lifecycleRules;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket cname response body to cname configuration.
     */
    @SuppressWarnings("unchecked")
    public static List<CnameConfiguration> parseGetBucketCname(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            List<CnameConfiguration> cnames = new ArrayList<CnameConfiguration>();
            List<Element> cnameElements = root.getChildren("Cname");

            for (Element cnameElem : cnameElements) {
                CnameConfiguration cname = new CnameConfiguration();

                cname.setDomain(cnameElem.getChildText("Domain"));
                cname.setStatus(CnameConfiguration.CnameStatus.valueOf(cnameElem.getChildText("Status")));
                cname.setLastMofiedTime(DateUtil.parseIso8601Date(cnameElem.getChildText("LastModified")));

                if (cnameElem.getChildText("IsPurgeCdnCache") != null) {
                    boolean purgeCdnCache = Boolean.valueOf(cnameElem.getChildText("IsPurgeCdnCache"));
                    cname.setPurgeCdnCache(purgeCdnCache);
                }

                cnames.add(cname);
            }

            return cnames;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall bucket request payment response body to RequestPayer.
     */
    public static RequestPayer parseGetBucketRequestPayment(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            String payerString = root.getChildText("Payer");

            return RequestPayer.parse(payerString);
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall bucket worm configuration response body to WormConfiguration.
     */
    public static WormConfiguration parseGetWormConfiguration(InputStream responseBody) throws ResponseParseException {

        try {
            WormConfiguration wormConfiguration = new WormConfiguration();

            Element root = getXmlRootElement(responseBody);
            wormConfiguration.setWormId(root.getChildText("WormId"));
            if(root.getChildText("State") != null){
                wormConfiguration.setState(WormConfiguration.WormState.parse(root.getChildText("State")));
            }
            if(root.getChildText("CreationDate") != null){
                wormConfiguration.setCreationDate(DateUtil.parseIso8601Date(root.getChildText("CreationDate")));
            }

            if(root.getChildText("ExpirationDate") != null){
                wormConfiguration.setExpirationDate(DateUtil.parseIso8601Date(root.getChildText("ExpirationDate")));
            }

            if(root.getChildText("RetentionPeriodInDays") != null) {
               wormConfiguration.setRetentionPeriodInDays(Integer.parseInt(root.getChildText("RetentionPeriodInDays")));
            }

            return wormConfiguration;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmashall get bucket notification response body to corresponding result
     */
    public static NotificationConfiguration parseNotificationConfiguration(InputStream responseBody) throws ResponseParseException {
        try {
            Element root = getXmlRootElement(responseBody);
            List<FunctionComputeConfiguration> functionComputeConfigurations = new ArrayList<FunctionComputeConfiguration>();

            List<Element> notificationElems = root.getChildren("FunctionComputeConfiguration");

            for (Element notificationElem : notificationElems) {
                String prefix = null;
                String suffix = null;
                String arn = null;
                String assumeRole = null;

                String id = notificationElem.getChildText("ID");
                String event = notificationElem.getChildText("Event");

                if (notificationElem.getChild("Filter") != null && notificationElem.getChild("Filter").getChild("Key") != null) {
                    prefix = notificationElem.getChild("Filter").getChild("Key").getChildText("Prefix");
                    suffix = notificationElem.getChild("Filter").getChild("Key").getChildText("Suffix");
                }

                if (notificationElem.getChild("Function") != null) {
                    arn = notificationElem.getChild("Function").getChildText("Arn");
                    assumeRole = notificationElem.getChild("Function").getChildText("AssumeRole");
                }

                functionComputeConfigurations.add(new FunctionComputeConfiguration(id, event, prefix, suffix, arn, assumeRole));
            }
            return new NotificationConfiguration(functionComputeConfigurations);
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    public static final class GetObjectTaggingResponseParser implements ResponseParser<ObjectTagging> {

        @Override
        public ObjectTagging parse(ResponseMessage response) throws ResponseParseException {
            try {
                ObjectTagging result = parseObjectTagging(response.getContent());
                result.setRequestId(response.getRequestId());

                return result;
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static ObjectTagging parseObjectTagging(InputStream inputStream) throws ResponseParseException {
        List<Tag> tagSet = new ArrayList<Tag>();
        if (inputStream == null) {
            return new ObjectTagging(tagSet);
        }

        try {
            Element root = getXmlRootElement(inputStream);

            if (root.getChild("TagSet") != null) {
                List<Element> tagElems = root.getChild("TagSet").getChildren("Tag");
                if (tagElems != null) {
                    for (Element e : tagElems) {
                        Tag tag = new Tag(e.getChildText("Key"), e.getChildText("Value"));
                        tagSet.add(tag);
                    }
                }
            }

            ObjectTagging objectTagging = new ObjectTagging(tagSet);

            return objectTagging;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    public static final class GetBucketVersioningResponseParser implements ResponseParser<BucketVersion> {

        @Override
        public BucketVersion parse(ResponseMessage response) throws ResponseParseException {
            try {
                BucketVersion result = parseBucketVersioning(response.getContent());
                result.setRequestId(response.getRequestId());

                return result;
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static BucketVersion parseBucketVersioning(InputStream inputStream) throws ResponseParseException {
       String bucketVersionStatus = "Disabled";
        if (inputStream == null) {
            return new BucketVersion(bucketVersionStatus);
        }

        try {
            Element root = getXmlRootElement(inputStream);

            if (root.getChildText("Status") != null) {
                bucketVersionStatus = root.getChildText("Status");
            }

            BucketVersion bucketVersion = new BucketVersion(bucketVersionStatus);

            return bucketVersion;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }
}
