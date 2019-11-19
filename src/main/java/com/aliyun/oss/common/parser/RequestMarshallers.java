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

package com.aliyun.oss.common.parser;

import static com.aliyun.oss.internal.OSSConstants.DEFAULT_CHARSET_NAME;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.comm.io.FixedLengthInputStream;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.common.utils.HttpUtil;
import com.aliyun.oss.common.utils.StringUtils;
import com.aliyun.oss.internal.RequestParameters;
import com.aliyun.oss.model.*;
import com.aliyun.oss.model.AddBucketReplicationRequest.ReplicationAction;
import com.aliyun.oss.model.DeleteVersionsRequest.KeyVersion;
import com.aliyun.oss.model.LifecycleRule.AbortMultipartUpload;
import com.aliyun.oss.model.LifecycleRule.RuleStatus;
import com.aliyun.oss.model.LifecycleRule.StorageTransition;
import com.aliyun.oss.model.LifecycleRule.NoncurrentVersionStorageTransition;
import com.aliyun.oss.model.LifecycleRule.NoncurrentVersionExpiration;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;

/**
 * A collection of marshallers that marshall HTTP request into crossponding
 * input stream.
 */
public final class RequestMarshallers {

    public static final StringMarshaller stringMarshaller = new StringMarshaller();

    public static final DeleteObjectsRequestMarshaller deleteObjectsRequestMarshaller = new DeleteObjectsRequestMarshaller();
    public static final DeleteVersionsRequestMarshaller deleteVersionsRequestMarshaller = new DeleteVersionsRequestMarshaller();

    public static final CreateBucketRequestMarshaller createBucketRequestMarshaller = new CreateBucketRequestMarshaller();
    public static final BucketRefererMarshaller bucketRefererMarshaller = new BucketRefererMarshaller();
    public static final SetBucketLoggingRequestMarshaller setBucketLoggingRequestMarshaller = new SetBucketLoggingRequestMarshaller();
    public static final SetBucketWebsiteRequestMarshaller setBucketWebsiteRequestMarshaller = new SetBucketWebsiteRequestMarshaller();
    public static final SetBucketLifecycleRequestMarshaller setBucketLifecycleRequestMarshaller = new SetBucketLifecycleRequestMarshaller();
    public static final PutBucketImageRequestMarshaller putBucketImageRequestMarshaller = new PutBucketImageRequestMarshaller();
    public static final PutImageStyleRequestMarshaller putImageStyleRequestMarshaller = new PutImageStyleRequestMarshaller();
    public static final BucketImageProcessConfMarshaller bucketImageProcessConfMarshaller = new BucketImageProcessConfMarshaller();
    public static final SetBucketCORSRequestMarshaller setBucketCORSRequestMarshaller = new SetBucketCORSRequestMarshaller();
    public static final SetBucketTaggingRequestMarshaller setBucketTaggingRequestMarshaller = new SetBucketTaggingRequestMarshaller();
    public static final AddBucketReplicationRequestMarshaller addBucketReplicationRequestMarshaller = new AddBucketReplicationRequestMarshaller();
    public static final DeleteBucketReplicationRequestMarshaller deleteBucketReplicationRequestMarshaller = new DeleteBucketReplicationRequestMarshaller();
    public static final AddBucketCnameRequestMarshaller addBucketCnameRequestMarshaller = new AddBucketCnameRequestMarshaller();
    public static final DeleteBucketCnameRequestMarshaller deleteBucketCnameRequestMarshaller = new DeleteBucketCnameRequestMarshaller();
    public static final SetBucketQosRequestMarshaller setBucketQosRequestMarshaller = new SetBucketQosRequestMarshaller();
    public static final CompleteMultipartUploadRequestMarshaller completeMultipartUploadRequestMarshaller = new CompleteMultipartUploadRequestMarshaller();
    public static final CreateLiveChannelRequestMarshaller createLiveChannelRequestMarshaller = new CreateLiveChannelRequestMarshaller();
    public static final CreateUdfRequestMarshaller createUdfRequestMarshaller = new CreateUdfRequestMarshaller();
    public static final CreateUdfApplicationRequestMarshaller createUdfApplicationRequestMarshaller = new CreateUdfApplicationRequestMarshaller();
    public static final UpgradeUdfApplicationRequestMarshaller upgradeUdfApplicationRequestMarshaller = new UpgradeUdfApplicationRequestMarshaller();
    public static final ResizeUdfApplicationRequestMarshaller resizeUdfApplicationRequestMarshaller = new ResizeUdfApplicationRequestMarshaller();
    public static final ProcessObjectRequestMarshaller processObjectRequestMarshaller = new ProcessObjectRequestMarshaller();
    public static final SetBucketVersioningRequestMarshaller setBucketVersioningRequestMarshaller = new SetBucketVersioningRequestMarshaller();
    public static final SetBucketEncryptionRequestMarshaller setBucketEncryptionRequestMarshaller = new SetBucketEncryptionRequestMarshaller();
    public static final SetBucketPolicyRequestMarshaller setBucketPolicyRequestMarshaller = new SetBucketPolicyRequestMarshaller();
    public static final SetBucketRequestPaymentRequestMarshaller setBucketRequestPaymentRequestMarshaller = new SetBucketRequestPaymentRequestMarshaller();
    public static final SetBucketQosInfoRequestMarshaller setBucketQosInfoRequestMarshaller = new SetBucketQosInfoRequestMarshaller();
    public static final SetAsyncFetchTaskRequestMarshaller setAsyncFetchTaskRequestMarshaller = new SetAsyncFetchTaskRequestMarshaller();

    public static final CreateSelectObjectMetadataRequestMarshaller createSelectObjectMetadataRequestMarshaller = new CreateSelectObjectMetadataRequestMarshaller();
    public static final SelectObjectRequestMarshaller selectObjectRequestMarshaller = new SelectObjectRequestMarshaller();

    public interface RequestMarshaller<R> extends Marshaller<FixedLengthInputStream, R> {

    }

    public interface RequestMarshaller2<R> extends Marshaller<byte[], R> {

    }

    public static final class StringMarshaller implements Marshaller<FixedLengthInputStream, String> {

        @Override
        public FixedLengthInputStream marshall(String input) {
            if (input == null) {
                throw new IllegalArgumentException("The input should not be null.");
            }

            byte[] binaryData = null;
            try {
                binaryData = input.getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            long length = binaryData.length;
            InputStream instream = new ByteArrayInputStream(binaryData);
            return new FixedLengthInputStream(instream, length);
        }

    }

    public static final class PutImageStyleRequestMarshaller implements RequestMarshaller<PutImageStyleRequest> {
        @Override
        public FixedLengthInputStream marshall(PutImageStyleRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<Style>");
            xmlBody.append("<Content>" + request.GetStyle() + "</Content>");
            xmlBody.append("</Style>");
            return stringMarshaller.marshall(xmlBody.toString());
        }
    }

    public static final class BucketImageProcessConfMarshaller implements RequestMarshaller<ImageProcess> {

        @Override
        public FixedLengthInputStream marshall(ImageProcess imageProcessConf) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<BucketProcessConfiguration>");
            xmlBody.append("<CompliedHost>" + imageProcessConf.getCompliedHost() + "</CompliedHost>");
            if (imageProcessConf.isSourceFileProtect() != null
                    && imageProcessConf.isSourceFileProtect().booleanValue()) {
                xmlBody.append("<SourceFileProtect>Enabled</SourceFileProtect>");
            } else {
                xmlBody.append("<SourceFileProtect>Disabled</SourceFileProtect>");
            }
            xmlBody.append("<SourceFileProtectSuffix>" + imageProcessConf.getSourceFileProtectSuffix()
                    + "</SourceFileProtectSuffix>");
            xmlBody.append("<StyleDelimiters>" + imageProcessConf.getStyleDelimiters() + "</StyleDelimiters>");
            if (imageProcessConf.isSupportAtStyle() != null && imageProcessConf.isSupportAtStyle().booleanValue()) {
                xmlBody.append("<OssDomainSupportAtProcess>Enabled</OssDomainSupportAtProcess>");
            } else {
                xmlBody.append("<OssDomainSupportAtProcess>Disabled</OssDomainSupportAtProcess>");
            }
            xmlBody.append("</BucketProcessConfiguration>");
            return stringMarshaller.marshall(xmlBody.toString());
        }

    }

    public static final class PutBucketImageRequestMarshaller implements RequestMarshaller<PutBucketImageRequest> {
        @Override
        public FixedLengthInputStream marshall(PutBucketImageRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<Channel>");
            if (request.GetIsForbidOrigPicAccess()) {
                xmlBody.append("<OrigPicForbidden>true</OrigPicForbidden>");
            } else {
                xmlBody.append("<OrigPicForbidden>false</OrigPicForbidden>");
            }

            if (request.GetIsUseStyleOnly()) {
                xmlBody.append("<UseStyleOnly>true</UseStyleOnly>");
            } else {
                xmlBody.append("<UseStyleOnly>false</UseStyleOnly>");
            }

            if (request.GetIsAutoSetContentType()) {
                xmlBody.append("<AutoSetContentType>true</AutoSetContentType>");
            } else {
                xmlBody.append("<AutoSetContentType>false</AutoSetContentType>");
            }

            if (request.GetIsUseSrcFormat()) {
                xmlBody.append("<UseSrcFormat>true</UseSrcFormat>");
            } else {
                xmlBody.append("<UseSrcFormat>false</UseSrcFormat>");
            }

            if (request.GetIsSetAttachName()) {
                xmlBody.append("<SetAttachName>true</SetAttachName>");
            } else {
                xmlBody.append("<SetAttachName>false</SetAttachName>");
            }
            xmlBody.append("<Default404Pic>" + request.GetDefault404Pic() + "</Default404Pic>");
            xmlBody.append("<StyleDelimiters>" + request.GetStyleDelimiters() + "</StyleDelimiters>");

            xmlBody.append("</Channel>");
            return stringMarshaller.marshall(xmlBody.toString());
        }
    }

    public static final class CreateBucketRequestMarshaller implements RequestMarshaller<CreateBucketRequest> {

        @Override
        public FixedLengthInputStream marshall(CreateBucketRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            if (request.getLocationConstraint() != null 
                    || request.getStorageClass() != null 
                    || request.getDataRedundancyType() != null) {
                xmlBody.append("<CreateBucketConfiguration>");
                if (request.getLocationConstraint() != null) {
                    xmlBody.append("<LocationConstraint>" + request.getLocationConstraint() + "</LocationConstraint>");
                }
                if (request.getStorageClass() != null) {
                    xmlBody.append("<StorageClass>" + request.getStorageClass().toString() + "</StorageClass>");
                }
                if (request.getDataRedundancyType() != null) {
                    xmlBody.append("<DataRedundancyType>" + request.getDataRedundancyType().toString() + "</DataRedundancyType>");
                }
                xmlBody.append("</CreateBucketConfiguration>");
            }
            return stringMarshaller.marshall(xmlBody.toString());
        }

    }

    public static final class BucketRefererMarshaller implements RequestMarshaller<BucketReferer> {

        @Override
        public FixedLengthInputStream marshall(BucketReferer br) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<RefererConfiguration>");
            xmlBody.append("<AllowEmptyReferer>" + br.isAllowEmptyReferer() + "</AllowEmptyReferer>");

            if (!br.getRefererList().isEmpty()) {
                xmlBody.append("<RefererList>");
                for (String referer : br.getRefererList()) {
                    xmlBody.append("<Referer>" + referer + "</Referer>");
                }
                xmlBody.append("</RefererList>");
            } else {
                xmlBody.append("<RefererList/>");
            }

            xmlBody.append("</RefererConfiguration>");
            return stringMarshaller.marshall(xmlBody.toString());
        }

    }

    public static final class SetBucketLoggingRequestMarshaller implements RequestMarshaller<SetBucketLoggingRequest> {

        @Override
        public FixedLengthInputStream marshall(SetBucketLoggingRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<BucketLoggingStatus>");
            if (request.getTargetBucket() != null) {
                xmlBody.append("<LoggingEnabled>");
                xmlBody.append("<TargetBucket>" + request.getTargetBucket() + "</TargetBucket>");
                if (request.getTargetPrefix() != null) {
                    xmlBody.append("<TargetPrefix>" + request.getTargetPrefix() + "</TargetPrefix>");
                }
                xmlBody.append("</LoggingEnabled>");
            } else {
                // Nothing to do here, user attempt to close bucket logging
                // functionality
                // by setting an empty BucketLoggingStatus entity.
            }
            xmlBody.append("</BucketLoggingStatus>");
            return stringMarshaller.marshall(xmlBody.toString());
        }

    }

    public static final class SetBucketWebsiteRequestMarshaller implements RequestMarshaller<SetBucketWebsiteRequest> {

        @Override
        public FixedLengthInputStream marshall(SetBucketWebsiteRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<WebsiteConfiguration>");
            if (request.getIndexDocument() != null) {
                xmlBody.append("<IndexDocument>");
                xmlBody.append("<Suffix>" + request.getIndexDocument() + "</Suffix>");
                xmlBody.append("</IndexDocument>");
            }
            if (request.getErrorDocument() != null) {
                xmlBody.append("<ErrorDocument>");
                xmlBody.append("<Key>" + request.getErrorDocument() + "</Key>");
                xmlBody.append("</ErrorDocument>");
            }

            // RoutingRules可以没有
            if (request.getRoutingRules().size() > 0) {
                xmlBody.append("<RoutingRules>");
                for (RoutingRule routingRule : request.getRoutingRules()) {
                    xmlBody.append("<RoutingRule>");
                    xmlBody.append("<RuleNumber>" + routingRule.getNumber() + "</RuleNumber>");

                    // Condition字句可以没有，如果有至少有一个条件
                    RoutingRule.Condition condition = routingRule.getCondition();
                    if (condition.getKeyPrefixEquals() != null || condition.getHttpErrorCodeReturnedEquals() > 0) {
                        xmlBody.append("<Condition>");
                        if (condition.getKeyPrefixEquals() != null) {
                            xmlBody.append("<KeyPrefixEquals>" + escapeKey(condition.getKeyPrefixEquals())
                                    + "</KeyPrefixEquals>");
                        }
                        if (condition.getHttpErrorCodeReturnedEquals() != null) {
                            xmlBody.append("<HttpErrorCodeReturnedEquals>" + condition.getHttpErrorCodeReturnedEquals()
                                    + "</HttpErrorCodeReturnedEquals>");
                        }
                        xmlBody.append("</Condition>");
                    }

                    // Redirect子句必须存在
                    RoutingRule.Redirect redirect = routingRule.getRedirect();
                    xmlBody.append("<Redirect>");
                    if (redirect.getRedirectType() != null) {
                        xmlBody.append("<RedirectType>" + redirect.getRedirectType().toString() + "</RedirectType>");
                    }
                    if (redirect.getHostName() != null) {
                        xmlBody.append("<HostName>" + redirect.getHostName() + "</HostName>");
                    }
                    if (redirect.getProtocol() != null) {
                        xmlBody.append("<Protocol>" + redirect.getProtocol().toString() + "</Protocol>");
                    }
                    if (redirect.getReplaceKeyPrefixWith() != null) {
                        xmlBody.append("<ReplaceKeyPrefixWith>" + escapeKey(redirect.getReplaceKeyPrefixWith())
                                + "</ReplaceKeyPrefixWith>");
                    }
                    if (redirect.getReplaceKeyWith() != null) {
                        xmlBody.append(
                                "<ReplaceKeyWith>" + escapeKey(redirect.getReplaceKeyWith()) + "</ReplaceKeyWith>");
                    }
                    if (redirect.getHttpRedirectCode() != null) {
                        xmlBody.append("<HttpRedirectCode>" + redirect.getHttpRedirectCode() + "</HttpRedirectCode>");
                    }
                    if (redirect.getMirrorURL() != null) {
                        xmlBody.append("<MirrorURL>" + redirect.getMirrorURL() + "</MirrorURL>");
                    }
                    if (redirect.getMirrorSecondaryURL() != null) {
                        xmlBody.append("<MirrorURLSlave>" + redirect.getMirrorSecondaryURL() + "</MirrorURLSlave>");
                    }
                    if (redirect.getMirrorProbeURL() != null) {
                        xmlBody.append("<MirrorURLProbe>" + redirect.getMirrorProbeURL() + "</MirrorURLProbe>");
                    }
                    if (redirect.isPassQueryString() != null) {
                        xmlBody.append(
                                "<MirrorPassQueryString>" + redirect.isPassQueryString() + "</MirrorPassQueryString>");
                    }
                    if (redirect.isPassOriginalSlashes() != null) {
                        xmlBody.append("<MirrorPassOriginalSlashes>" + redirect.isPassOriginalSlashes()
                                + "</MirrorPassOriginalSlashes>");
                    }
                    xmlBody.append("</Redirect>");
                    xmlBody.append("</RoutingRule>");
                }
                xmlBody.append("</RoutingRules>");
            }

            xmlBody.append("</WebsiteConfiguration>");
            return stringMarshaller.marshall(xmlBody.toString());
        }

    }

    public static final class SetBucketLifecycleRequestMarshaller
            implements RequestMarshaller<SetBucketLifecycleRequest> {

        @Override
        public FixedLengthInputStream marshall(SetBucketLifecycleRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<LifecycleConfiguration>");
            for (LifecycleRule rule : request.getLifecycleRules()) {
                xmlBody.append("<Rule>");

                if (rule.getId() != null) {
                    xmlBody.append("<ID>" + rule.getId() + "</ID>");
                }

                if (rule.getPrefix() != null) {
                    xmlBody.append("<Prefix>" + rule.getPrefix() + "</Prefix>");
                } else {
                    xmlBody.append("<Prefix></Prefix>");
                }
                
                if (rule.hasTags()) {
                    for (Map.Entry<String, String> tag : rule.getTags().entrySet()) {
                        xmlBody.append("<Tag>");
                        xmlBody.append("<Key>" + tag.getKey() + "</Key>");
                        xmlBody.append("<Value>" + tag.getValue() + "</Value>");
                        xmlBody.append("</Tag>");
                    }
                }

                if (rule.getStatus() == RuleStatus.Enabled) {
                    xmlBody.append("<Status>Enabled</Status>");
                } else {
                    xmlBody.append("<Status>Disabled</Status>");
                }

                if (rule.getExpirationTime() != null) {
                    String formatDate = DateUtil.formatIso8601Date(rule.getExpirationTime());
                    xmlBody.append("<Expiration><Date>" + formatDate + "</Date></Expiration>");
                } else if (rule.getExpirationDays() != 0) {
                    xmlBody.append("<Expiration><Days>" + rule.getExpirationDays() + "</Days></Expiration>");
                } else if (rule.getCreatedBeforeDate() != null) {
                    String formatDate = DateUtil.formatIso8601Date(rule.getCreatedBeforeDate());
                    xmlBody.append(
                            "<Expiration><CreatedBeforeDate>" + formatDate + "</CreatedBeforeDate></Expiration>");
                } else if (rule.getExpiredDeleteMarker() != null) {
                    xmlBody.append("<Expiration><ExpiredObjectDeleteMarker>" + rule.getExpiredDeleteMarker() +
                            "</ExpiredObjectDeleteMarker></Expiration>");
                }

                if (rule.hasAbortMultipartUpload()) {
                    AbortMultipartUpload abortMultipartUpload = rule.getAbortMultipartUpload();
                    if (abortMultipartUpload.getExpirationDays() != 0) {
                        xmlBody.append("<AbortMultipartUpload><Days>" + abortMultipartUpload.getExpirationDays()
                                + "</Days></AbortMultipartUpload>");
                    } else {
                        String formatDate = DateUtil.formatIso8601Date(abortMultipartUpload.getCreatedBeforeDate());
                        xmlBody.append("<AbortMultipartUpload><CreatedBeforeDate>" + formatDate
                                + "</CreatedBeforeDate></AbortMultipartUpload>");
                    }
                }

                if (rule.hasStorageTransition()) {
                    for (StorageTransition storageTransition : rule.getStorageTransition()) {
                        xmlBody.append("<Transition>");
                        if (storageTransition.hasExpirationDays()) {
                            xmlBody.append("<Days>" + storageTransition.getExpirationDays() + "</Days>");
                        } else if (storageTransition.hasCreatedBeforeDate()) {
                            String formatDate = DateUtil.formatIso8601Date(storageTransition.getCreatedBeforeDate());
                            xmlBody.append("<CreatedBeforeDate>" + formatDate + "</CreatedBeforeDate>");
                        }
                        xmlBody.append("<StorageClass>" + storageTransition.getStorageClass() + "</StorageClass>");
                        xmlBody.append("</Transition>");
                    }
                }

                if (rule.hasNoncurrentVersionExpiration()) {
                    NoncurrentVersionExpiration expiration = rule.getNoncurrentVersionExpiration();
                    if (expiration.hasNoncurrentDays()) {
                        xmlBody.append("<NoncurrentVersionExpiration><NoncurrentDays>" + expiration.getNoncurrentDays() +
                                "</NoncurrentDays></NoncurrentVersionExpiration>");
                    }
                }

                if (rule.hasNoncurrentVersionStorageTransitions()) {
                    for (NoncurrentVersionStorageTransition transition : rule.getNoncurrentVersionStorageTransitions()) {
                        xmlBody.append("<NoncurrentVersionTransition>");
                        xmlBody.append("<NoncurrentDays>" + transition.getNoncurrentDays() + "</NoncurrentDays>");
                        xmlBody.append("<StorageClass>" + transition.getStorageClass() + "</StorageClass>");
                        xmlBody.append("</NoncurrentVersionTransition>");
                    }
                }

                xmlBody.append("</Rule>");
            }
            xmlBody.append("</LifecycleConfiguration>");
            return stringMarshaller.marshall(xmlBody.toString());
        }

    }

    public static final class SetBucketCORSRequestMarshaller implements RequestMarshaller<SetBucketCORSRequest> {

        @Override
        public FixedLengthInputStream marshall(SetBucketCORSRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<CORSConfiguration>");
            for (CORSRule rule : request.getCorsRules()) {
                xmlBody.append("<CORSRule>");

                for (String allowedOrigin : rule.getAllowedOrigins()) {
                    xmlBody.append("<AllowedOrigin>" + allowedOrigin + "</AllowedOrigin>");
                }

                for (String allowedMethod : rule.getAllowedMethods()) {
                    xmlBody.append("<AllowedMethod>" + allowedMethod + "</AllowedMethod>");
                }

                if (rule.getAllowedHeaders().size() > 0) {
                    for (String allowedHeader : rule.getAllowedHeaders()) {
                        xmlBody.append("<AllowedHeader>" + allowedHeader + "</AllowedHeader>");
                    }
                }

                if (rule.getExposeHeaders().size() > 0) {
                    for (String exposeHeader : rule.getExposeHeaders()) {
                        xmlBody.append("<ExposeHeader>" + exposeHeader + "</ExposeHeader>");
                    }
                }

                if (null != rule.getMaxAgeSeconds()) {
                    xmlBody.append("<MaxAgeSeconds>" + rule.getMaxAgeSeconds() + "</MaxAgeSeconds>");
                }

                xmlBody.append("</CORSRule>");
            }
            xmlBody.append("</CORSConfiguration>");
            return stringMarshaller.marshall(xmlBody.toString());
        }

    }

    public static final class CompleteMultipartUploadRequestMarshaller
            implements RequestMarshaller<CompleteMultipartUploadRequest> {

        @Override
        public FixedLengthInputStream marshall(CompleteMultipartUploadRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            List<PartETag> eTags = request.getPartETags();
            xmlBody.append("<CompleteMultipartUpload>");
            for (int i = 0; i < eTags.size(); i++) {
                PartETag part = eTags.get(i);
                String eTag = EscapedChar.QUOT + part.getETag().replace("\"", "") + EscapedChar.QUOT;
                xmlBody.append("<Part>");
                xmlBody.append("<PartNumber>" + part.getPartNumber() + "</PartNumber>");
                xmlBody.append("<ETag>" + eTag + "</ETag>");
                xmlBody.append("</Part>");
            }
            xmlBody.append("</CompleteMultipartUpload>");
            return stringMarshaller.marshall(xmlBody.toString());
        }

    }

    public static final class CreateSelectObjectMetadataRequestMarshaller
            implements RequestMarshaller2<CreateSelectObjectMetadataRequest> {

        @Override
        public byte[] marshall(CreateSelectObjectMetadataRequest request) {
            StringBuilder xmlBody = new StringBuilder();
            InputSerialization inputSerialization = request.getInputSerialization();
            CSVFormat csvFormat = inputSerialization.getCsvInputFormat();
            JsonFormat jsonFormat = inputSerialization.getJsonInputFormat();
            if (inputSerialization.getSelectContentFormat() == SelectContentFormat.CSV) {
                xmlBody.append("<CsvMetaRequest>");
                xmlBody.append("<InputSerialization>");
                xmlBody.append("<CompressionType>" + inputSerialization.getCompressionType() + "</CompressionType>");
                xmlBody.append("<CSV>");
                xmlBody.append("<RecordDelimiter>" + BinaryUtil.toBase64String(csvFormat.getRecordDelimiter().getBytes()) + "</RecordDelimiter>");
                xmlBody.append("<FieldDelimiter>" + BinaryUtil.toBase64String(csvFormat.getFieldDelimiter().toString().getBytes()) + "</FieldDelimiter>");
                xmlBody.append("<QuoteCharacter>" + BinaryUtil.toBase64String(csvFormat.getQuoteChar().toString().getBytes()) + "</QuoteCharacter>");
                xmlBody.append("</CSV>");
                xmlBody.append("</InputSerialization>");
                xmlBody.append("<OverwriteIfExists>" + request.isOverwrite() + "</OverwriteIfExists>");
                xmlBody.append("</CsvMetaRequest>");
            } else {
                xmlBody.append("<JsonMetaRequest>");
                xmlBody.append("<InputSerialization>");
                xmlBody.append("<CompressionType>" + inputSerialization.getCompressionType() + "</CompressionType>");
                xmlBody.append("<JSON>");
                xmlBody.append("<Type>" + jsonFormat.getJsonType().name() + "</Type>");
                xmlBody.append("</JSON>");
                xmlBody.append("</InputSerialization>");
                xmlBody.append("<OverwriteIfExists>" + request.isOverwrite() + "</OverwriteIfExists>");
                xmlBody.append("</JsonMetaRequest>");
            }

            try {
                return xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
        }
    }

    private static void populateSelectRange(StringBuilder xmlBody, SelectObjectRequest request) {
        if (request.getLineRange() != null) {
            xmlBody.append("<Range>" + request.lineRangeToString(request.getLineRange()) + "</Range>");
        }
        if (request.getSplitRange() != null) {
            xmlBody.append("<Range>" + request.splitRangeToString(request.getSplitRange()) + "</Range>");
        }
    }

    private static void populateSelectJsonObjectRequest(StringBuilder xmlBody, SelectObjectRequest request) {
        InputSerialization inputSerialization = request.getInputSerialization();
        JsonFormat jsonInputFormat = inputSerialization.getJsonInputFormat();
        xmlBody.append("<InputSerialization>");
        xmlBody.append("<CompressionType>" + inputSerialization.getCompressionType() + "</CompressionType>");
        xmlBody.append("<JSON>");
        xmlBody.append("<Type>" + jsonInputFormat.getJsonType().name() + "</Type>");
        xmlBody.append("<ParseJsonNumberAsString>" + jsonInputFormat.isParseJsonNumberAsString() + "</ParseJsonNumberAsString>");
        populateSelectRange(xmlBody, request);
        xmlBody.append("</JSON>");
        xmlBody.append("</InputSerialization>");

        OutputSerialization outputSerialization = request.getOutputSerialization();
        xmlBody.append("<OutputSerialization>");
        xmlBody.append("<JSON>");
        xmlBody.append("<RecordDelimiter>" + BinaryUtil.toBase64String(outputSerialization.getJsonOutputFormat().getRecordDelimiter().getBytes()) + "</RecordDelimiter>");
        xmlBody.append("</JSON>");
        xmlBody.append("<OutputRawData>" + outputSerialization.isOutputRawData() + "</OutputRawData>");
        xmlBody.append("<EnablePayloadCrc>" + outputSerialization.isPayloadCrcEnabled() + "</EnablePayloadCrc>");
        xmlBody.append("</OutputSerialization>");
    }

    private static void populateSelectCsvObjectRequest(StringBuilder xmlBody, SelectObjectRequest request) {
        InputSerialization inputSerialization = request.getInputSerialization();
        CSVFormat csvInputFormat = inputSerialization.getCsvInputFormat();
        xmlBody.append("<InputSerialization>");
        xmlBody.append("<CompressionType>" + inputSerialization.getCompressionType() + "</CompressionType>");
        xmlBody.append("<CSV>");
        xmlBody.append("<FileHeaderInfo>" + csvInputFormat.getHeaderInfo() + "</FileHeaderInfo>");
        xmlBody.append("<AllowQuotedRecordDelimiter>" + csvInputFormat.isAllowQuotedRecordDelimiter() + "</AllowQuotedRecordDelimiter>");
        xmlBody.append("<RecordDelimiter>" + BinaryUtil.toBase64String(csvInputFormat.getRecordDelimiter().getBytes()) + "</RecordDelimiter>");
        xmlBody.append("<FieldDelimiter>" + BinaryUtil.toBase64String(csvInputFormat.getFieldDelimiter().toString().getBytes()) + "</FieldDelimiter>");
        xmlBody.append("<QuoteCharacter>" + BinaryUtil.toBase64String(csvInputFormat.getQuoteChar().toString().getBytes()) + "</QuoteCharacter>");
        xmlBody.append("<CommentCharacter>" + BinaryUtil.toBase64String(csvInputFormat.getCommentChar().toString().getBytes()) + "</CommentCharacter>");
        populateSelectRange(xmlBody, request);
        xmlBody.append("</CSV>");
        xmlBody.append("</InputSerialization>");

        OutputSerialization outputSerialization = request.getOutputSerialization();
        xmlBody.append("<OutputSerialization>");
        xmlBody.append("<CSV>");
        xmlBody.append("<RecordDelimiter>" + BinaryUtil.toBase64String(outputSerialization.getCsvOutputFormat().getRecordDelimiter().getBytes()) + "</RecordDelimiter>");
        xmlBody.append("<FieldDelimiter>" + BinaryUtil.toBase64String(outputSerialization.getCsvOutputFormat().getFieldDelimiter().toString().getBytes()) + "</FieldDelimiter>");
        xmlBody.append("<QuoteCharacter>" + BinaryUtil.toBase64String(outputSerialization.getCsvOutputFormat().getQuoteChar().toString().getBytes()) + "</QuoteCharacter>");
        xmlBody.append("</CSV>");
        xmlBody.append("<KeepAllColumns>" + outputSerialization.isKeepAllColumns() + "</KeepAllColumns>");
        xmlBody.append("<OutputHeader>" + outputSerialization.isOutputHeader() + "</OutputHeader>");
        xmlBody.append("<OutputRawData>" + outputSerialization.isOutputRawData() + "</OutputRawData>");
        xmlBody.append("<EnablePayloadCrc>" + outputSerialization.isPayloadCrcEnabled() + "</EnablePayloadCrc>");
        xmlBody.append("</OutputSerialization>");
    }

    public static final class SelectObjectRequestMarshaller implements RequestMarshaller2<SelectObjectRequest> {

        @Override
        public byte[] marshall(SelectObjectRequest request) {
            StringBuilder xmlBody = new StringBuilder();
            xmlBody.append("<SelectRequest>");

            xmlBody.append("<Expression>" + BinaryUtil.toBase64String(request.getExpression().getBytes()) + "</Expression>");
            xmlBody.append("<Options>");
            xmlBody.append("<SkipPartialDataRecord>" + request.isSkipPartialDataRecord() + "</SkipPartialDataRecord>");
            if (request.getMaxSkippedRecordsAllowed() > 0) {
                xmlBody.append("<MaxSkippedRecordsAllowed>" + request.getMaxSkippedRecordsAllowed() + "</MaxSkippedRecordsAllowed>");
            }
            xmlBody.append("</Options>");
            InputSerialization inputSerialization = request.getInputSerialization();
            SelectContentFormat selectContentFormat = inputSerialization.getSelectContentFormat();

            if (selectContentFormat == SelectContentFormat.JSON) {
                populateSelectJsonObjectRequest(xmlBody, request);
            } else {
                populateSelectCsvObjectRequest(xmlBody, request);
            }

            xmlBody.append("</SelectRequest>");

            try {
                return xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
        }
    }

    public static final class DeleteObjectsRequestMarshaller implements RequestMarshaller2<DeleteObjectsRequest> {

        @Override
        public byte[] marshall(DeleteObjectsRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            boolean quiet = request.isQuiet();
            List<String> keysToDelete = request.getKeys();

            xmlBody.append("<Delete>");
            xmlBody.append("<Quiet>" + quiet + "</Quiet>");
            for (int i = 0; i < keysToDelete.size(); i++) {
                String key = keysToDelete.get(i);
                xmlBody.append("<Object>");
                xmlBody.append("<Key>" + escapeKey(key) + "</Key>");
                xmlBody.append("</Object>");
            }
            xmlBody.append("</Delete>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }
    
    public static final class DeleteVersionsRequestMarshaller implements RequestMarshaller2<DeleteVersionsRequest> {

        @Override
        public byte[] marshall(DeleteVersionsRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            boolean quiet = request.getQuiet();
            List<KeyVersion> keysToDelete = request.getKeys();

            xmlBody.append("<Delete>");
            xmlBody.append("<Quiet>" + quiet + "</Quiet>");
            for (int i = 0; i < keysToDelete.size(); i++) {
                KeyVersion key = keysToDelete.get(i);
                xmlBody.append("<Object>");
                xmlBody.append("<Key>" +
                    escapeKey(key.getKey()) + "</Key>");
                if (key.getVersion() != null) {
                    xmlBody.append("<VersionId>" + key.getVersion() + "</VersionId>");
                }
                xmlBody.append("</Object>");
            }
            xmlBody.append("</Delete>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class SetBucketTaggingRequestMarshaller implements RequestMarshaller<SetTaggingRequest> {

        @Override
        public FixedLengthInputStream marshall(SetTaggingRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            TagSet tagSet = request.getTagSet();
            xmlBody.append("<Tagging><TagSet>");
            Map<String, String> tags = tagSet.getAllTags();
            if (!tags.isEmpty()) {
                for (Map.Entry<String, String> tag : tags.entrySet()) {
                    xmlBody.append("<Tag>");
                    xmlBody.append("<Key>" + tag.getKey() + "</Key>");
                    xmlBody.append("<Value>" + tag.getValue() + "</Value>");
                    xmlBody.append("</Tag>");
                }
            }
            xmlBody.append("</TagSet></Tagging>");
            return stringMarshaller.marshall(xmlBody.toString());
        }

    }

    public static final class AddBucketReplicationRequestMarshaller
            implements RequestMarshaller<AddBucketReplicationRequest> {

        @Override
        public FixedLengthInputStream marshall(AddBucketReplicationRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<ReplicationConfiguration>");
            xmlBody.append("<Rule>");
            xmlBody.append("<ID>" + escapeKey(request.getReplicationRuleID()) + "</ID>");
            xmlBody.append("<Destination>");
            xmlBody.append("<Bucket>" + request.getTargetBucketName() + "</Bucket>");
            if (request.getTargetBucketLocation() != null) {
                xmlBody.append("<Location>" + request.getTargetBucketLocation() + "</Location>");
            } else if (request.getTargetCloud() != null && request.getTargetCloudLocation() != null) {
                xmlBody.append("<Cloud>" + request.getTargetCloud() + "</Cloud>");
                xmlBody.append("<CloudLocation>" + request.getTargetCloudLocation() + "</CloudLocation>");
            }

            xmlBody.append("</Destination>");
            if (request.isEnableHistoricalObjectReplication()) {
                xmlBody.append("<HistoricalObjectReplication>" + "enabled" + "</HistoricalObjectReplication>");
            } else {
                xmlBody.append("<HistoricalObjectReplication>" + "disabled" + "</HistoricalObjectReplication>");
            }
            if (request.getObjectPrefixList() != null && request.getObjectPrefixList().size() > 0) {
                xmlBody.append("<PrefixSet>");
                for (String prefix : request.getObjectPrefixList()) {
                    xmlBody.append("<Prefix>" + prefix + "</Prefix>");
                }
                xmlBody.append("</PrefixSet>");
            }
            if (request.getReplicationActionList() != null && request.getReplicationActionList().size() > 0) {
                xmlBody.append("<Action>" + RequestMarshallers.joinRepliationAction(request.getReplicationActionList())
                        + "</Action>");
            }

            if (request.getSyncRole() != null) {
                xmlBody.append("<SyncRole>" + request.getSyncRole() + "</SyncRole>");
            }
            if (request.getReplicaKmsKeyID() != null) {
                xmlBody.append("<EncryptionConfiguration>");
                xmlBody.append("<ReplicaKmsKeyID>" + request.getReplicaKmsKeyID() + "</ReplicaKmsKeyID>");
                xmlBody.append("</EncryptionConfiguration>");
            }
            if (request.ENABLED.equals(request.getSseKmsEncryptedObjectsStatus()) ||
                    request.DISABLED.equals(request.getSseKmsEncryptedObjectsStatus())) {
                xmlBody.append("<SourceSelectionCriteria><SseKmsEncryptedObjects>");
                xmlBody.append("<Status>" + request.getSseKmsEncryptedObjectsStatus() + "</Status>");
                xmlBody.append("</SseKmsEncryptedObjects></SourceSelectionCriteria>");
            }

            xmlBody.append("</Rule>");
            xmlBody.append("</ReplicationConfiguration>");
            return stringMarshaller.marshall(xmlBody.toString());
        }

    }

    public static final class DeleteBucketReplicationRequestMarshaller
            implements RequestMarshaller2<DeleteBucketReplicationRequest> {

        @Override
        public byte[] marshall(DeleteBucketReplicationRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<ReplicationRules>");
            xmlBody.append("<ID>" + escapeKey(request.getReplicationRuleID()) + "</ID>");
            xmlBody.append("</ReplicationRules>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class AddBucketCnameRequestMarshaller implements RequestMarshaller2<AddBucketCnameRequest> {

        @Override
        public byte[] marshall(AddBucketCnameRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<BucketCnameConfiguration>");
            xmlBody.append("<Cname>");
            xmlBody.append("<Domain>" + request.getDomain() + "</Domain>");
            xmlBody.append("</Cname>");
            xmlBody.append("</BucketCnameConfiguration>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class DeleteBucketCnameRequestMarshaller
            implements RequestMarshaller2<DeleteBucketCnameRequest> {

        @Override
        public byte[] marshall(DeleteBucketCnameRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<BucketCnameConfiguration>");
            xmlBody.append("<Cname>");
            xmlBody.append("<Domain>" + request.getDomain() + "</Domain>");
            xmlBody.append("</Cname>");
            xmlBody.append("</BucketCnameConfiguration>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class SetBucketQosRequestMarshaller implements RequestMarshaller2<UserQos> {

        @Override
        public byte[] marshall(UserQos userQos) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<BucketUserQos>");
            if (userQos.hasStorageCapacity()) {
                xmlBody.append("<StorageCapacity>" + userQos.getStorageCapacity() + "</StorageCapacity>");
            }
            xmlBody.append("</BucketUserQos>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }
    
    public static final class SetBucketVersioningRequestMarshaller
        implements RequestMarshaller2<SetBucketVersioningRequest> {

        @Override
        public byte[] marshall(SetBucketVersioningRequest setBucketVersioningRequest) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<VersioningConfiguration>");
            xmlBody
                .append("<Status>" + setBucketVersioningRequest.getVersioningConfiguration().getStatus() + "</Status>");
            xmlBody.append("</VersioningConfiguration>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class SetBucketEncryptionRequestMarshaller
    implements RequestMarshaller2<SetBucketEncryptionRequest> {

    	@Override
    	public byte[] marshall(SetBucketEncryptionRequest setBucketEncryptionRequest) {
    		StringBuffer xmlBody = new StringBuffer();
    		ServerSideEncryptionConfiguration sseConfig =
    				setBucketEncryptionRequest.getServerSideEncryptionConfiguration();
    		ServerSideEncryptionByDefault sseByDefault = sseConfig.getApplyServerSideEncryptionByDefault();

    		xmlBody.append("<ServerSideEncryptionRule>");
    		xmlBody.append("<ApplyServerSideEncryptionByDefault>");

    		xmlBody.append("<SSEAlgorithm>" + sseByDefault.getSSEAlgorithm() + "</SSEAlgorithm>");
    		if (sseByDefault.getKMSMasterKeyID() != null) {
    			xmlBody.append("<KMSMasterKeyID>" + sseByDefault.getKMSMasterKeyID() + "</KMSMasterKeyID>");
    		} else {
    			xmlBody.append("<KMSMasterKeyID></KMSMasterKeyID>");
    		}

    		xmlBody.append("</ApplyServerSideEncryptionByDefault>");
    		xmlBody.append("</ServerSideEncryptionRule>");

    		byte[] rawData = null;
    		try {
    			rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
    		} catch (UnsupportedEncodingException e) {
    			throw new ClientException("Unsupported encoding " + e.getMessage(), e);
    		}
    		return rawData;
    	}

    }

    public static final class SetBucketPolicyRequestMarshaller
            implements RequestMarshaller2<SetBucketPolicyRequest> {

        @Override
        public byte[] marshall(SetBucketPolicyRequest setBucketPolicyRequest) {

            byte[] rawData = null;
            try {
                rawData = setBucketPolicyRequest.getPolicyText().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class CreateLiveChannelRequestMarshaller
            implements RequestMarshaller2<CreateLiveChannelRequest> {

        @Override
        public byte[] marshall(CreateLiveChannelRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<LiveChannelConfiguration>");
            xmlBody.append("<Description>" + request.getLiveChannelDescription() + "</Description>");
            xmlBody.append("<Status>" + request.getLiveChannelStatus() + "</Status>");

            LiveChannelTarget target = request.getLiveChannelTarget();
            xmlBody.append("<Target>");
            xmlBody.append("<Type>" + target.getType() + "</Type>");
            xmlBody.append("<FragDuration>" + target.getFragDuration() + "</FragDuration>");
            xmlBody.append("<FragCount>" + target.getFragCount() + "</FragCount>");
            xmlBody.append("<PlaylistName>" + target.getPlaylistName() + "</PlaylistName>");
            xmlBody.append("</Target>");
            xmlBody.append("</LiveChannelConfiguration>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class SetBucketRequestPaymentRequestMarshaller implements RequestMarshaller2<String> {

        @Override
        public byte[] marshall(String payer) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<RequestPaymentConfiguration>");
            xmlBody.append("<Payer>" +payer + "</Payer>");
            xmlBody.append("</RequestPaymentConfiguration>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class SetBucketQosInfoRequestMarshaller implements RequestMarshaller2<BucketQosInfo> {

        @Override
        public byte[] marshall(BucketQosInfo bucketQosInfo) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<QoSConfiguration>");
            if (bucketQosInfo.getTotalUploadBw() != null) {
                xmlBody.append("<TotalUploadBandwidth>" +bucketQosInfo.getTotalUploadBw() + "</TotalUploadBandwidth>");
            }

            if (bucketQosInfo.getIntranetUploadBw() != null) {
                xmlBody.append("<IntranetUploadBandwidth>" +bucketQosInfo.getIntranetUploadBw() + "</IntranetUploadBandwidth>");
            }

            if (bucketQosInfo.getExtranetUploadBw() != null) {
                xmlBody.append("<ExtranetUploadBandwidth>" +bucketQosInfo.getExtranetUploadBw() + "</ExtranetUploadBandwidth>");
            }

            if (bucketQosInfo.getTotalDownloadBw() != null) {
                xmlBody.append("<TotalDownloadBandwidth>" +bucketQosInfo.getTotalDownloadBw() + "</TotalDownloadBandwidth>");
            }

            if (bucketQosInfo.getIntranetDownloadBw() != null) {
                xmlBody.append("<IntranetDownloadBandwidth>" +bucketQosInfo.getIntranetDownloadBw() + "</IntranetDownloadBandwidth>");
            }

            if (bucketQosInfo.getExtranetDownloadBw() != null) {
                xmlBody.append("<ExtranetDownloadBandwidth>" +bucketQosInfo.getExtranetDownloadBw() + "</ExtranetDownloadBandwidth>");
            }

            if (bucketQosInfo.getTotalQps() != null) {
                xmlBody.append("<TotalQps>" +bucketQosInfo.getTotalQps() + "</TotalQps>");
            }

            if (bucketQosInfo.getIntranetQps() != null) {
                xmlBody.append("<IntranetQps>" +bucketQosInfo.getIntranetQps() + "</IntranetQps>");
            }

            if (bucketQosInfo.getExtranetQps() != null) {
                xmlBody.append("<ExtranetQps>" +bucketQosInfo.getExtranetQps() + "</ExtranetQps>");
            }

            xmlBody.append("</QoSConfiguration>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class SetAsyncFetchTaskRequestMarshaller implements RequestMarshaller2<AsyncFetchTaskConfiguration> {

        @Override
        public byte[] marshall(AsyncFetchTaskConfiguration asyncFetchTaskConfiguration) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<AsyncFetchTaskConfiguration>");

            if (asyncFetchTaskConfiguration.getUrl() != null) {
                xmlBody.append("<Url>" + escapeKey(asyncFetchTaskConfiguration.getUrl()) + "</Url>");
            }

            if (asyncFetchTaskConfiguration.getObjectName() != null) {
                xmlBody.append("<Object>" + asyncFetchTaskConfiguration.getObjectName() + "</Object>");
            }

            if (asyncFetchTaskConfiguration.getHost() != null) {
                xmlBody.append("<Host>" + asyncFetchTaskConfiguration.getHost() + "</Host>");
            }

            if (asyncFetchTaskConfiguration.getContentMd5() != null) {
                xmlBody.append("<ContentMD5>" + asyncFetchTaskConfiguration.getContentMd5() + "</ContentMD5>");
            }

            if (asyncFetchTaskConfiguration.getCallback() != null) {
                xmlBody.append("<Callback>" + asyncFetchTaskConfiguration.getCallback() + "</Callback>");
            }

            if (asyncFetchTaskConfiguration.getIgnoreSameKey() != null) {
                xmlBody.append("<IgnoreSameKey>" + asyncFetchTaskConfiguration.getIgnoreSameKey() + "</IgnoreSameKey>");
            }

            xmlBody.append("</AsyncFetchTaskConfiguration>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class CreateUdfRequestMarshaller implements RequestMarshaller2<CreateUdfRequest> {

        @Override
        public byte[] marshall(CreateUdfRequest request) {
            StringBuffer xmlBody = new StringBuffer();

            xmlBody.append("<CreateUDFConfiguration>");
            xmlBody.append("<Name>" + request.getName() + "</Name>");
            if (request.getId() != null) {
                xmlBody.append("<ID>" + request.getId() + "</ID>");
            }
            if (request.getDesc() != null) {
                xmlBody.append("<Description>" + request.getDesc() + "</Description>");
            }
            xmlBody.append("</CreateUDFConfiguration>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class CreateUdfApplicationRequestMarshaller
            implements RequestMarshaller2<CreateUdfApplicationRequest> {

        @Override
        public byte[] marshall(CreateUdfApplicationRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            UdfApplicationConfiguration config = request.getUdfApplicationConfiguration();

            xmlBody.append("<CreateUDFApplicationConfiguration>");
            xmlBody.append("<ImageVersion>" + config.getImageVersion() + "</ImageVersion>");
            xmlBody.append("<InstanceNum>" + config.getInstanceNum() + "</InstanceNum>");
            xmlBody.append("<Flavor>");
            xmlBody.append("<InstanceType>" + config.getFlavor().getInstanceType() + "</InstanceType>");
            xmlBody.append("</Flavor>");
            xmlBody.append("</CreateUDFApplicationConfiguration>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class UpgradeUdfApplicationRequestMarshaller
            implements RequestMarshaller2<UpgradeUdfApplicationRequest> {

        @Override
        public byte[] marshall(UpgradeUdfApplicationRequest request) {
            StringBuffer xmlBody = new StringBuffer();

            xmlBody.append("<UpgradeUDFApplicationConfiguration>");
            xmlBody.append("<ImageVersion>" + request.getImageVersion() + "</ImageVersion>");
            xmlBody.append("</UpgradeUDFApplicationConfiguration>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class ResizeUdfApplicationRequestMarshaller
            implements RequestMarshaller2<ResizeUdfApplicationRequest> {

        @Override
        public byte[] marshall(ResizeUdfApplicationRequest request) {
            StringBuffer xmlBody = new StringBuffer();

            xmlBody.append("<ResizeUDFApplicationConfiguration>");
            xmlBody.append("<InstanceNum>" + request.getInstanceNum() + "</InstanceNum>");
            xmlBody.append("</ResizeUDFApplicationConfiguration>");

            byte[] rawData = null;
            try {
                rawData = xmlBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    public static final class ProcessObjectRequestMarshaller implements RequestMarshaller2<ProcessObjectRequest> {

        @Override
        public byte[] marshall(ProcessObjectRequest request) {
            StringBuffer processBody = new StringBuffer();

            processBody.append(RequestParameters.SUBRESOURCE_PROCESS);
            processBody.append("=" + request.getProcess());

            byte[] rawData = null;
            try {
                rawData = processBody.toString().getBytes(DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding " + e.getMessage(), e);
            }
            return rawData;
        }

    }

    private static enum EscapedChar {
        // "\r"
        RETURN("&#x000D;"),

        // "\n"
        NEWLINE("&#x000A;"),

        // " "
        SPACE("&#x0020;"),

        // "\t"
        TAB("&#x0009;"),

        // """
        QUOT("&quot;"),

        // "&"
        AMP("&amp;"),

        // "<"
        LT("&lt;"),

        // ">"
        GT("&gt;");

        private final String escapedChar;

        private EscapedChar(String escapedChar) {
            this.escapedChar = escapedChar;
        }

        @Override
        public String toString() {
            return this.escapedChar;
        }
    }

    private static String escapeKey(String key) {
        if (key == null) {
            return "";
        }

        int pos;
        int len = key.length();
        StringBuilder builder = new StringBuilder();
        for (pos = 0; pos < len; pos++) {
            char ch = key.charAt(pos);
            EscapedChar escapedChar;
            switch (ch) {
            case '\t':
                escapedChar = EscapedChar.TAB;
                break;
            case '\n':
                escapedChar = EscapedChar.NEWLINE;
                break;
            case '\r':
                escapedChar = EscapedChar.RETURN;
                break;
            case '&':
                escapedChar = EscapedChar.AMP;
                break;
            case '"':
                escapedChar = EscapedChar.QUOT;
                break;
            case '<':
                escapedChar = EscapedChar.LT;
                break;
            case '>':
                escapedChar = EscapedChar.GT;
                break;
            default:
                escapedChar = null;
                break;
            }

            if (escapedChar != null) {
                builder.append(escapedChar.toString());
            } else {
                builder.append(ch);
            }
        }

        return builder.toString();
    }

    private static String joinRepliationAction(List<ReplicationAction> actions) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (ReplicationAction action : actions) {
            if (!first) {
                sb.append(",");
            }
            sb.append(action);

            first = false;
        }

        return sb.toString();
    }

}
