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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.aliyun.oss.internal.OSSConstants.DEFAULT_CHARSET_NAME;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.comm.io.FixedLengthInputStream;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.BucketReferer;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.SetBucketCORSRequest;
import com.aliyun.oss.model.SetBucketLifecycleRequest;
import com.aliyun.oss.model.SetBucketLoggingRequest;
import com.aliyun.oss.model.SetBucketWebsiteRequest;
import com.aliyun.oss.model.LifecycleRule.RuleStatus;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;

/**
 * A collection of marshallers that marshall HTTP request into crossponding input stream. 
 */
public final class RequestMarshallers {
	
	public static final StringMarshaller stringMarshaller = new StringMarshaller();
	
	public static final DeleteObjectsRequestMarshaller deleteObjectsRequestMarshaller = new DeleteObjectsRequestMarshaller();
	
	public static final CreateBucketRequestMarshaller createBucketRequestMarshaller = new CreateBucketRequestMarshaller();
	public static final BucketRefererMarshaller bucketRefererMarshaller = new BucketRefererMarshaller();
	public static final SetBucketLoggingRequestMarshaller setBucketLoggingRequestMarshaller = new SetBucketLoggingRequestMarshaller();
	public static final SetBucketWebsiteRequestMarshaller setBucketWebsiteRequestMarshaller = new SetBucketWebsiteRequestMarshaller();
	public static final SetBucketLifecycleRequestMarshaller setBucketLifecycleRequestMarshaller = new SetBucketLifecycleRequestMarshaller();
	public static final SetBucketCORSRequestMarshaller setBucketCORSRequestMarshaller = new SetBucketCORSRequestMarshaller();
	
	public static final CompleteMultipartUploadRequestMarshaller completeMultipartUploadRequestMarshaller = new CompleteMultipartUploadRequestMarshaller();
	
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
	
	public static final class CreateBucketRequestMarshaller implements RequestMarshaller<CreateBucketRequest> {

		@Override
		public FixedLengthInputStream marshall(CreateBucketRequest request) {
			StringBuffer xmlBody = new StringBuffer();
	        if (request.getLocationConstraint() != null) {
	        	xmlBody.append("<CreateBucketConfiguration>");
		        xmlBody.append("<LocationConstraint>" + request.getLocationConstraint() + "</LocationConstraint>");
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
		        if(request.getTargetPrefix() != null) {
		        	xmlBody.append("<TargetPrefix>" + request.getTargetPrefix() + "</TargetPrefix>");
		        }
		        xmlBody.append("</LoggingEnabled>");
	        } else {
	        	// Nothing to do here, user attempt to close bucket logging functionality 
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
	        xmlBody.append("<IndexDocument>");
	        xmlBody.append("<Suffix>" + request.getIndexDocument() + "</Suffix>");
	        xmlBody.append("</IndexDocument>");
	        if(request.getErrorDocument() != null){
	            xmlBody.append("<ErrorDocument>");
	        	xmlBody.append("<Key>" + request.getErrorDocument() + "</Key>");
	            xmlBody.append("</ErrorDocument>");
	        }
	        xmlBody.append("</WebsiteConfiguration>");
			return stringMarshaller.marshall(xmlBody.toString());
		}
		
	}
	
	public static final class SetBucketLifecycleRequestMarshaller implements RequestMarshaller<SetBucketLifecycleRequest> {

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
	            
	            if (rule.getStatus() == RuleStatus.Enabled) {
	            	xmlBody.append("<Status>Enabled</Status>");
	            } else {
	            	xmlBody.append("<Status>Disabled</Status>");
	            }
	            
	            if (rule.getExpirationTime() != null) {
	            	String formatDate = DateUtil.formatIso8601Date(rule.getExpirationTime());
	            	xmlBody.append("<Expiration><Date>" + formatDate + "</Date></Expiration>");
	            } else {
	            	xmlBody.append("<Expiration><Days>" + rule.getExpriationDays() + "</Days></Expiration>");
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
					for(String allowedHeader : rule.getAllowedHeaders()){
						xmlBody.append("<AllowedHeader>" +allowedHeader + "</AllowedHeader>");
					}
				}
				
				if (rule.getExposeHeaders().size() > 0) {
					for (String exposeHeader : rule.getExposeHeaders()) {
						xmlBody.append("<ExposeHeader>" +exposeHeader + "</ExposeHeader>");
					}
				}
				
				if(null != rule.getMaxAgeSeconds()) {
					xmlBody.append("<MaxAgeSeconds>" + rule.getMaxAgeSeconds() + "</MaxAgeSeconds>");
				}
				
				xmlBody.append("</CORSRule>");
			}
			xmlBody.append("</CORSConfiguration>");
			return stringMarshaller.marshall(xmlBody.toString());
		}
		
	}
	
	public static final class CompleteMultipartUploadRequestMarshaller implements RequestMarshaller<CompleteMultipartUploadRequest> {

		@Override
		public FixedLengthInputStream marshall(CompleteMultipartUploadRequest request) {
			StringBuffer xmlBody = new StringBuffer();
			List<PartETag> eTags =  request.getPartETags();
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
	
	public static final class DeleteObjectsRequestMarshaller implements RequestMarshaller2<DeleteObjectsRequest> {

		@Override
		public byte[] marshall(DeleteObjectsRequest request) {
			StringBuffer xmlBody = new StringBuffer();
			boolean quiet = request.isQuiet();
			List<String> keysToDelete =  request.getKeys();
	        
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
	
}
