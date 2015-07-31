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

import static com.aliyun.oss.common.utils.CodingUtils.isNullOrEmpty;
import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;
import static com.aliyun.oss.internal.OSSUtils.trimQuotes;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.parser.ResponseParseException;
import com.aliyun.oss.common.parser.ResponseParser;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.AppendObjectResult;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.BucketLoggingResult;
import com.aliyun.oss.model.BucketReferer;
import com.aliyun.oss.model.BucketWebsiteResult;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.GroupGrantee;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.LifecycleRule.RuleStatus;
import com.aliyun.oss.model.MultipartUpload;
import com.aliyun.oss.model.MultipartUploadListing;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.Owner;
import com.aliyun.oss.model.PartListing;
import com.aliyun.oss.model.PartSummary;
import com.aliyun.oss.model.Permission;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;
import com.aliyun.oss.model.UploadPartCopyResult;

/*
 * A collection of parsers that parse HTTP reponses into corresponding human-readable results.
 */
public final class ResponseParsers {
	
	public static final ListBucketResponseParser listBucketResponseParser = new ListBucketResponseParser();
	public static final GetBucketRefererResponseParser getBucketRefererResponseParser = new GetBucketRefererResponseParser();
	public static final GetBucketAclResponseParser getBucketAclResponseParser = new GetBucketAclResponseParser();	
	public static final GetBucketLocationResponseParser getBucketLocationResponseParser = new GetBucketLocationResponseParser();	
	public static final GetBucketLoggingResponseParser getBucketLoggingResponseParser = new GetBucketLoggingResponseParser();	
	public static final GetBucketWebsiteResponseParser getBucketWebsiteResponseParser = new GetBucketWebsiteResponseParser();	
	public static final GetBucketLifecycleResponseParser getBucketLifecycleResponseParser = new GetBucketLifecycleResponseParser();	
	public static final GetBucketCorsResponseParser getBucketCorsResponseParser = new GetBucketCorsResponseParser();	
	
	public static final ListObjectsReponseParser listObjectsReponseParser = new ListObjectsReponseParser();	
	public static final PutObjectReponseParser putObjectReponseParser = new PutObjectReponseParser();
	public static final AppendObjectResponseParser appendObjectResponseParser = new AppendObjectResponseParser();
	public static final GetObjectMetadataResponseParser getObjectMetadataResponseParser = new GetObjectMetadataResponseParser();	
	public static final CopyObjectResponseParser copyObjectResponseParser = new CopyObjectResponseParser();	
	public static final DeleteObjectsResponseParser deleteObjectsResponseParser = new DeleteObjectsResponseParser();	
	
	public static final CompleteMultipartUploadResponseParser completeMultipartUploadResponseParser = new CompleteMultipartUploadResponseParser();	
	public static final InitiateMultipartUploadResponseParser initiateMultipartUploadResponseParser = new InitiateMultipartUploadResponseParser();	
	public static final ListMultipartUploadsResponseParser listMultipartUploadsResponseParser = new ListMultipartUploadsResponseParser();	
	public static final ListPartsResponseParser listPartsResponseParser = new ListPartsResponseParser();	
	
	public static final class EmptyResponseParser implements ResponseParser<ResponseMessage> {

		@Override
		public ResponseMessage parse(ResponseMessage response)
				throws ResponseParseException {
			// Close response and return it directly without parsing.
			safeCloseResponse(response);
			return response;
		}
		
	}
	
	public static final class ListBucketResponseParser implements ResponseParser<BucketList> {

		@Override
		public BucketList parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseListBucket(response.getContent());
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
	
	public static final class GetBucketRefererResponseParser implements ResponseParser<BucketReferer> {
		
		@Override
		public BucketReferer parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseGetBucketReferer(response.getContent());
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
	
	public static final class GetBucketAclResponseParser implements ResponseParser<AccessControlList> {

		@Override
		public AccessControlList parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseGetBucketAcl(response.getContent());
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
	
	public static final class GetBucketLocationResponseParser implements ResponseParser<String> {

		@Override
		public String parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseGetBucketLocation(response.getContent());
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
	
	public static final class GetBucketLoggingResponseParser implements ResponseParser<BucketLoggingResult> {

		@Override
		public BucketLoggingResult parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseBucketLogging(response.getContent());
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
	
	public static final class GetBucketWebsiteResponseParser implements ResponseParser<BucketWebsiteResult> {

		@Override
		public BucketWebsiteResult parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseBucketWebsite(response.getContent());
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
	
	public static final class GetBucketLifecycleResponseParser implements ResponseParser<List<LifecycleRule>> {
		
		@Override
		public List<LifecycleRule> parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseGetBucketLifecycle(response.getContent());
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
	
	public static final class GetBucketCorsResponseParser implements ResponseParser<List<CORSRule>> {
		
		@Override
		public List<CORSRule> parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseListBucketCORS(response.getContent());
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
	
	public static final class ListObjectsReponseParser implements ResponseParser<ObjectListing> {
		
		@Override
		public ObjectListing parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseListObjects(response.getContent());
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
    
	public static final class PutObjectReponseParser implements ResponseParser<PutObjectResult> {
		
		@Override
		public PutObjectResult parse(ResponseMessage response)
				throws ResponseParseException {
			PutObjectResult result = new PutObjectResult();
			try {
				result.setETag(trimQuotes(response.getHeaders().get(OSSHeaders.ETAG)));
				return result;
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
	
	public static final class AppendObjectResponseParser implements ResponseParser<AppendObjectResult> {

		@Override
		public AppendObjectResult parse(ResponseMessage response)
				throws ResponseParseException {
			AppendObjectResult result = new AppendObjectResult();
			try {
				String nextPosition = response.getHeaders().get(OSSHeaders.OSS_NEXT_APPEND_POSITION);
				if (nextPosition != null) {
					result.setNextPosition(Long.valueOf(nextPosition));					
				}
				result.setObjectCRC64(response.getHeaders().get(OSSHeaders.OSS_HASH_CRC64_ECMA));
				return result;
			} catch (Exception e) {
				throw new ResponseParseException(e.getMessage(), e);
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
		public OSSObject parse(ResponseMessage response)
				throws ResponseParseException {
			OSSObject ossObject = new OSSObject();
			ossObject.setBucketName(this.bucketName);
			ossObject.setKey(this.key);
			ossObject.setObjectContent(response.getContent());
			try {
	            ossObject.setObjectMetadata(parseObjectMetadata(response.getHeaders()));
	            return ossObject;
	        } catch (ResponseParseException e) {
	        	// Close response only when parsing exception thrown. Otherwise, 
	        	// just hand over to SDK users and remain them close it when no longer in use.
	        	safeCloseResponse(response);
	            
	        	// Rethrow
	        	throw e;
	        }
		}
		
	}
	
	public static final class GetObjectMetadataResponseParser implements ResponseParser<ObjectMetadata> {
		
		@Override
		public ObjectMetadata parse(ResponseMessage response)
				throws ResponseParseException {
			try {
	            return parseObjectMetadata(response.getHeaders());
	        } finally {
	        	safeCloseResponse(response);	        	
	        }
		}
		
	}
	
	public static final class CopyObjectResponseParser implements ResponseParser<CopyObjectResult> {

		@Override
		public CopyObjectResult parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseCopyObjectResult(response.getContent());
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
	
	public static final class DeleteObjectsResponseParser implements ResponseParser<DeleteObjectsResult> {

		@Override
		public DeleteObjectsResult parse(ResponseMessage response)
				throws ResponseParseException {
			// Occurs when deleting multiple objects in quiet mode.
			if (response.getContentLength() == 0) {
				return new DeleteObjectsResult(null);
			}
			
			try {
				return parseDeleteObjectsResult(response.getContent());
			} finally {
				safeCloseResponse(response);
			}
		}
		
	}
	
	public static final class CompleteMultipartUploadResponseParser implements ResponseParser<CompleteMultipartUploadResult> {

		@Override
		public CompleteMultipartUploadResult parse(ResponseMessage response)
				throws ResponseParseException {
			try {
	            return parseCompleteMultipartUpload(response.getContent());
	        } finally {
	        	safeCloseResponse(response);	        	
	        }
		}
		
	}
	
	public static final class InitiateMultipartUploadResponseParser implements ResponseParser<InitiateMultipartUploadResult> {

		@Override
		public InitiateMultipartUploadResult parse(ResponseMessage response)
				throws ResponseParseException {
			try {
	            return parseInitiateMultipartUpload(response.getContent());
	        } finally {
	        	safeCloseResponse(response);	        	
	        }
		}
		
	}
	
	public static final class ListMultipartUploadsResponseParser implements ResponseParser<MultipartUploadListing> {
		
		@Override
		public MultipartUploadListing parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseListMultipartUploads(response.getContent());
			} finally {
				safeCloseResponse(response);	        	
			}
		}
		
	}
	
	public static final class ListPartsResponseParser implements ResponseParser<PartListing> {
		
		@Override
		public PartListing parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				return parseListParts(response.getContent());
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
		public UploadPartCopyResult parse(ResponseMessage response)
				throws ResponseParseException {
			try {
				UploadPartCopyResult result = new UploadPartCopyResult();
				result.setPartNumber(partNumber);
	            result.setETag(trimQuotes(parseUploadPartCopy(response.getContent())));
				return result;
			} finally {
				safeCloseResponse(response);	        	
			}
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
    public static ObjectListing parseListObjects(InputStream responseBody) 
    		throws ResponseParseException {
    	
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
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall get bucket acl response body to ACL.
     */
    public static AccessControlList parseGetBucketAcl(InputStream responseBody)
    		throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            AccessControlList acl = new AccessControlList();

            String id = root.getChild("Owner").getChildText("ID");
            String displayName = root.getChild("Owner").getChildText("DisplayName");
            Owner owner = new Owner(id, displayName);
            acl.setOwner(owner);

            String aclString = root.getChild("AccessControlList").getChildText("Grant");
            CannedAccessControlList cacl = CannedAccessControlList.parse(aclString);

            if (cacl == CannedAccessControlList.PublicRead) {
                acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
            } else if (cacl == CannedAccessControlList.PublicReadWrite) {
                acl.grantPermission(GroupGrantee.AllUsers, Permission.FullControl);
            }
            
            return acl;
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket referer response body to bucket referer list.
     */
    @SuppressWarnings("unchecked")
	public static BucketReferer parseGetBucketReferer(InputStream responseBody)
    		throws ResponseParseException {
    	
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
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }
    }
    
    /**
     * Unmarshall upload part copy response body to uploaded part's ETag.
     */
    public static String parseUploadPartCopy(InputStream responseBody)
    		throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            return  root.getChildText("ETag");            
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall list bucket response body to bucket list.
     */
    @SuppressWarnings("unchecked")
    public static BucketList parseListBucket(InputStream responseBody)
    		throws ResponseParseException {

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

                    buckets.add(bucket);
            	}
            }
            bucketList.setBucketList(buckets);
            
            return bucketList;
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }

    }
    
    /**
     * Unmarshall get bucket location response body to bucket location.
     */
    public static String parseGetBucketLocation(InputStream responseBody) 
    		throws ResponseParseException {
                
        try {
            Element root = getXmlRootElement(responseBody);
            return root.getText();
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall object metadata from response headers.
     */
    public static ObjectMetadata parseObjectMetadata(Map<String, String> headers) 
    		throws ResponseParseException {

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
                } else {
                    objectMetadata.setHeader(key, headers.get(key) );
                }
            }

        	return objectMetadata;
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall initiate multipart upload response body to corresponding result.
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
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall list multipart uploads response body to multipart upload listing.
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
            	// TODO: Occurs when multipart uploads cannot be fully listed ?
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
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall list parts response body to part listing.
     */
    @SuppressWarnings("unchecked")
    public static PartListing parseListParts(InputStream responseBody) 
    		throws ResponseParseException {
    	
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
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall complete multipart upload response body to corresponding result.
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
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket logging response body to corresponding result.
     */
    public static BucketLoggingResult parseBucketLogging(InputStream responseBody) 
    		throws ResponseParseException {

        try {
        	Element root = getXmlRootElement(responseBody);

        	BucketLoggingResult result = new BucketLoggingResult();
        	if(root.getChild("LoggingEnabled") != null) {
        		result.setTargetBucket(root.getChild("LoggingEnabled").getChildText("TargetBucket"));
        	}
        	if(root.getChild("LoggingEnabled") != null) {
        		result.setTargetPrefix(root.getChild("LoggingEnabled").getChildText("TargetPrefix"));
        	}

        	return result;
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket website response body to corresponding result.
     */
    public static BucketWebsiteResult parseBucketWebsite(InputStream responseBody)
    		throws ResponseParseException {

        try {
        	Element root = getXmlRootElement(responseBody);

        	BucketWebsiteResult result = new BucketWebsiteResult();
        	if(root.getChild("IndexDocument") != null) {
        		result.setIndexDocument(root.getChild("IndexDocument").getChildText("Suffix"));
        	}
        	if(root.getChild("ErrorDocument") != null) {
        		result.setErrorDocument(root.getChild("ErrorDocument").getChildText("Key"));
        	}
        	
        	return result;
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }
    }
    
    /**
     * Unmarshall copy object response body to corresponding result.
     */
    public static CopyObjectResult parseCopyObjectResult(InputStream responseBody) 
    		throws ResponseParseException {

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
	public static DeleteObjectsResult parseDeleteObjectsResult(InputStream responseBody) 
    		throws ResponseParseException {
    	
    	try {
    		Element root = getXmlRootElement(responseBody);
    		
    		DeleteObjectsResult deleteObjectsResult = new DeleteObjectsResult();
    		if (root.getChild("EncodingType") != null) {
            	String encodingType = root.getChildText("EncodingType");
            	deleteObjectsResult.setEncodingType(isNullOrEmpty(encodingType) ? null : encodingType);
            }
    		
    		List<String> deletedObjects = new ArrayList<String>();
    		List<Element> deletedElements = root.getChildren("Deleted");
    		for (Element elem : deletedElements) {
    			deletedObjects.add(elem.getChildText("Key"));
    		}
    		deleteObjectsResult.setDeletedObjects(deletedObjects);

    		return deleteObjectsResult;
    	} catch (Exception e) {
    		throw new ResponseParseException(e.getMessage(), e);
    	}
    }
    
    /**
     * Unmarshall get bucket cors response body to cors rules.
     */
    @SuppressWarnings("unchecked")
    public static List<CORSRule> parseListBucketCORS(InputStream responseBody) 
    		throws ResponseParseException{
    	
        try {
            Element root = getXmlRootElement(responseBody);
            
            List<CORSRule> corsRules = new ArrayList<CORSRule>();
            List<Element> corsRuleElems = root.getChildren("CORSRule");
            
        	for (Element corsRuleElem : corsRuleElems) {
        		CORSRule rule = new CORSRule();
        		
        		List<Element> allowedOriginElems =corsRuleElem.getChildren("AllowedOrigin");
        		for(Element allowedOriginElement : allowedOriginElems) {
        			rule.getAllowedOrigins().add(allowedOriginElement.getValue());
        		}
        		
        		List<Element> allowedMethodElems =corsRuleElem.getChildren("AllowedMethod");
        		for(Element allowedMethodElement : allowedMethodElems) {
        			rule.getAllowedMethods().add(allowedMethodElement.getValue());
        		}
        		
        		List<Element> allowedHeaderElems =corsRuleElem.getChildren("AllowedHeader");
        		for(Element allowedHeaderElement : allowedHeaderElems) {
        			rule.getAllowedHeaders().add(allowedHeaderElement.getValue());
        		}
        		
        		List<Element> exposeHeaderElems =corsRuleElem.getChildren("ExposeHeader");
        		for(Element exposeHeaderElement : exposeHeaderElems) {
        			rule.getExposeHeaders().add(exposeHeaderElement.getValue());
        		}
        		
        		Element maxAgeSecondsElem = corsRuleElem.getChild("MaxAgeSeconds");
        		if(maxAgeSecondsElem!=null) {
        			rule.setMaxAgeSeconds(Integer.parseInt(maxAgeSecondsElem.getValue()));
        		}
        		
        		corsRules.add(rule);
            }

        	return corsRules;
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }
    }
   
    /**
     * Unmarshall get bucket lifecycle response body to lifecycle rules.
     */
    @SuppressWarnings("unchecked")
    public static List<LifecycleRule> parseGetBucketLifecycle(InputStream responseBody) 
    		throws ResponseParseException {
       
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
        				Date expirationDate = DateUtil.parseIso8601Date(ruleElem.getChild("Expiration").getChildText("Date"));
        				rule.setExpirationTime(expirationDate);
        			} else {
        				rule.setExpriationDays(Integer.parseInt(ruleElem.getChild("Expiration").getChildText("Days")));
        			}
        		}
        		
        		lifecycleRules.add(rule);
            }
        	
        	return lifecycleRules;
        } catch (Exception e) {
        	throw new ResponseParseException(e.getMessage(), e);
        }
    }
}
