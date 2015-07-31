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

package com.aliyun.oss.model;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 指定从OSS下载Object的请求参数。
 */
public class GetObjectRequest extends WebServiceRequest {

    // Object所在的Bucket的名称
    private String bucketName;

    // Object Key
    private String key;

    // 指定返回Object内容的字节范围。
    private long[] range;

    private List<String> matchingETagConstraints = new ArrayList<String>();
    private List<String> nonmatchingEtagConstraints = new ArrayList<String>();
    private Date unmodifiedSinceConstraint;
    private Date modifiedSinceConstraint;

    private ResponseHeaderOverrides responseHeaders;
    
    /**
     * Fields releated with getobject operation by using url signature.
     */
    private URL absoluteUrl;
    private boolean useUrlSignature = false;

    /**
     * 构造函数。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object Key。
     */
    public GetObjectRequest(String bucketName, String key) {
        setBucketName(bucketName);
        setKey(key);
    }
    
    /**
     * 使用URL签名及用户自定义头作为参数的构造函数。
     * @param absoluteUri URL签名。
     * @param requestHeaders 请求头。
     */
    public GetObjectRequest(URL absoluteUrl, Map<String, String> requestHeaders) {
    	this.absoluteUrl = absoluteUrl;
    	this.useUrlSignature = true;
    	this.getHeaders().clear();
    	if (requestHeaders != null && !requestHeaders.isEmpty()) {
    		this.getHeaders().putAll(requestHeaders);
    	}
    }

    /**
     * 返回Bucket名称。
     * @return Bucket名称。
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置Bucket名称。
     * @param bucketName
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 返回Object Key。
     * @return Object Key。
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置Object Key。
     * @param key
     *          Object Key。
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 返回一个值表示请求应当返回Object内容的字节范围。
     * @return 一个值表示请求应当返回Object内容的字节范围。
     */
    public long[] getRange() {
        return range;
    }

    /**
     * 设置一个值表示请求应当返回Object内容的字节范围（可选）。
     * @param start
     *          <p>范围的起始值。</p>
     *          <p>当值大于或等于0时，表示起始的字节位置。
     *          当值为-1时，表示不设置起始的字节位置，此时end参数不能-1，
     *          例如end为100，Range请求头的值为bytes=-100，表示获取最后100个字节。
     *          </p>
     * @param end
     *          <p>范围的结束值。</p>
     *          <p>当值小于或等于0时，表示结束的字节位或最后的字节数。
     *          当值为-1时，表示不设置结束的字节位置，此时start参数不能为-1，
     *          例如start为99，Range请求头的值为bytes=99-，表示获取第100个字节及
     *          以后的所有内容。
     *          </p>
     */
    public void setRange(long start, long end) {
    	range = new long[] {start, end};
    }

    /**
     * 返回“If-Match”参数，表示：如果传入期望的 ETag 和 object 的 ETag 匹配，正常的发送文件。
     * 如果不符合，返回错误。
     * @return 表示期望object的ETag与之匹配的ETag列表。
     */
    public List<String> getMatchingETagConstraints() {
        return matchingETagConstraints;
    }

    /**
     * 返回“If-Match”参数（可选）。
     * 表示如果传入期望的 ETag 和 Object 的 ETag 匹配，则正常的发送文件。
     * 如果不符合，则返回错误。
     * @param eTagList
     *          表示期望object的ETag与之匹配的ETag列表。
     *          目前OSS支持传入一个ETag，如果传入多于一个ETag，将只有列表中的第一个有效。
     */
    public void setMatchingETagConstraints(List<String> eTagList) {
        this.matchingETagConstraints.clear();
        if (eTagList != null && !eTagList.isEmpty()) {
        	this.matchingETagConstraints.addAll(eTagList);
        }
    }
    
    public void clearMatchingETagConstraints() {
    	this.matchingETagConstraints.clear();
    }

    /**
     * 返回“If-None-Match”参数，可以用来检查文件是否有更新。
     * 如果传入的 ETag值和Object的ETag 相同，返回错误；否则正常传输文件。 
     * @return 表示期望Object的ETag与之不匹配的ETag列表。
     */
    public List<String> getNonmatchingETagConstraints() {
        return nonmatchingEtagConstraints;
    }

    /**
     * 返回“If-None-Match”参数，可以用来检查文件是否有更新（可选）。
     * 如果传入的 ETag值和Object的ETag 相同，返回错误；否则正常传输文件。 
     * @param eTagList
     *          表示期望Object的ETag与之不匹配的ETag列表。
     *          目前OSS支持传入一个ETag，如果传入多于一个ETag，将只有列表中的第一个有效。
     */
    public void setNonmatchingETagConstraints(List<String> eTagList) {
    	this.nonmatchingEtagConstraints.clear();
        if (eTagList != null && !eTagList.isEmpty()) {
        	this.nonmatchingEtagConstraints.addAll(eTagList);
        }
    }
    
    public void clearNonmatchingETagConstraints() {
    	this.nonmatchingEtagConstraints.clear();
    }

    /**
     * 返回“If-Unmodified-Since”参数。
     * 表示：如果传入参数中的时间等于或者晚于文件实际修改时间，则传送文件；
     * 如果早于实际修改时间，则返回错误。 
     * @return “If-Unmodified-Since”参数。
     */
    public Date getUnmodifiedSinceConstraint() {
        return unmodifiedSinceConstraint;
    }

    /**
     * 设置“If-Unmodified-Since”参数（可选）。
     * 表示：如果传入参数中的时间等于或者晚于文件实际修改时间，则传送文件；
     * 如果早于实际修改时间，则返回错误。 
     * @param date
     *          “If-Unmodified-Since”参数。
     */
    public void setUnmodifiedSinceConstraint(Date date) {
        this.unmodifiedSinceConstraint = date;
    }

    /**
     * 返回“If-Modified-Since”参数。
     * 表示：如果指定的时间早于实际修改时间，则正常传送文件，并返回 200 OK；
     * 如果参数中的时间和实际修改时间一样或者更晚，会返回错误。
     * @return “If-Modified-Since”参数。
     */
    public Date getModifiedSinceConstraint() {
        return modifiedSinceConstraint;
    }

    /**
     * 设置“If-Modified-Since”参数（可选）。
     * 表示：如果指定的时间早于实际修改时间，则正常传送文件，并返回 200 OK；
     * 如果参数中的时间和实际修改时间一样或者更晚，会返回错误。
     * @param date
     *          “If-Modified-Since”参数。
     */
    public void setModifiedSinceConstraint(Date date) {
        this.modifiedSinceConstraint = date;
    }

    /**
     * 返回要重载的返回请求头。
     * @return 要重载的返回请求头。
     */
    public ResponseHeaderOverrides getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * 设置要重载的返回请求头（可选）。
     * @param responseHeaders
     *          要重载的返回请求头。
     */
    public void setResponseHeaders(ResponseHeaderOverrides responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

	public URL getAbsoluteUri() {
		return absoluteUrl;
	}

	public void setAbsoluteUri(URL absoluteUri) {
		this.absoluteUrl = absoluteUri;
	}

	public boolean isUseUrlSignature() {
		return useUrlSignature;
	}

	public void setUseUrlSignature(boolean useUrlSignature) {
		this.useUrlSignature = useUrlSignature;
	}
    
}
