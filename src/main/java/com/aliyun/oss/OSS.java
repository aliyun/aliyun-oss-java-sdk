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

package com.aliyun.oss;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.AbortMultipartUploadRequest;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.AppendObjectResult;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.BucketLoggingResult;
import com.aliyun.oss.model.BucketReferer;
import com.aliyun.oss.model.BucketWebsiteResult;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.HeadObjectRequest;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.ListBucketsRequest;
import com.aliyun.oss.model.ListMultipartUploadsRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ListPartsRequest;
import com.aliyun.oss.model.MultipartUploadListing;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectAcl;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.OptionsRequest;
import com.aliyun.oss.model.PartListing;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.SetBucketAclRequest;
import com.aliyun.oss.model.SetBucketCORSRequest;
import com.aliyun.oss.model.SetBucketRefererRequest;
import com.aliyun.oss.model.SetObjectAclRequest;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;
import com.aliyun.oss.model.SetBucketLifecycleRequest;
import com.aliyun.oss.model.SetBucketLoggingRequest;
import com.aliyun.oss.model.SetBucketWebsiteRequest;
import com.aliyun.oss.model.UploadPartCopyRequest;
import com.aliyun.oss.model.UploadPartCopyResult;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;

/**
 * 阿里云对象存储服务（Object Storage Service， OSS）的访问接口。
 * <p>
 * 阿里云存储服务（Object Storage Service，简称OSS），是阿里云对外提供的海量，安全，低成本，
 * 高可靠的云存储服务。用户可以通过简单的REST接口，在任何时间、任何地点上传和下载数据，
 * 也可以使用WEB页面对数据进行管理。<br />
 * 基于OSS，用户可以搭建出各种多媒体分享网站、网盘、个人企业数据备份等基于大规模数据的服务。
 * </p>
 */
public interface OSS {

    /**
     * 切换用户身份认证。
     * @param creds 用户身份认证。
     */
    public void switchCredentials(Credentials creds);
    
    /**
     * 关闭Client实例，并释放所有正在使用的资源。
     * 一旦关闭，将不再处理任何发往OSS的请求。
     */
    public void shutdown();
    
    /**
     * 创建{@link Bucket}。
     * @param bucketName
     *          Bucket名称。
     */
    public Bucket createBucket(String bucketName) 
            throws OSSException, ClientException;
    
    /**
     * 创建{@link Bucket}。
     * @param createBucketRequest
     *          请求参数{@link CreateBucketRequest}。
     */
    public Bucket createBucket(CreateBucketRequest createBucketRequest) 
            throws OSSException, ClientException;

    /**
     * 删除{@link Bucket}。
     * @param bucketName
     *          Bucket名称。
     */
    public void deleteBucket(String bucketName) 
            throws OSSException, ClientException;
    
    /**
     * 删除{@link Bucket}。
     * @param genericRequest
     *          请求信息。
     */
    public void deleteBucket(GenericRequest genericRequest) 
            throws OSSException, ClientException;

    /**
     * 返回请求者拥有的所有{@link Bucket}的列表。
     * @return
     *      请求者拥有的所有{@link Bucket}的列表。
     */
    public List<Bucket> listBuckets() throws OSSException, ClientException;

    /**
     * 按要求返回请求者的{@link Bucket}列表。
     * @param prefix
     *      限定返回的bucket的名字必须以prefix作为前缀，可以为null（表示不设置前缀）
     * @param marker
     *      设定结果从marker之后按字母排序的第一个开始返回，可以为null（表示没有marker的点，从头开始返回）
     * @param maxKeys
     *      限定此次返回bucket的最大数，取值不能大于1000，默认为100，可以为null（表示默认返回最多100个）
     * @return
     *      该次请求获得的所有{@link Bucket}的列表。
     */
    public BucketList listBuckets(String prefix, String marker, Integer maxKeys) 
            throws OSSException, ClientException;

    /**
     * 按要求返回请求者的{@link Bucket}列表。
     * @param listBucketsRequest
     *      请求信息
     * @return
     *      该次请求获得的所有{@link Bucket}的列表。
     */
    public BucketList listBuckets(ListBucketsRequest listBucketsRequest) 
            throws OSSException, ClientException;

    /**
     * 设置指定{@link Bucket}的Access Control List(ACL)。
     * @param bucketName
     *          Bucket名称。
     * @param acl
     *          {@link CannedAccessControlList}中列出的ACL。
     *          如果传入null，则保持Bucket原先的ACL不变。
     */
    public void setBucketAcl(String bucketName, CannedAccessControlList acl)
            throws OSSException, ClientException;
    
    /**
     * 设置指定{@link Bucket}的Access Control List(ACL)。
     * @param bucketName
     *          Bucket名称。
     * @param setBucketAclRequest
     *          请求信息。
     */
    public void setBucketAcl(SetBucketAclRequest setBucketAclRequest)
            throws OSSException, ClientException;

    /**
     * 返回给定{@link Bucket}的Access Control List(ACL)。
     * @param bucketName
     *          Bucket名称。
     * @return Access Control List(ACL) {@link AccessControlList}。
     */
    public AccessControlList getBucketAcl(String bucketName)
            throws OSSException, ClientException;
    
    /**
     * 返回给定{@link Bucket}的Access Control List(ACL)。
     * @param genericRequest
     *          请求信息。
     * @return Access Control List(ACL) {@link AccessControlList}。
     */
    public AccessControlList getBucketAcl(GenericRequest genericRequest)
            throws OSSException, ClientException;
 
    /**
     * 设置指定{@link Bucket}的http referer。
     * @param bucketName
     *          Bucket名称。
     * @param referer
     *          {@link BucketReferer}。
     *          如果传入null，则表示使用默认值{@link BucketReferer}。
     */
    public void setBucketReferer(String bucketName, BucketReferer referer)
            throws OSSException, ClientException;
    
    /**
     * 设置指定{@link Bucket}的http referer。
     * @param bucketName
     *          Bucket名称。
     * @param setBucketRefererRequest
     *          请求信息。
     */
    public void setBucketReferer(SetBucketRefererRequest setBucketRefererRequest)
            throws OSSException, ClientException;
    
    /**
     * 返回给定{@link Bucket}的http referer。
     * @param bucketName
     *          Bucket名称。
     * @return bucket http referer {@link BucketReferer}。
     */
    public BucketReferer getBucketReferer(String bucketName)
            throws OSSException, ClientException;
    
    /**
     * 返回给定{@link Bucket}的http referer。
     * @param genericRequest
     *          请求信息。
     * @return bucket http referer {@link BucketReferer}。
     */
    public BucketReferer getBucketReferer(GenericRequest genericRequest)
            throws OSSException, ClientException;
    
    /**
     * 返回给定{@link Bucket}所在的数据中心。
     * @param bucketName
     *          Bucket名称。
     * @return Bucket所在的数据中心。
     */ 
    public String getBucketLocation(String bucketName)
            throws OSSException, ClientException;
    
    /**
     * 返回给定{@link Bucket}所在的数据中心。
     * @param genericRequest
     *          请求信息。
     * @return Bucket所在的数据中心。
     */ 
    public String getBucketLocation(GenericRequest genericRequest)
            throws OSSException, ClientException;

    /**
     * 判断给定{@link Bucket}是否存在。
     * @param bucketName
     *          Bucket名称。
     */
    public boolean doesBucketExist(String bucketName) 
            throws OSSException, ClientException;
    
    /**
     * 判断给定{@link Bucket}是否存在。
     * @param genericRequest
     *          请求信息。
     */
    public boolean doesBucketExist(GenericRequest genericRequest)
            throws OSSException, ClientException;

    /**
     * 列出指定{@link Bucket}下的{@link OSSObject}。
     * @param bucketName
     *          Bucket名称。
     * @return Object列表{@link ObjectListing}
     */
    public ObjectListing listObjects(String bucketName) 
            throws OSSException, ClientException;

    /**
     * 列出指定{@link Bucket}下key以给定prefix开头的{@link OSSObject}。
     * @param bucketName
     *          Bucket名称。
     * @param prefix
     *          限定返回的Object key必须以prefix作为前缀。
     * @return Object列表{@link ObjectListing}
     * @throws OSSException
     * @throws ClientException
     */
    public ObjectListing listObjects(String bucketName, String prefix)
            throws OSSException, ClientException;

    /**
     * 列出指定{@link Bucket}下的{@link OSSObject}。
     * @param listObjectsRequest
     *          请求信息。
     * @return object列表{@link ObjectListing}
     * @throws OSSException
     * @throws ClientException
     */
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest)
            throws OSSException, ClientException;
    
    /**
     * 上传指定的{@link OSSObject}到OSS中指定的{@link Bucket}。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          object的key。
     * @param input
     *          输入流。
     */
    public PutObjectResult putObject(String bucketName, String key, InputStream input) 
            throws OSSException, ClientException;

    /**
     * 上传指定的{@link OSSObject}到OSS中指定的{@link Bucket}。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          object的key。
     * @param input
     *          输入流。
     * @param metadata
     *          object的元信息{@link ObjectMetadata}，若该元信息未包含Content-Length，
     *          则采用chunked编码传输请求数据。
     */
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata) 
            throws OSSException, ClientException;
    
    /**
     * 上传指定文件到OSS中指定的{@link Bucket}。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          object的key。
     * @param file
     *          指定上传文件。
     * @param metadata
     *          object的元信息{@link ObjectMetadata}，若该元信息未包含Content-Length，
     *          则采用chunked编码传输请求数据。
     */
    public PutObjectResult putObject(String bucketName, String key, File file, ObjectMetadata metadata) 
            throws OSSException, ClientException;
    
    /**
     * 上传指定文件到OSS中指定的{@link Bucket}。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          object的key。
     * @param file
     *          指定上传文件。
     */
    public PutObjectResult putObject(String bucketName, String key, File file) 
            throws OSSException, ClientException;
    
    /**
     * 上传指定文件或输入流至指定的{@link Bucket}。
     * @param putObjectRequest 请求参数{@link PutObjectRequest}。
     * @return 请求结果{@link PutObjectResult}实例。
     * @throws OSSException
     * @throws ClientException
     */
    public PutObjectResult putObject(PutObjectRequest putObjectRequest)
            throws OSSException, ClientException;
    
    /**
     * 使用URL签名方式上传指定文件。
     * @param signedUrl PUT请求类型的URL签名。
     * @param filePath 上传文件的路径。
     * @param requestHeaders 请求头（包括HTTP标准请求头、用户自定义请求头）。
     * @return 请求结果{@link PutObjectResult}实例。
     */
    public PutObjectResult putObject(URL signedUrl, String filePath, Map<String, String> requestHeaders) 
            throws OSSException, ClientException;
    
    /**
     * 使用URL签名方式上传指定文件。
     * @param signedUrl PUT请求类型的URL签名。
     * @param filePath 上传文件的路径。
     * @param requestHeaders 请求头（包括HTTP标准请求头、用户自定义请求头）。
     * @param useChunkEncoding 是否采用chunked编码传输请求数据。
     * @return 请求结果{@link PutObjectResult}实例。
     */
    public PutObjectResult putObject(URL signedUrl, String filePath, Map<String, String> requestHeaders,
            boolean useChunkEncoding) throws OSSException, ClientException;

    /**
     * 使用URL签名方式上传指定输入流。
     * @param signedUrl PUT请求类型的URL签名。
     * @param requestContent 请求输入流。
     * @param contentLength 请求输入流的长度。
     * @param requestHeaders 请求头（包括HTTP标准请求头、用户自定义请求头）。
     * @return 请求结果{@link PutObjectResult}实例。
     */
    public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength,
            Map<String, String> requestHeaders) throws OSSException, ClientException;
    
    /**
     * 使用URL签名方式上传指定输入流。
     * @param signedUrl PUT请求类型的URL签名。
     * @param requestContent 请求输入流。
     * @param contentLength 请求输入流的长度，如果采用chunked编码则设置为-1。
     * @param requestHeaders 请求头（包括HTTP标准请求头、用户自定义请求头）。
     * @param useChunkEncoding 是否采用chunked编码传输请求数据。
     * @return 请求结果{@link PutObjectResult}实例。
     */
    public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength,
            Map<String, String> requestHeaders, boolean useChunkEncoding) throws OSSException, ClientException;

    /**
     * 拷贝一个在OSS上已经存在的Object成另外一个Object。
     * @param sourceBucketName
     *          源Object所在的Bucket的名称。
     * @param sourceKey
     *          源Object的Key。
     * @param destinationBucketName
     *          目标Object所在的Bucket的名称。
     * @param destinationKey
     *          目标Object的Key。
     * @return 请求结果{@link CopyObjectResult}实例。
     * @throws OSSException
     * @throws ClientException
     */
    public CopyObjectResult copyObject(String sourceBucketName,
            String sourceKey, String destinationBucketName,String destinationKey) 
                    throws OSSException, ClientException;

    /**
     * 拷贝一个在OSS上已经存在的Object成另外一个Object。
     * @param copyObjectRequest
     *          请求参数{@link CopyObjectRequest}实例。
     * @return
     * @throws OSSException
     * @throws ClientException
     */
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest)
            throws OSSException, ClientException;

    /**
     * 从OSS指定的{@link Bucket}中导出{@link OSSObject}。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object Key。
     * @return 请求结果{@link OSSObject}实例。使用完之后需要手动关闭其中的ObjectContent释放请求连接。
     */
    public OSSObject getObject(String bucketName, String key)
            throws OSSException, ClientException;

    /**
     * 从OSS指定的{@link Bucket}中导出指定的{@link OSSObject}到目标文件。
     * @param getObjectRequest
     *          请求参数{@link GetObjectRequest}。
     * @param file
     *          目标文件。
     */
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File file)
            throws OSSException, ClientException;

    /**
     * 从OSS指定的{@link Bucket}中导出{@link OSSObject}。
     * @param getObjectRequest
     *          请求参数{@link GetObjectRequest}。
     * @return 请求结果{@link OSSObject}实例。使用完之后需要手动关闭其中的ObjectContent释放请求连接。
     */
    public OSSObject getObject(GetObjectRequest getObjectRequest)
            throws OSSException, ClientException;

    /**
     * 使用URL签名方式导出{@link OSSObject}。
     * @param signedUrl GET请求类型的URL签名。
     * @param requestHeaders 请求头（包括HTTP标准请求头、用户自定义请求头）。
     * @return 请求结果{@link OSSObject}实例。使用完之后需要手动关闭其中的ObjectContent释放请求连接。
     */
    public OSSObject getObject(URL signedUrl, Map<String, String> requestHeaders)
            throws OSSException, ClientException;
    
    /**
     * 返回{@link OSSObject}的元数据。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object key。
     */
    public ObjectMetadata getObjectMetadata(String bucketName, String key)
            throws OSSException, ClientException;
    
    /**
     * 返回{@link OSSObject}的元数据。
     * @param genericRequest
     *          请求信息。
     */
    public ObjectMetadata getObjectMetadata(GenericRequest genericRequest)
            throws OSSException, ClientException;
    
    /**
     * 以追加写的方式上传文件或输入流。
     * @param appendObjectRequest
     *             请求参数{@link AppendObjectRequest}实例。
     * @return
     *             追加写的结果。
     */
    public AppendObjectResult appendObject(AppendObjectRequest appendObjectRequest)
            throws OSSException, ClientException;

    /**
     * 删除指定的{@link OSSObject}。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object key。
     */
    public void deleteObject(String bucketName, String key)
            throws OSSException, ClientException;
    
    /**
     * 删除指定的{@link OSSObject}。
     * @param genericRequest
     *          请求信息。
     */
    public void deleteObject(GenericRequest genericRequest)
            throws OSSException, ClientException;

    /**
     * 批量删除指定Bucket下的{@link OSSObject}。 
     * @param deleteObjectsRequest 
     *             请求参数{@link DeleteObjectsRequest}实例。
     * @return 批量删除结果。
     */
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest)
            throws OSSException, ClientException;
    
    /**
     * 判断指定{@link Bucket}下是否存在指定的{@link OSSObject}。
     * @param bucketName 
     *             Bucket名称。
     * @param key
     *             Object Key。 
     * @return 
     *             如果存在返回True，不存在则返回False。
     */
    public boolean doesObjectExist(String bucketName, String key)
            throws OSSException, ClientException;
    
    /**
     * 判断指定的{@link OSSObject}是否存在。
     * @param headObjectRequest 
     *             请求参数{@link HeadObjectRequest}实例。
     * @return 
     *             如果存在返回True，不存在则返回False。
     */
    public boolean doesObjectExist(HeadObjectRequest headObjectRequest)
            throws OSSException, ClientException;
    
    /**
     * 设置指定{@link OSSObject}的Access Control List(ACL)。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *             Object Key。
     * @param cannedAcl
     *          Private/PublicRead/PublicReadWrite中的一种。
     */
    public void setObjectAcl(String bucketName, String key, CannedAccessControlList cannedAcl)
            throws OSSException, ClientException;
    
    /**
     * 设置指定{@link OSSObject}的Access Control List(ACL)。
     * @param setObjectAclRequest
     *          请求信息。
     */
    public void setObjectAcl(SetObjectAclRequest setObjectAclRequest)
            throws OSSException, ClientException;
    
    /**
     * 返回指定{@link OSSObject}的Access Control List(ACL)。
     * @param bucketName
     *             Bucket名称。
     * @param key
     *             Object Key。
     * @return 指定{@link OSSObject}的Access Control List(ACL)。
     */
    public ObjectAcl getObjectAcl(String bucketName, String key)
            throws OSSException, ClientException;
    
    /**
     * 返回指定{@link OSSObject}的Access Control List(ACL)。
     * @param bucketName
     *             Bucket名称。
     * @param genericRequest
     *             请求信息。
     */
    public ObjectAcl getObjectAcl(GenericRequest genericRequest)
            throws OSSException, ClientException;
    
    /**
     * 生成一个用HTTP GET方法访问{@link OSSObject}的URL。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object key。
     * @param expiration
     *          URL的超时时间。
     * @return
     *          访问{@link OSSObject}的URL。
     * @throws ClientException 
     */
    public URL generatePresignedUrl(String bucketName, String key,
            Date expiration) throws ClientException;

    /**
     * 生成一个用指定HTTP方法访问{@link OSSObject}的URL。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object Key。
     * @param expiration
     *          URL的超时时间。
     * @param method
     *          HTTP方法，只支持{@link HttpMethod#GET}和{@link HttpMethod#PUT}。
     * @return
     *          访问{@link OSSObject}的URL。
     * @throws ClientException 
     */
    public URL generatePresignedUrl(String bucketName, String key,
            Date expiration, HttpMethod method) throws ClientException;

    /**
     * 生成一个包含签名信息并可以访问{@link OSSObject}的URL。
     * @param request
     *          {@link GeneratePresignedUrlRequest}对象。
     * @return 包含签名信息并可以访问{@link OSSObject}的URL。
     * @throws ClientException
     */
    public URL generatePresignedUrl(GeneratePresignedUrlRequest request)
            throws ClientException;

    /**
     * 初始化一个Multipart上传事件。
     * <p>
     * 使用Multipart模式上传数据前，必须先调用该接口来通过OSS初始化一个Multipart上传事件。
     * 该接口会返回一个OSS服务器创建的全局唯一的Upload ID，用于标识本次Multipart上传事件。
     * 用户可以根据这个ID来发起相关的操作，如中止、查询Multipart上传等。
     * </p>
     * 
     * <p>
     * 此方法对应的操作为非幂等操作，SDK不会对其进行重试（即使设置最大重试次数大于0也不会重试）
     * </p>
     * @param request
     *          {@link InitiateMultipartUploadRequest}对象。
     * @return  InitiateMultipartUploadResult    
     * @throws ClientException
     */
    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request) 
            throws OSSException, ClientException;

    /**
     * 列出所有执行中的 Multipart上传事件。
     * <p>
     * 即已经被初始化的 Multipart Upload 但是未被完成或被终止的 Multipart上传事件。 
     * OSS返回的罗列结果中最多会包含1000个Multipart上传事件。
     * </p>
     * @param request
     *          {@link ListMultipartUploadsRequest}对象。
     * @return  MultipartUploadListing
     *          Multipart上传事件的列表{@link MultipartUploadListing}。
     * @throws ClientException
     */
    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request) 
            throws OSSException, ClientException;

    /**
     * 列出multipart中上传的所有part信息
     * @param request
     *          {@link ListPartsRequest}对象。
     * @return  PartListing    
     * @throws ClientException
     */
    public PartListing listParts(ListPartsRequest request) 
            throws OSSException, ClientException;

    /**
     * 上传一个分块（Part）到指定的的Multipart上传事件中。
     * @param request
     *          {@link UploadPartRequest}对象。
     * @return  UploadPartResult 上传Part的返回结果{@link UploadPartResult}。
     * @throws ClientException
     */
    public UploadPartResult uploadPart(UploadPartRequest request)
            throws OSSException, ClientException;
    
    /**
     * 分片拷贝。
     * @param request 分片拷贝请求参数。
     * @return 分片拷贝结果。
     * @throws OSSException
     * @throws ClientException
     */
    public UploadPartCopyResult uploadPartCopy(UploadPartCopyRequest request)
            throws OSSException, ClientException;
    
    /**
     * 终止一个Multipart上传事件。
     * @param request
     *          {@link AbortMultipartUploadRequest}对象。
     * @throws ClientException
     */
    public void abortMultipartUpload(AbortMultipartUploadRequest request)
            throws OSSException, ClientException;

    /**
     * 完成一个Multipart上传事件。
     * <p>
     * 在将所有数据Part 都上传完成后，可以调用 Complete Multipart Upload API
     * 来完成整个文件的 Multipart Upload。在执行该操作时，用户必须提供所有有效
     * 的数据Part的列表（包括part号码和ETAG）； OSS收到用户提交的Part列表后，
     * 会逐一验证每个数据 Part 的有效性。当所有的数据 Part 验证通过后，OSS 将把
     * 这些数据part组合成一个完整的 Object。 
     * </p>
     * 
     * <p>
     * 此方法对应的操作为非幂等操作，SDK不会对其进行重试（即使设置最大重试次数大于0也不会重试）
     * </p>
     * 
     * @param request
     *          {@link CompleteMultipartUploadRequest}对象。
     * @return  CompleteMultipartUploadResult    
     * @throws ClientException
     */
    public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request) 
            throws OSSException, ClientException;
    
    /**
     * 操作将在指定的bucket上设定一个跨域资源共享(CORS)的规则，如果原规则存在则覆盖原规则
     * @param request
     *                     {@link SetBucketCORSRequest}}
     * @throws OSSException
     * @throws ClientException
     */
    public void setBucketCORS(SetBucketCORSRequest request) 
            throws OSSException, ClientException;
    
    /**
     * 列出指定bucket的跨域访问规则
     * @param bucketName
     * @return
     * @throws OSSException
     * @throws ClientException
     */
    public List<CORSRule> getBucketCORSRules(String bucketName)
            throws OSSException, ClientException;
    
    /**
     * 列出指定bucket的跨域访问规则
     * @param genericRequest
     *          请求信息。
     * @return
     * @throws OSSException
     * @throws ClientException
     */
    public List<CORSRule> getBucketCORSRules(GenericRequest genericRequest)
            throws OSSException, ClientException;
    
    /**
     * 删除指定bucket下面的所有跨域访问规则
     * @param bucketName
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketCORSRules(String bucketName)
            throws OSSException, ClientException;
    
    /**
     * 删除指定bucket下面的所有跨域访问规则
     * @param genericRequest
     *          请求信息。
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketCORSRules(GenericRequest genericRequest)
            throws OSSException, ClientException;
    
    /**
     * 该接口已过时。
     */
    @Deprecated
    public ResponseMessage optionsObject(OptionsRequest request)
           throws OSSException, ClientException;
    
    /**
     * 设置{@link Bucket}的访问日志记录功能。
     * 这个功能开启后，OSS将自动记录访问这个{@link Bucket}请求的详细信息，并按照用户指定的规则，
     * 以小时为单位，将访问日志作为一个Object写入用户指定的{@link Bucket}。
     * @param request {@link PutBucketLoggingRequest}对象。
     * @return  BucketLoggingResult {@link UploadPartResult}。
     */
    public void setBucketLogging(SetBucketLoggingRequest request)
            throws OSSException, ClientException;
    
    /**
     * 查看{@link Bucket}的访问日志配置。
     * @param bucketName
     * @return
     * @throws OSSException
     * @throws ClientException
     */
    public BucketLoggingResult getBucketLogging(String bucketName)
            throws OSSException, ClientException;
    
    /**
     * 查看{@link Bucket}的访问日志配置。
     * @param genericRequest
     *          请求信息。
     * @return
     * @throws OSSException
     * @throws ClientException
     */
    public BucketLoggingResult getBucketLogging(GenericRequest genericRequest)
            throws OSSException, ClientException;
    
    /**
     * 关闭{@link Bucket}的访问日志记录功能。
     * @param bucketName
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketLogging(String bucketName) 
            throws OSSException, ClientException;
    
    /**
     * 关闭{@link Bucket}的访问日志记录功能。
     * @param genericRequest
     *          请求信息。
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketLogging(GenericRequest genericRequest) 
            throws OSSException, ClientException;
    
    /**
     * 将一个{@link Bucket}设置成静态网站托管模式。
     * @param setBucketWebSiteRequest
     * @throws OSSException
     * @throws ClientException
     */
    public void setBucketWebsite(SetBucketWebsiteRequest setBucketWebSiteRequest)
            throws OSSException, ClientException;
    
    /**
     * 获取{@link Bucket}的静态网站托管状态。
     * @param bucketName
     * @return
     * @throws OSSException
     * @throws ClientException
     */
    public BucketWebsiteResult getBucketWebsite(String bucketName)
            throws OSSException, ClientException;
    
    /**
     * 获取{@link Bucket}的静态网站托管状态。
     * @param genericRequest
     *          请求信息。
     * @return
     * @throws OSSException
     * @throws ClientException
     */
    public BucketWebsiteResult getBucketWebsite(GenericRequest genericRequest)
            throws OSSException, ClientException;
    
    /**
     * 关闭{@link Bucket}的静态网站托管模式。
     * @param bucketName
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketWebsite(String bucketName)
            throws OSSException, ClientException;
    
    /**
     * 关闭{@link Bucket}的静态网站托管模式。
     * @param genericRequest
     *          请求信息。
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketWebsite(GenericRequest genericRequest)
            throws OSSException, ClientException;
    
    /**
     * 生成Post请求的policy表单域。
     * @param expiration policy过期时间。
     * @param conds policy条件列表。
     * @return policy字符串。
     */
    public String generatePostPolicy(Date expiration, PolicyConditions conds) 
            throws ClientException;
    
    /**
     * 根据Access Key Secret和policy计算签名，OSS依据该签名验证Post请求的合法性。
     * @param postPolicy 由{@link #generatePostPolicy(Date, PolicyConditions)}生成的policy字符串。
     * @return post签名。
     */
    public String calculatePostSignature(String postPolicy);
    
    /**
     * 设置{@link Bucket}的Lifecycle规则。
     * @param setBucketLifecycleRequest 请求参数。
     * @throws OSSException OSS Server异常信息。
     * @throws ClientException OSS Client异常信息。
     */
    public void setBucketLifecycle(SetBucketLifecycleRequest setBucketLifecycleRequest)
            throws OSSException, ClientException;
    
    /**
     * 获取{@link Bucket}的Lifecycle规则列表。
     * @param bucketName 指定Bucket名称。
     * @return Lifecycle规则列表。
     * @throws OSSException OSS Server异常信息。
     * @throws ClientException OSS Client异常信息。
     */
    public List<LifecycleRule> getBucketLifecycle(String bucketName)
            throws OSSException, ClientException;
    
    /**
     * 获取{@link Bucket}的Lifecycle规则列表。
     * @param genericRequest
     *          请求信息。
     * @return Lifecycle规则列表。
     * @throws OSSException OSS Server异常信息。
     * @throws ClientException OSS Client异常信息。
     */
    public List<LifecycleRule> getBucketLifecycle(GenericRequest genericRequest)
            throws OSSException, ClientException;
    
    /**
     * 关闭{@link Bucket}的Lifecycle规则。
     * @param bucketName 指定Bucket名称。
     * @throws OSSException OSS Server异常信息。
     * @throws ClientException OSS Client异常信息。
     */
    public void deleteBucketLifecycle(String bucketName)
            throws OSSException, ClientException;
    
    /**
     * 关闭{@link Bucket}的Lifecycle规则。
     * @param genericRequest
     *          请求信息。
     * @throws OSSException OSS Server异常信息。
     * @throws ClientException OSS Client异常信息。
     */
    public void deleteBucketLifecycle(GenericRequest genericRequest)
            throws OSSException, ClientException;
}
