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
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.*;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;

/**
 * Entry point interface of Alibaba Cloud's OSS (Object Store Service)
 * <p>
 * Object Store Service (a.k.a OSS) is the massive, secure, low cost and highly
 * reliable public storage which could be accessed from anywhere at anytime via
 * REST APIs, SDKs or web console. <br>
 * Developers could use OSS to create any services that need huge data storage
 * and access throughput, such as media sharing web apps, cloud storage service
 * or enterprise or personal data backup.
 * </p>
 */
public interface OSS {

    /**
     * Switches to another users with specified credentials
     * 
     * @param creds
     *            the credential to switch to。
     */
    public void switchCredentials(Credentials creds);

    /**
     * Switches to another signature version
     *
     * @param signatureVersion
     *            the signature version to switch to。
     */
    public void switchSignatureVersion(SignVersion signatureVersion);

    /**
     * Shuts down the OSS instance (release all resources) The OSS instance is
     * not usable after its shutdown() is called.
     */
    public void shutdown();

    /**
     * Creates {@link Bucket} instance. The bucket name specified must be
     * globally unique and follow the naming rules from
     * https://www.alibabacloud.com/help/doc-detail/31827.htm?spm=a3c0i.o32012en
     * .a3.1.64ece5e0jPpa2t.
     * 
     * @param bucketName
     *            bucket name
     */
    public Bucket createBucket(String bucketName) throws OSSException, ClientException;

    /**
     * Creates a {@link Bucket} instance with specified CreateBucketRequest
     * information.
     * 
     * @param createBucketRequest
     *            instance of {@link CreateBucketRequest}, which at least has
     *            bucket name information.
     */
    public Bucket createBucket(CreateBucketRequest createBucketRequest) throws OSSException, ClientException;

    /**
     * Deletes the {@link Bucket} instance. A non-empty bucket could not be
     * deleted.
     * 
     * @param bucketName
     *            bucket name to delete.
     */
    public void deleteBucket(String bucketName) throws OSSException, ClientException;

    /**
     * Deletes the {@link Bucket} instance.
     * 
     * @param genericRequest
     *            the generic request instance that has the bucket name
     *            information.
     */
    public void deleteBucket(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Returns all {@link Bucket} instances of the current account.
     * 
     * @return A list of {@link Bucket} instances. If there's no buckets, the
     *         list will be empty (instead of null).
     */
    public List<Bucket> listBuckets() throws OSSException, ClientException;

    /**
     * Returns all {@link Bucket} instances of the current account that meet the
     * conditions specified.
     * 
     * @param prefix
     *            The prefix of the bucket name returned. If null, the bucket
     *            name could have any prefix.
     * @param marker
     *            The start point in the lexicographic order for the buckets to
     *            return. If null, return the buckets from the beginning in the
     *            lexicographic order. For example, if the account has buckets
     *            bk1, bk2, bk3. If the marker is set as bk2, then only bk2 and
     *            bk3 meet the criteria. But if the marker is null, then all
     *            three buckets meet the criteria.
     * @param maxKeys
     *            Max bucket count to return. The valid value is from 1 to 1000,
     *            default is 100 if it's null.
     * @return The list of {@link Bucket} instances.
     */
    public BucketList listBuckets(String prefix, String marker, Integer maxKeys) throws OSSException, ClientException;

    /**
     * Returns all {@link Bucket} instances of the current account that meet the
     * conditions specified.
     * 
     * @param listBucketsRequest
     *            the ListBucketsRequest instance that defines the criteria
     *            which could have requirements on prefix, marker, maxKeys.
     * @return The list of {@link Bucket} instances.
     */
    public BucketList listBuckets(ListBucketsRequest listBucketsRequest) throws OSSException, ClientException;

    /**
     * Applies the Access Control List(ACL) on the {@link Bucket}.
     * 
     * @param bucketName
     *            Bucket name.
     * @param acl
     *            {@link CannedAccessControlList} instance. If the instance is
     *            null, no ACL change on the bucket (but the request is still
     *            sent).
     */
    public void setBucketAcl(String bucketName, CannedAccessControlList acl) throws OSSException, ClientException;

    /**
     * Sends the request to apply ACL on a {@link Bucket} instance.
     * 
     * @param setBucketAclRequest
     *            SetBucketAclRequest instance which specifies the ACL and the
     *            bucket information.
     */
    public void setBucketAcl(SetBucketAclRequest setBucketAclRequest) throws OSSException, ClientException;

    /**
     * Returns the Access control List (ACL) of the {@link Bucket} instance.
     * 
     * @param bucketName
     *            Bucket Name.
     * @return Access Control List(ACL) {@link AccessControlList}.
     */
    public AccessControlList getBucketAcl(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the Access Control List(ACL) of the {@link Bucket} instance.
     * 
     * @param genericRequest
     *            {@link GenericRequest} instance that has the bucket name
     *            information.
     * @return {@link AccessControlList} instance.
     */
    public AccessControlList getBucketAcl(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Gets the metadata of {@link Bucket}.
     * 
     * @param bucketName
     *            Bucket name.
     *
     * @return The {@link BucketMetadata} instance.
     */
    public BucketMetadata getBucketMetadata(String bucketName) throws OSSException, ClientException;

    /**
     * Gets all the metadata of {@link Bucket}.
     * 
     * @param genericRequest
     *            Generic request which specifies the bucket name.
     *
     * @return The {@link BucketMetadata} instance.
     *
     */
    public BucketMetadata getBucketMetadata(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Sets the http referer on the {@link Bucket} instance specified by the
     * bucket name.
     * 
     * @param bucketName
     *            Bucket name.
     * @param referer
     *            The {@link BucketReferer} instance. If null, it would create a
     *            {@link BucketReferer} instance from default constructor.
     */
    public void setBucketReferer(String bucketName, BucketReferer referer) throws OSSException, ClientException;

    /**
     * Sets the http referer on the {@link Bucket} instance in the parameter
     * setBucketRefererRequest.
     * 
     * @param setBucketRefererRequest
     *            {@link SetBucketRefererRequest} instance that specify the
     *            bucket name and the {@link BucketReferer} instance.
     */
    public void setBucketReferer(SetBucketRefererRequest setBucketRefererRequest) throws OSSException, ClientException;

    /**
     * Returns http referer information of the {@link Bucket} specified by
     * bucket name.
     * 
     * @param bucketName
     *            Bucket name
     * @return {@link BucketReferer} instance. The BucketReferer object with
     *         empty referer information is returned if there's no http referer
     *         information.
     */
    public BucketReferer getBucketReferer(String bucketName) throws OSSException, ClientException;

    /**
     * Returns http referer information of the {@link Bucket} specified by
     * bucket name in GenericRequest object.
     * 
     * @param genericRequest
     *            {@link GenericRequest} instance that has the bucket name.
     * @return bucket http referer {@link BucketReferer}。
     */
    public BucketReferer getBucketReferer(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Returns the datacenter name where the {@link Bucket} instance is hosted.
     * As of 08/03/2017, the valid datacenter names are oss-cn-hangzhou,
     * oss-cn-qingdao, oss-cn-beijing, oss-cn-hongkong, oss-cn-shenzhen,
     * oss-cn-shanghai, oss-us-west-1, oss-us-east-1, and oss-ap-southeast-1.
     * 
     * @param bucketName
     *            Bucket name.
     * @return The datacenter name in string.
     */
    public String getBucketLocation(String bucketName) throws OSSException, ClientException;

    /**
     * Returns the datacenter name where the {@link Bucket} instance specified
     * by GenericRequest is hosted.
     * 
     * @param genericRequest
     *            {@link GenericRequest} instance with bucket name information.
     * @return The datacenter name in string.
     */
    public String getBucketLocation(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Sets the tags on the {@link Bucket} instance specified by the bucket name
     * 
     * @param bucketName
     *            Bucket name.
     * @param tags
     *            The dictionary that contains the tags in the form of &lt;key,
     *            value&gt; pairs
     */
    public void setBucketTagging(String bucketName, Map<String, String> tags) throws OSSException, ClientException;

    /**
     * Sets the tags on the {@link Bucket} instance.
     * 
     * @param bucketName
     *            Bucket name.
     * @param tagSet
     *            {@link TagSet} instance that has the tags in the form of &lt;key,
     *            value&gt; paris.
     */
    public void setBucketTagging(String bucketName, TagSet tagSet) throws OSSException, ClientException;

    /**
     * Sets the tags on the {@link Bucket} instance in
     * {@link SetBucketTaggingRequest} object.
     * 
     * @param setBucketTaggingRequest
     *            {@link SetBucketTaggingRequest} instance that has bucket
     *            information as well as tagging information.
     */
    public void setBucketTagging(SetBucketTaggingRequest setBucketTaggingRequest) throws OSSException, ClientException;

    /**
     * Gets all tags of the {@link Bucket} instance.
     * 
     * @param bucketName
     *            Bucket name
     * @return A {@link TagSet} instance. If there's no tag, the TagSet object
     *         with empty tag information is returned.
     */
    public TagSet getBucketTagging(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the tags of {@link Bucket} instance.
     * 
     * @param genericRequest
     *            {@link GenericRequest} instance that has the bucket name.
     * @return A {@link TagSet} instance.
     */
    public TagSet getBucketTagging(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Clears all the tags of the {@link Bucket} instance。
     * 
     * @param bucketName
     *            Bucket name
     */
    public void deleteBucketTagging(String bucketName) throws OSSException, ClientException;

    /**
     * Clears all the tags of the {@link Bucket} instance.
     * 
     * @param genericRequest
     *            {@link GenericRequest} instance that has the bucket name
     */
    public void deleteBucketTagging(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * <p>
     * Returns the versioning configuration for the specified bucket.
     * </p>
     * <p>
     * A bucket's versioning configuration can be in one of three possible
     * states:
     *  <ul>
     *      <li>{@link BucketVersioningConfiguration#OFF}
     *      <li>{@link BucketVersioningConfiguration#ENABLED}
     *      <li>{@link BucketVersioningConfiguration#SUSPENDED}
     *  </ul>
     * </p>
     * <p>
     * By default, new buckets are in the
     * {@link BucketVersioningConfiguration#OFF off} state. Once versioning is
     * enabled for a bucket the status can never be reverted to
     * {@link BucketVersioningConfiguration#OFF off}.
     * </p>
     * <p>
     * The versioning configuration of a bucket has different implications for
     * each operation performed on that bucket or for objects within that
     * bucket. For example, when versioning is enabled a <code>PutObject</code>
     * operation creates a unique object version-id for the object being uploaded. The
     * The <code>PutObject</code> API guarantees that, if versioning is enabled for a bucket at
     * the time of the request, the new object can only be permanently deleted
     * using a <code>DeleteVersion</code> operation. It can never be overwritten.
     * Additionally, the <code>PutObject</code> API guarantees that,
     * if versioning is enabled for a bucket the request,
     * no other object will be overwritten by that request.
     * </p>
     * <p>
     * OSS is eventually consistent. It can take time for the versioning status
     * of a bucket to be propagated throughout the system.
     * </p>
     *
     * @param bucketName
     *            The bucket whose versioning configuration will be retrieved.
     *
     * @return The bucket versioning configuration for the specified bucket.
     *
     * @throws ClientException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     * @throws OSSException
     *             If any errors occurred in OSS while processing the request.
     *
     * @see OSS#setBucketVersioning(SetBucketVersioningRequest)
     * @see OSS#getBucketVersioning(GenericRequest)
     */
    public BucketVersioningConfiguration getBucketVersioning(String bucketName) throws OSSException, ClientException;
    
    /**
     * <p>
     * Returns the versioning configuration for the specified bucket.
     * </p>
     * <p>
     * A bucket's versioning configuration can be in one of three possible
     * states:
     *  <ul>
     *      <li>{@link BucketVersioningConfiguration#OFF}
     *      <li>{@link BucketVersioningConfiguration#ENABLED}
     *      <li>{@link BucketVersioningConfiguration#SUSPENDED}
     *  </ul>
     * </p>
     * <p>
     * By default, new buckets are in the
     * {@link BucketVersioningConfiguration#OFF off} state. Once versioning is
     * enabled for a bucket the status can never be reverted to
     * {@link BucketVersioningConfiguration#OFF off}.
     * </p>
     * <p>
     * The versioning configuration of a bucket has different implications for
     * each operation performed on that bucket or for objects within that
     * bucket. For example, when versioning is enabled a <code>PutObject</code>
     * operation creates a unique object version-id for the object being uploaded. The
     * The <code>PutObject</code> API guarantees that, if versioning is enabled for a bucket at
     * the time of the request, the new object can only be permanently deleted
     * using a <code>DeleteVersion</code> operation. It can never be overwritten.
     * Additionally, the <code>PutObject</code> API guarantees that,
     * if versioning is enabled for a bucket the request,
     * no other object will be overwritten by that request.
     * </p>
     * <p>
     * OSS is eventually consistent. It can take time for the versioning status
     * of a bucket to be propagated throughout the system.
     * </p>
     *
     * @param genericRequest
     *            {@link GenericRequest} instance that has the bucket name.
     *
     * @return The bucket versioning configuration for the specified bucket.
     *
     * @throws ClientException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     * @throws OSSException
     *             If any errors occurred in OSS while processing the request.
     *
     * @see OSS#setBucketVersioning(SetBucketVersioningRequest)
     * @see OSS#getBucketVersioning(String)
     */
    public BucketVersioningConfiguration getBucketVersioning(GenericRequest genericRequest)
            throws OSSException, ClientException;

    /**
     * <p>
     * Sets the versioning configuration for the specified bucket.
     * </p>
     * <p>
     * A bucket's versioning configuration can be in one of three possible
     * states:
     *  <ul>
     *      <li>{@link BucketVersioningConfiguration#OFF}
     *      <li>{@link BucketVersioningConfiguration#ENABLED}
     *      <li>{@link BucketVersioningConfiguration#SUSPENDED}
     *  </ul>
     * </p>
     * <p>
     * By default, new buckets are in the
     * {@link BucketVersioningConfiguration#OFF off} state. Once versioning is
     * enabled for a bucket the status can never be reverted to
     * {@link BucketVersioningConfiguration#OFF off}.
     * </p>
     * <p>
     * Objects created before versioning was enabled or when versioning is
     * suspended will be given the default <code>null</code> version ID (see
     * {@link com.aliyun.oss.internal.OSSConstants#NULL_VERSION_ID}). Note that the
     * <code>null</code> version ID is a valid version ID and is not the
     * same as not having a version ID.
     * </p>
     * <p>
     * The versioning configuration of a bucket has different implications for
     * each operation performed on that bucket or for objects within that
     * bucket. For example, when versioning is enabled a <code>PutObject</code>
     * operation creates a unique object version-id for the object being uploaded. The
     * The <code>PutObject</code> API guarantees that, if versioning is enabled for a bucket at
     * the time of the request, the new object can only be permanently deleted
     * using a <code>DeleteVersion</code> operation. It can never be overwritten.
     * Additionally, the <code>PutObject</code> API guarantees that,
     * if versioning is enabled for a bucket the request,
     * no other object will be overwritten by that request.
     * Refer to the documentation sections for each API for information on how
     * versioning status affects the semantics of that particular API.
     * </p>
     * <p>
     * OSS is eventually consistent. It can take time for the versioning status
     * of a bucket to be propagated throughout the system.
     * </p>
     *
     * @param setBucketVersioningRequest
     *            The request object containing all options for setting the
     *            bucket versioning configuration.
     *
     * @throws ClientException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     * @throws OSSException
     *             If any errors occurred in OSS while processing the request.
     *
     * @see OSS#getBucketVersioning(String)
     */
    public void setBucketVersioning(SetBucketVersioningRequest setBucketVersioningRequest)
        throws OSSException, ClientException;
    
    /**
     * Checks the {@link Bucket} exists .
     * 
     * @param bucketName
     *            Bucket name.
     * @return Returns true if the bucket exists and false if not.
     */
    public boolean doesBucketExist(String bucketName) throws OSSException, ClientException;

    /**
     * Checks if the {@link Bucket} exists。
     * 
     * @param genericRequest
     *            {@link GenericRequest} instance that has the bucket name.
     * @return Returns true if the bucket exists and false if not.
     */
    public boolean doesBucketExist(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Lists all objects under the specified {@link Bucket}
     * 
     * @param bucketName
     *            Bucket name
     * @return {@link ObjectListing} instance that has all objects.
     */
    public ObjectListing listObjects(String bucketName) throws OSSException, ClientException;

    /**
     * Lists all objects under the specified {@link Bucket} with the specified
     * prefix.
     * 
     * @param bucketName
     *            Bucket name.
     * @param prefix
     *            The prefix returned object must have.
     * @return A {@link ObjectListing} instance that has all objects
     * @throws OSSException
     * @throws ClientException
     */
    public ObjectListing listObjects(String bucketName, String prefix) throws OSSException, ClientException;

    /**
     * Lists all objects under the specified {@link Bucket} in the parameter of
     * {@link ListObjectsRequest}
     * 
     * @param listObjectsRequest
     *            The {@link ListObjectsRequest} instance that defines the
     *            bucket name as well as the criteria such as prefix, marker,
     *            maxKeys, delimiter, etc.
     * @return A {@link ObjectListing} instance that has the objects meet the
     *         criteria
     * @throws OSSException
     * @throws ClientException
     */
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws OSSException, ClientException;
    
    /**
     * <p>
     * Returns a list of summary information about the versions in the specified
     * bucket.
     * </p>
     * <p>
     * The returned version summaries are ordered first by key and then by
     * version. Keys are sorted lexicographically (alphabetically)
     * while versions are sorted from most recent to least recent.
     * Both versions with data and delete markers are included in the results.
     * </p>
     * <p>
     * Because buckets can contain a virtually unlimited number of versions, the
     * complete results of a list query can be extremely large. To manage large
     * result sets, OSS uses pagination to split them into multiple
     * responses. Always check the
     * {@link VersionListing#isTruncated()} method to determine if the
     * returned listing is complete or if additional calls are needed to get
     * more results. 
     * </p>
     * <p>
     * For more information about enabling versioning for a bucket, see
     * {@link #setBucketVersioning(SetBucketVersioningRequest)}.
     * </p>
     *
     * @param bucketName
     *            The name of the OSS bucket whose versions are to be
     *            listed.
     * @param prefix
     *            An optional parameter restricting the response to keys
     *            beginning with the specified prefix. Use prefixes to
     *            separate a bucket into different sets of keys,
     *            similar to how a file system organizes files
     * 		      into directories.
     *
     * @return A listing of the versions in the specified bucket, along with any
     *         other associated information and original request parameters.
     *
     * @throws ClientException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     * @throws OSSException
     *             If any errors occurred in OSS while processing the
     *             request.
     *
     * @see OSSClient#listVersions(ListVersionsRequest)
     * @see OSSClient#listVersions(String, String, String, String, String, Integer)
     */
    public VersionListing listVersions(String bucketName, String prefix)
            throws OSSException, ClientException;
    
    /**
     * <p>
     * Returns a list of summary information about the versions in the specified
     * bucket.
     * </p>
     * <p>
     * The returned version summaries are ordered first by key and then by
     * version. Keys are sorted lexicographically (alphabetically)
     * and versions are sorted from most recent to least recent.
     * Versions
     * with data and delete markers are included in the results.
     * </p>
     * <p>
     * Because buckets can contain a virtually unlimited number of versions, the
     * complete results of a list query can be extremely large. To manage large
     * result sets, OSS uses pagination to split them into multiple
     * responses. Always check the
     * {@link VersionListing#isTruncated()} method to determine if the
     * returned listing is complete or if additional calls are needed
     * to get more results.
     * </p>
     * <p>
     * The <code>keyMarker</code> and <code>versionIdMarker</code> parameters allow
     * callers to specify where to start the version listing.
     * </p>
     * <p>
     * The <code>delimiter</code> parameter allows groups of keys that share a
     * delimiter-terminated prefix to be included
     * in the returned listing. This allows applications to organize and browse
     * their keys hierarchically, much like how a file system organizes
     * files into directories. These common prefixes can be retrieved
     * by calling the {@link VersionListing#getCommonPrefixes()} method.
     * </p>
     * <p>
     * For example, consider a bucket that contains the following keys:
     * <ul>
     * 	<li>"foo/bar/baz"</li>
     * 	<li>"foo/bar/bash"</li>
     * 	<li>"foo/bar/bang"</li>
     * 	<li>"foo/boo"</li>
     * </ul>
     * If calling <code>listVersions</code> with
     * a <code>prefix</code> value of "foo/" and a <code>delimiter</code> value of "/"
     * on this bucket, a <code>VersionListing</code> is returned that contains:
     * 	<ul>
     * 		<li>all the versions for one key ("foo/boo")</li>
     * 		<li>one entry in the common prefixes list ("foo/bar/")</li>
     * 	</ul>
     * </p>
     * <p>
     * To see deeper into the virtual hierarchy, make
     * another call to <code>listVersions</code> setting the prefix parameter to any
     * interesting common prefix to list the individual versions under that
     * prefix.
     * </p>
     * <p>
     * For more information about enabling versioning for a bucket, see
     * {@link #setBucketVersioning(SetBucketVersioningRequest)}.
     * </p>
     *
     * @param bucketName
     *            The name of the OSS bucket whose versions are to be listed.
     * @param prefix
     *            An optional parameter restricting the response to keys that
     *            begin with the specified prefix. Use prefixes to
     *            separate a bucket into different sets of keys,
     *            similar to how a file system organizes files
     * 		      into directories.
     * @param keyMarker
     *            Optional parameter indicating where in the sorted list of all
     *            versions in the specified bucket to begin returning results.
     *            Results are always ordered first lexicographically (i.e.
     *            alphabetically) and then from most recent version to least
     *            recent version. If a keyMarker is used without a
     *            versionIdMarker, results begin immediately after that key's
     *            last version. When a keyMarker is used with a versionIdMarker,
     *            results begin immediately after the version with the specified
     *            key and version ID.
     *            <p>
     *            This enables pagination; to get the next page of results use
     *            the next key marker and next version ID marker (from
     *            {@link VersionListing#getNextKeyMarker()} and
     *            {@link VersionListing#getNextVersionIdMarker()}) as the
     *            markers for the next request to list versions.
     * @param versionIdMarker
     *            Optional parameter indicating where in the sorted list of all
     *            versions in the specified bucket to begin returning results.
     *            Results are always ordered first lexicographically (i.e.
     *            alphabetically) and then from most recent version to least
     *            recent version. A keyMarker must be specified when specifying
     *            a versionIdMarker. Results begin immediately after the version
     *            with the specified key and version ID.
     *            <p>
     *            This enables pagination; to get the next page of results use
     *            the next key marker and next version ID marker (from
     *            {@link VersionListing#getNextKeyMarker()} and
     *            {@link VersionListing#getNextVersionIdMarker()}) as the
     *            markers for the next request to list versions.
     * @param delimiter
     *            Optional parameter that causes keys that contain the same
     *            string between the prefix and the first occurrence of the
     *            delimiter to be rolled up into a single result element in the
     *            {@link VersionListing#getCommonPrefixes()} list. These
     *            rolled-up keys are not returned elsewhere in the response. The
     *            most commonly used delimiter is "/", which simulates a
     *            hierarchical organization similar to a file system directory
     *            structure.
     * @param maxResults
     *            Optional parameter indicating the maximum number of results to
     *            include in the response. OSS might return fewer than
     *            this, but will not return more. Even if maxResults is not
     *            specified, OSS will limit the number of results in the
     *            response.
     *
     * @return A listing of the versions in the specified bucket, along with any
     *         other associated information such as common prefixes (if a
     *         delimiter was specified), the original request parameters, etc.
     *
     * @throws ClientException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     * @throws OSSException
     *             If any errors occurred in OSS while processing the
     *             request.
     *
     * @see OSSClient#listVersions(String, String)
     * @see OSSClient#listVersions(ListVersionsRequest)
     */
    public VersionListing listVersions(String bucketName, String prefix,
            String keyMarker, String versionIdMarker, String delimiter, Integer maxResults)
            throws OSSException, ClientException;
    
    /**
     * <p>
     * Returns a list of summary information about the versions in the specified
     * bucket.
     * </p>
     * <p>
     * The returned version summaries are ordered first by key and then by
     * version. Keys are sorted lexicographically (alphabetically)
     * and versions are sorted from most recent to least recent.
     * Versions
     * with data and delete markers are included in the results.
     * </p>
     * <p>
     * Because buckets can contain a virtually unlimited number of versions, the
     * complete results of a list query can be extremely large. To manage large
     * result sets, OSS uses pagination to split them into multiple
     * responses. Always check the
     * {@link VersionListing#isTruncated()} method to determine if the
     * returned listing is complete or if additional calls are needed
     * to get more results.
     * </p>
     * <p>
     * The <code>keyMarker</code> and <code>versionIdMarker</code> parameters allow
     * callers to specify where to start the version listing.
     * </p>
     * <p>
     * The <code>delimiter</code> parameter allows groups of keys that share a
     * delimiter-terminated prefix to be included
     * in the returned listing. This allows applications to organize and browse
     * their keys hierarchically, much like how a file system organizes
     * files into directories. These common prefixes can be retrieved
     * by calling the {@link VersionListing#getCommonPrefixes()} method.
     * </p>
     * <p>
     * For example, consider a bucket that contains the following keys:
     * <ul>
     *  <li>"foo/bar/baz"</li>
     *  <li>"foo/bar/bash"</li>
     *  <li>"foo/bar/bang"</li>
     *  <li>"foo/boo"</li>
     * </ul>
     * If calling <code>listVersions</code> with
     * a <code>prefix</code> value of "foo/" and a <code>delimiter</code> value of "/"
     * on this bucket, a <code>VersionListing</code> is returned that contains:
     *  <ul>
     *      <li>all the versions for one key ("foo/boo")</li>
     *      <li>one entry in the common prefixes list ("foo/bar/")</li>
     *  </ul>
     * </p>
     * <p>
     * To see deeper into the virtual hierarchy, make
     * another call to <code>listVersions</code> setting the prefix parameter to any
     * interesting common prefix to list the individual versions under that
     * prefix.
     * </p>
     * <p>
     * For more information about enabling versioning for a bucket, see
     * {@link #setBucketVersioning(SetBucketVersioningRequest)}.
     * </p>
     * 
     * @param listVersionsRequest
     *            The request object containing all options for listing the
     *            versions in a specified bucket.
     *
     * @return A listing of the versions in the specified bucket, along with any
     *         other associated information such as common prefixes (if a
     *         delimiter was specified), the original request parameters, etc.
     *
     * @throws ClientException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     * @throws OSSException
     *             If any errors occurred in OSS while processing the
     *             request.
     *
     * @see OSSClient#listVersions(String, String)
     * @see OSSClient#listVersions(String, String, String, String, String, Integer)
     */
    public VersionListing listVersions(ListVersionsRequest listVersionsRequest)
    		throws OSSException, ClientException;
    
    /**
     * Uploads the file to the {@link Bucket} from the {@link InputStream}
     * instance. It overwrites the existing one and the bucket must exist.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            object key.
     * @param input
     *            {@link InputStream} instance to write from. The must be
     *            readable.
     */
    public PutObjectResult putObject(String bucketName, String key, InputStream input)
            throws OSSException, ClientException;

    /**
     * Uploads the file to the {@link Bucket} from the @{link InputStream} with
     * the {@link ObjectMetadata} information。
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object key.
     * @param input
     *            {@link InputStream} instance to write from. It must be
     *            readable.
     * @param metadata
     *            The {@link ObjectMetadata} instance. If it does not specify
     *            the Content-Length information, the data is encoded by chunked
     *            tranfer encoding.
     */
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
            throws OSSException, ClientException;

    /**
     * Uploads the file to the {@link Bucket} from the file with the
     * {@link ObjectMetadata}.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object key.
     * @param file
     *            File object to read from.
     * @param metadata
     *            The {@link ObjectMetadata} instance. If it does not specify
     *            the Content-Length information, the data is encoded by chunked
     *            tranfer encoding.
     */
    public PutObjectResult putObject(String bucketName, String key, File file, ObjectMetadata metadata)
            throws OSSException, ClientException;

    /**
     * Uploads the file to the {@link Bucket} from the file.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object key.
     * @param file
     *            File object to read from.
     */
    public PutObjectResult putObject(String bucketName, String key, File file) throws OSSException, ClientException;

    /**
     * Uploads the file to {@link Bucket}.
     * 
     * @param putObjectRequest
     *            The {@link PutObjectRequest} instance that has bucket name,
     *            object key, metadata information.
     * @return A {@link PutObjectResult} instance.
     * @throws OSSException
     * @throws ClientException
     */
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws OSSException, ClientException;

    /**
     * Uploads the file from a specified file path to the signed URL with
     * specified headers
     * 
     * @param signedUrl
     *            Signed url, which has the bucket name, object key, account
     *            information and accessed Ids and its signature. The url is
     *            recommended to be generated by generatePresignedUrl().
     * @param filePath
     *            The file path to read from
     * @param requestHeaders
     *            Request headers, including standard or customized http headers
     *            documented by PutObject REST API.
     * @return A {@link PutObjectResult} instance.
     */
    public PutObjectResult putObject(URL signedUrl, String filePath, Map<String, String> requestHeaders)
            throws OSSException, ClientException;

    /**
     * Uploads the file from a specified file path to the signed URL with
     * specified headers with the flag of using chunked tranfer encoding.
     * 
     * @param signedUrl
     *            Signed url, which has the bucket name, object key, account
     *            information and accessed Ids and its signature. The url is
     *            recommended to be generated by generatePresignedUrl().
     * @param filePath
     *            The file path to read from.
     * @param requestHeaders
     *            Request headers, including standard or customized http headers
     *            documented by PutObject REST API.
     * @param useChunkEncoding
     *            The flag of using chunked transfer encoding.
     * @return A {@link PutObjectResult} instance.
     */
    public PutObjectResult putObject(URL signedUrl, String filePath, Map<String, String> requestHeaders,
            boolean useChunkEncoding) throws OSSException, ClientException;

    /**
     * Uploads the file from a InputStream instance to the signed URL with
     * specified headers.
     * 
     * @param signedUrl
     *            Signed Url, which has the bucket name, object key, account
     *            information and accessed Ids and its signature. The url is
     *            recommended to be generated by generatePresignedUrl().
     * @param requestContent
     *            {@link InputStream} instance to read from.
     * @param contentLength
     *            Hint content length to write.
     * @param requestHeaders
     *            Request headers,including standard or customized http headers
     *            documented by PutObject REST API.
     * @return A {@link PutObjectResult} instance.
     */
    public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength,
            Map<String, String> requestHeaders) throws OSSException, ClientException;

    /**
     * Uploads the file from a InputStream instance to the signed URL with
     * specified headers.
     * 
     * @param signedUrl
     *            Signed Url, which has the bucket name, object key, account
     *            information and accessed Ids and its signature. The url is
     *            recommended to be generated by generatePresignedUrl().
     * @param requestContent
     *            {@link InputStream} instance to read from.
     * @param contentLength
     *            Hint content length to write. if useChunkEncoding is true,
     *            then -1 is used.
     * @param requestHeaders
     *            Rquest headers,including standard or customized http headers
     *            documented by PutObject REST API.
     * @param useChunkEncoding
     *            The flag of using chunked transfer encoding.
     * @return A {@link PutObjectResult} instance.
     */
    public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength,
            Map<String, String> requestHeaders, boolean useChunkEncoding) throws OSSException, ClientException;

    /**
     * Copies an existing file in OSS from source bucket to the target bucket.
     * If target file exists, it would be overwritten by the source file.
     * 
     * @param sourceBucketName
     *            Source object's bucket name.
     * @param sourceKey
     *            Source object's key.
     * @param destinationBucketName
     *            Target object's bucket name.
     * @param destinationKey
     *            Target object's key.
     * @return A {@link CopyObjectResult} instance.
     * @throws OSSException
     * @throws ClientException
     */
    public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
            String destinationKey) throws OSSException, ClientException;

    /**
     * Copies an existing file in OSS from source bucket to the target bucket.
     * If target file exists, it would be overwritten by the source file.
     * 
     * @param copyObjectRequest
     *            A {@link CopyObjectRequest} instance that specifies source
     *            file, source bucket and target file, target bucket。
     * @return A {@link CopyObjectResult} instance.
     * @throws OSSException
     * @throws ClientException
     */
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws OSSException, ClientException;

    /**
     * Gets a {@link OSSObject} from {@link Bucket}.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object Key.
     * @return A {@link OSSObject} instance. The caller is responsible to close
     *         the connection after usage.
     */
    public OSSObject getObject(String bucketName, String key) throws OSSException, ClientException;

    /**
     * Downloads the file from a file specified by the {@link GetObjectRequest}
     * parameter.
     * 
     * @param getObjectRequest
     *            A {@link GetObjectRequest} instance which specifies bucket
     *            name and object key.
     * @param file
     *            Target file instance to download as.
     */
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File file) throws OSSException, ClientException;

    /**
     * Gets the {@link OSSObject} from the bucket specified in
     * {@link GetObjectRequest} parameter.
     * 
     * @param getObjectRequest
     *            A {@link GetObjectRequest} instance which specifies the bucket
     *            name and the object key.
     * @return A {@link OSSObject} instance of the bucket file. The caller is
     *         responsible to close the connection after usage.
     */
    public OSSObject getObject(GetObjectRequest getObjectRequest) throws OSSException, ClientException;

    /**
     * Select the {@link OSSObject} from the bucket specified in
     * {@link SelectObjectRequest} parameter
     * @param selectObjectRequest
     *          A {@link SelectObjectRequest} instance which specifies the
     *              bucket name
     *              object key
     *              filter expression
     *              input serialization
     *              output serialization
     * @return A {@link OSSObject} instance will be returned. The caller is
     *          responsible to close the connection after usage.
     * @throws OSSException
     * @throws ClientException
     */
    public OSSObject selectObject(SelectObjectRequest selectObjectRequest) throws OSSException, ClientException;

    /**
     * Gets the {@link OSSObject} from the signed Url.
     * 
     * @param signedUrl
     *            The signed Url.
     * @param requestHeaders
     *            Request headers, including http standard or OSS customized
     *            headers.
     * @return A{@link OSSObject} instance.The caller is responsible to close
     *         the connection after usage.
     */
    public OSSObject getObject(URL signedUrl, Map<String, String> requestHeaders) throws OSSException, ClientException;

    /**
     * Gets the simplified metadata information of {@link OSSObject}.
     * <p>
     * Simplified metadata includes ETag, Size, LastModified and thus it's more
     * lightweight then GetObjectMeta().
     * </p>
     *
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object key.
     * @return A {@link SimplifiedObjectMeta} instance of the object.
     */
    public SimplifiedObjectMeta getSimplifiedObjectMeta(String bucketName, String key)
            throws OSSException, ClientException;

    /**
     * Gets the simplified metadata information of {@link OSSObject}.
     * <p>
     * Simplified metadata includes ETag, Size, LastModified and thus it's more
     * lightweight then GetObjectMeta().
     * </p>
     *
     * @param genericRequest
     *            Generic request which specifies the bucket name and object key
     * @return The {@link SimplifiedObjectMeta} instance of specified file.
     */
    public SimplifiedObjectMeta getSimplifiedObjectMeta(GenericRequest genericRequest)
            throws OSSException, ClientException;
    
    /**
     * Gets all the metadata of {@link OSSObject}.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object key.
     *
     * @return The {@link ObjectMetadata} instance.
     */
    public ObjectMetadata getObjectMetadata(String bucketName, String key) throws OSSException, ClientException;

    /**
     * Gets all the metadata of {@link OSSObject}.
     * 
     * @param genericRequest
     *            Generic request which specifies the bucket name and object
     *            key.
     *
     * @return The {@link ObjectMetadata} instance.
     *
     */
    public ObjectMetadata getObjectMetadata(GenericRequest genericRequest) throws OSSException, ClientException;
    
    /**
     * Create select object metadata(create metadata if not exists or overwrite flag set in {@link CreateSelectObjectMetadataRequest})
     *
     * @param createSelectObjectMetadataRequest
     *            {@link CreateSelectObjectMetadataRequest} create select object metadata request.
     *
     * @return The {@link SelectObjectMetadata} instance.
     */
    public SelectObjectMetadata createSelectObjectMetadata(CreateSelectObjectMetadataRequest createSelectObjectMetadataRequest) throws OSSException, ClientException;

    /**
     * Gets all the head data of {@link OSSObject}.
     *
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object key.
     *
     * @return The {@link ObjectMetadata} instance.
     */
    public ObjectMetadata headObject(String bucketName, String key) throws OSSException, ClientException;

    /**
     * Gets all the head data of {@link OSSObject}.
     *
     * @param headObjectRequest
     *            A {@link HeadObjectRequest} instance which specifies the
     *            bucket name and object key, and some constraint information can be set.
     * @return The {@link ObjectMetadata} instance.
     */
    public ObjectMetadata headObject(HeadObjectRequest headObjectRequest) throws OSSException, ClientException;

    /**
     * Append the data to the appendable object specified in
     * {@link AppendObjectRequest}. It's not applicable to normal OSS object.
     * 
     * @param appendObjectRequest
     *            A {@link AppendObjectRequest} instance which specifies the
     *            bucket name, appendable object key, the file or the
     *            InputStream object to append.
     * @return A {@link AppendObjectResult} instance.
     */
    public AppendObjectResult appendObject(AppendObjectRequest appendObjectRequest)
            throws OSSException, ClientException;

    /**
     * Deletes the specified {@link OSSObject} by bucket name and object key.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object key.
     */
    public void deleteObject(String bucketName, String key) throws OSSException, ClientException;

    /**
     * Deletes the specified {@link OSSObject} by the {@link GenericRequest}
     * instance.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance that specfies the bucket
     *            name and object key.
     */
    public void deleteObject(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Batch deletes the specified files under a specific bucket. If the files
     * are non-exist, the operation will still return successful.
     * 
     * @param deleteObjectsRequest
     *            A {@link DeleteObjectsRequest} instance which specifies the
     *            bucket and file keys to delete.
     * @return A {@link DeleteObjectsResult} instance which specifies each
     *         file's result in normal mode or only failed deletions in quite
     *         mode. By default it's normal mode.
     */
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest)
            throws OSSException, ClientException;
    
    /**
     * <p>
     * Deletes a specific version of the specified object in the specified
     * bucket. Once deleted, there is no method to restore or undelete an object
     * version. This is the only way to permanently delete object versions that
     * are protected by versioning.
     * </p>
     * <p>
     * Deleting an object version is permanent and irreversible.
     * It is a
     * privileged operation that only the owner of the bucket containing the
     * version can perform.
     * </p>
     * <p>
     * Users can only delete a version of an object if versioning is enabled
     * for the bucket.
     * For more information about enabling versioning for a bucket, see
     * {@link #setBucketVersioning(SetBucketVersioningRequest)}.
     * </p>
     * <p>
     * If attempting to delete an object that does not exist,
     * OSS will return a success message instead of an error message.
     * </p>
     *
     * @param bucketName
     *            The name of the OSS bucket containing the object to delete.
     * @param key
     *            The key of the object to delete.
     * @param versionId
     *            The version of the object to delete.
     *
     * @throws ClientException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     * @throws OSSException
     *             If any errors occurred in OSS while processing the request.
     */
    public void deleteVersion(String bucketName, String key, String versionId) throws OSSException, ClientException;

    /**
     * <p>
     * Deletes a specific version of an object in the specified bucket. Once
     * deleted, there is no method to restore or undelete an object version.
     * This is the only way to permanently delete object versions that are
     * protected by versioning.
     * </p>
     * <p>
     * Deleting an object version is permanent and irreversible.
     * It is a
     * privileged operation that only the owner of the bucket containing the
     * version can perform.
     * </p>
     * <p>
     * Users can only delete a version of an object if versioning is enabled
     * for the bucket.
     * For more information about enabling versioning for a bucket, see
     * {@link #setBucketVersioning(SetBucketVersioningRequest)}.
     * </p>
     * <p>
     * If attempting to delete an object that does not exist,
     * OSS will return a success message instead of an error message.
     * </p>
     *
     * @param deleteVersionRequest
     *            The request object containing all options for deleting a
     *            specific version of an OSS object.
     *
     * @throws ClientException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     * @throws OSSException
     *             If any errors occurred in OSS while processing the request.
     */
    public void deleteVersion(DeleteVersionRequest deleteVersionRequest) throws OSSException, ClientException;
    
    /**
     * Batch deletes the specified object versions under a specific bucket. If the versions
     * are non-exist, the operation will still return successful.
     * 
     * @param deleteVersionsRequest
     *            A {@link DeleteVersionsRequest} instance which specifies the
     *            bucket and file keys to delete.
     * @return A {@link DeleteVersionsResult} instance which specifies each
     *         file's result in normal mode or only failed deletions in quite
     *         mode. By default it's normal mode.
     */
    public DeleteVersionsResult deleteVersions(DeleteVersionsRequest deleteVersionsRequest)
            throws OSSException, ClientException;
    
    /**
     * Checks if a specific {@link OSSObject} exists under the specific
     * {@link Bucket}. 302 Redirect or OSS mirroring will not impact the result
     * of this function.
     *
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object Key.
     * @return True if exists; false if not.
     */
    public boolean doesObjectExist(String bucketName, String key) throws OSSException, ClientException;

    /**
     * Checks if a specific {@link OSSObject} exists under the specific
     * {@link Bucket}. 302 Redirect or OSS mirroring will not impact the result
     * of this function.
     * 
     * @param genericRequest
     *            A {@link GenericRequest} instance which specifies the bucket
     *            and object key.
     * @return True if exists; false if not.
     */
    public boolean doesObjectExist(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Checks if a specific {@link OSSObject} exists under the specific
     * {@link Bucket}. 302 Redirect or OSS mirroring will impact the result of
     * this function if isOnlyInOSS is true.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object Key.
     * @param isOnlyInOSS
     *            true if ignore 302 redirect or mirroring； false if considering
     *            302 redirect or mirroring, which could download the object
     *            from source to OSS when the file exists in source but is not
     *            in OSS yet.
     * @return True if the file exists; false if not.
     */
    public boolean doesObjectExist(String bucketName, String key, boolean isOnlyInOSS);

    /**
     * Checks if a specific {@link OSSObject} exists under the specific
     * {@link Bucket}. 302 Redirect or OSS mirroring will not impact the result
     * of this function.
     * 
     * @param genericRequest
     *            A {@link GenericRequest} instance which specifies the bucket
     *            and object key.
     * @param isOnlyInOSS
     *            true if ignore 302 redirect or mirroring； false if considering
     *            302 redirect or mirroring, which could download the object
     *            from source to OSS when the file exists in source but is not
     *            in OSS yet.        
     * @return True if exists; false if not.
     */
    public boolean doesObjectExist(GenericRequest genericRequest, boolean isOnlyInOSS) throws OSSException, ClientException;

    /**
     * Checks if a specific {@link OSSObject} exists.
     * 
     * @param headObjectRequest
     *            A {@link HeadObjectRequest} instance which specifies the
     *            bucket name and object key. Constraint information is ignored.
     * @return True if the file exists; false if not.
     */
    @Deprecated
    public boolean doesObjectExist(HeadObjectRequest headObjectRequest) throws OSSException, ClientException;

    /**
     * Sets the Access Control List (ACL) on a {@link OSSObject} instance.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object Key.
     * @param cannedAcl
     *            One of the three values: Private, PublicRead or
     *            PublicReadWrite.
     */
    public void setObjectAcl(String bucketName, String key, CannedAccessControlList cannedAcl)
            throws OSSException, ClientException;

    /**
     * Sets the Access Control List (ACL) on a {@link OSSObject} instance.
     * 
     * @param setObjectAclRequest
     *            A {@link SetObjectAclRequest} instance which specifies the
     *            object's bucket name and key as well as the ACL information.
     */
    public void setObjectAcl(SetObjectAclRequest setObjectAclRequest) throws OSSException, ClientException;

    /**
     * Gets the Access Control List (ACL) of the OSS object.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object Key.
     * @return The {@link ObjectAcl} instance of the object.
     */
    public ObjectAcl getObjectAcl(String bucketName, String key) throws OSSException, ClientException;

    /**
     * Gets the Access Control List (ACL) of the OSS object.
     * 
     * @param genericRequest
     *            A {@link GenericRequest} instance which specifies the bucket
     *            name and object key.
     */
    public ObjectAcl getObjectAcl(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Restores the object of archive storage. The function is not applicable to
     * Normal or IA storage. The restoreObject() needs to be called prior to
     * calling getObject() on an archive object.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object Key.
     * @return A {@link RestoreObjectResult} instance.
     */
    public RestoreObjectResult restoreObject(String bucketName, String key) throws OSSException, ClientException;

    /**
     * Restores the object of archive storage. The function is not applicable to
     * Normal or IA storage. The restoreObject() needs to be called prior to
     * calling getObject() on an archive object.
     * 
     * @param genericRequest
     *            A {@link GenericRequest} instance that specifies the bucket
     *            name and object key.
     * @return A {@link RestoreObjectResult} instance.
     */
    public RestoreObjectResult restoreObject(GenericRequest genericRequest) throws OSSException, ClientException;


    /**
     * Sets the tags on the OSS object.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object name.
     * @param tags
     *            The dictionary that contains the tags in the form of &lt;key,
     *            value&gt; pairs.
     */
    public void setObjectTagging(String bucketName, String key, Map<String, String> tags) throws OSSException, ClientException;

    /**
     * Sets the tags on the OSS object.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object name.
     * @param tagSet
     *            {@link TagSet} instance that has the tags in the form of &lt;key,
     *            value&gt; pairs.
     */
    public void setObjectTagging(String bucketName, String key, TagSet tagSet) throws OSSException, ClientException;

    /**
     * Sets the tags on the OSS object.
     * 
     * @param setObjectTaggingRequest
     *            {@link SetObjectTaggingRequest} instance that has object
     *            information as well as tagging information.
     */
    public void setObjectTagging(SetObjectTaggingRequest setObjectTaggingRequest) throws OSSException, ClientException;

    /**
     * Gets all tags of the OSS object.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object name.
     * @return A {@link TagSet} instance. If there's no tag, the TagSet object
     *         with empty tag information is returned.
     */
    public TagSet getObjectTagging(String bucketName, String key) throws OSSException, ClientException;

    /**
     * Gets all tags of the OSS object.
     * 
     * @param genericRequest
     *            A {@link GenericRequest} instance that specifies the bucket
     *            name and object name.
     * @return A {@link TagSet} instance.
     */
    public TagSet getObjectTagging(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Clears all the tags of the OSS object.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object name.
     */
    public void deleteObjectTagging(String bucketName, String key) throws OSSException, ClientException;

    /**
     *  Clears all the tags of the OSS object.
     * 
     * @param genericRequest
     *            A {@link GenericRequest} instance that specifies the bucket
     *            name and object name.
     */
    public void deleteObjectTagging(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Generates a signed url for accessing the {@link OSSObject} with HTTP GET
     * method.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object key.
     * @param expiration
     *            URL's expiration time.
     * @return A signed URL that could be used for accessing the
     *         {@link OSSObject} object.
     * @throws ClientException
     */
    public URL generatePresignedUrl(String bucketName, String key, Date expiration) throws ClientException;

    /**
     * Generates a signed url for accessing the {@link OSSObject} with a
     * specific HTTP method.
     * 
     * @param bucketName
     *            Bucket name.
     * @param key
     *            Object Key.
     * @param expiration
     *            URL's expiration time.
     * @param method
     *            HTTP method，Only {@link HttpMethod#GET} and
     *            {@link HttpMethod#PUT} are supported.
     * @return A signed URL that could be used for accessing the
     *         {@link OSSObject} object.
     * @throws ClientException
     */
    public URL generatePresignedUrl(String bucketName, String key, Date expiration, HttpMethod method)
            throws ClientException;

    /**
     * Generates a signed url for accessing the {@link OSSObject} with a
     * specific HTTP method.
     * 
     * @param request
     *            A {@link GeneratePresignedUrlRequest} instance which specifies
     *            the bucket name, file key, expiration time, HTTP method, and
     *            the MD5 signature of the content, etc.
     * @return A signed URL that could be used for accessing the
     *         {@link OSSObject} object.
     * @throws ClientException
     */
    public URL generatePresignedUrl(GeneratePresignedUrlRequest request) throws ClientException;

    /**
     * Sets image processing attributes on the specific {@link Bucket}
     * 
     * @param request
     *            A {@link PutBucketImageRequest} instances which specifies some
     *            attributes of image processing.
     * @throws OSSException
     * @throws ClientException
     */
    public void putBucketImage(PutBucketImageRequest request) throws OSSException, ClientException;

    /**
     * Gets the image processing attributes on the specific {@link Bucket}.
     * 
     * @param bucketName
     *            The bucket name
     * @return A {@link GetBucketImageResult} instance which has attributes of
     *         image processing
     * @throws OSSException
     * @throws ClientException
     */
    public GetBucketImageResult getBucketImage(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the image processing attributes on the specific {@link Bucket}.
     * 
     * @param bucketName
     *            The bucket name.
     * @param genericRequest
     *            The origin request.
     * @return A {@link GetBucketImageResult} which has the attributes of image
     *         processing.
     * @throws OSSException
     * @throws ClientException
     */
    public GetBucketImageResult getBucketImage(String bucketName, GenericRequest genericRequest)
            throws OSSException, ClientException;

    /**
     * Deletes the image processing attributes on the specific {@link Bucket}.
     * 
     * @param bucketName
     *            Bucket name
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketImage(String bucketName) throws OSSException, ClientException;

    /**
     * Deletes the image processing attributes on the specific {@link Bucket}.
     * 
     * @param bucketName
     *            Bucket name
     * @param genericRequest
     *            The origin request
     * @throws OSSException
     * @param genericRequest
     * @throws ClientException
     */
    public void deleteBucketImage(String bucketName, GenericRequest genericRequest)
            throws OSSException, ClientException;

    /**
     * Deletes a style named by parameter styleName under {@link Bucket}
     * 
     * @param bucketName
     *            Bucket name
     * @param styleName
     *            Style name
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteImageStyle(String bucketName, String styleName) throws OSSException, ClientException;

    /**
     * Deletes a style named by parameter styleName under {@link Bucket}
     * 
     * @param bucketName
     *            Bucket name
     * @param styleName
     *            Style name
     * @param genericRequest
     *            The origin request
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteImageStyle(String bucketName, String styleName, GenericRequest genericRequest)
            throws OSSException, ClientException;

    /**
     * Adds a new style under {@link Bucket}.
     * 
     * @param putImageStyleRequest
     *            A {@link PutImageStyleRequest} instance that has bucket name
     *            and style information
     * @throws OSSException
     * @throws ClientException
     */
    public void putImageStyle(PutImageStyleRequest putImageStyleRequest) throws OSSException, ClientException;

    /**
     * Gets a style named by parameter styleName under {@link Bucket}
     * 
     * @param bucketName
     *            Bucket name.
     * @param styleName
     *            Style name.
     * @return A {@link GetImageStyleResult} instance which has the style
     *         information if successful or error code if failed.
     * @throws OSSException
     * @throws ClientException
     */
    public GetImageStyleResult getImageStyle(String bucketName, String styleName) throws OSSException, ClientException;

    /**
     * Gets a style named by parameter styleName under the {@link Bucket}
     * 
     * @param bucketName
     *            Bucket name.
     * @param styleName
     *            Style name.
     * @param genericRequest
     *            The origin request.
     * @return A {@link GetImageStyleResult} instance which has the style
     *         information if successful or error code if failed.
     * @throws OSSException
     * @throws ClientException
     */
    public GetImageStyleResult getImageStyle(String bucketName, String styleName, GenericRequest genericRequest)
            throws OSSException, ClientException;

    /**
     * Lists all styles under the {@link Bucket}
     * 
     * @param bucketName
     *            Bucket name.
     * @return A {@link List} of all styles of the Bucket. If there's no style,
     *         it will be an empty list.
     * @throws OSSException
     * @throws ClientException
     */
    public List<Style> listImageStyle(String bucketName) throws OSSException, ClientException;

    /**
     * Lists all styles under the {@link Bucket}
     * 
     * @param bucketName
     *            Bucket name.
     * @param genericRequest
     *            The origin request.
     * @return A {@link List} of all styles of the Bucket. If there's no style,
     *         it will be an empty list.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<Style> listImageStyle(String bucketName, GenericRequest genericRequest)
            throws OSSException, ClientException;

    /**
     * Creates the image accessing configuration according to the parameter
     * setBucketProcessRequest.
     * 
     * @param setBucketProcessRequest
     *            A {@link SetBucketTaggingRequest} instance that contains the
     *            image accessing configuration such as enable original picture
     *            protection, etc.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void setBucketProcess(SetBucketProcessRequest setBucketProcessRequest) throws OSSException, ClientException;

    /**
     * Gets the bucket's image accessing configuration.
     * 
     * @param bucketName
     *            Bucket name.
     * @return A {@link BucketProcess} which contains the image accessing
     *         configurations if succeeds.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public BucketProcess getBucketProcess(String bucketName) throws OSSException, ClientException;

    /**
     * Get the bucket's image accessing configuration
     * 
     * @param genericRequest
     *            A {@link GenericRequest} instance that has the bucket name.
     * @return A {@link BucketProcess} which contains the image accessing
     *         configurations if succeeds.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public BucketProcess getBucketProcess(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Initiates a multiple part upload
     * <p>
     * Prior to starting a multiple part upload, this method needs to be called
     * to ask OSS service do some initialization work. Upon a successful call,
     * it returns a globally unique upload ID which could be used for the
     * subsequent operations such as pause, lookup multiple parts, etc. This
     * method will not automatically retry even if the max retry count is
     * greater than 0, because it's not idempotent.
     * </p>
     * 
     * @param request
     *            A {@link InitiateMultipartUploadRequest} instance which
     *            specifies the bucket name, object key and metadata.
     * @return a {@link InitiateMultipartUploadResult} instance which has the
     *         global unique id if succeeds.
     * @throws ClientException
     */
    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request)
            throws OSSException, ClientException;

    /**
     * Lists executing multiple parts uploads.
     * <p>
     * Those initialized but not finished multipart uploads would be listed by
     * this method. If the executing multiple parts upload count is more than
     * maxUploads (which could be up to 1000), then it would return the
     * nextUploadIdMaker and nextKeyMaker which could be used for next call.
     * When keyMarker in parameter request is specified, it would list executing
     * multipart uploads whose keys are greater than the keyMarker in
     * lexicographic order and multipart uploads whose keys are equal to the
     * keyMarker and uploadIds are greater than uploadIdMarker in lexicographic
     * order. In the other words, the keyMarker has the priority over the
     * uploadIdMarker and uploadIdMarker only impacts the uploads who has the
     * same keys as the keyMarker.
     * </p>
     * 
     * @param request
     *            A {@link ListMultipartUploadsRequest} instance.
     * @return MultipartUploadListing A {@link MultipartUploadListing} instance.
     *         Upon a successful call, it may has nextKeyMarker and
     *         nextUploadIdMarker for the next call in case OSS has remaining
     *         uploads not returned.
     * @throws ClientException
     */
    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request)
            throws OSSException, ClientException;

    /**
     * Lists all parts in a multiple parts upload.
     * 
     * @param request
     *            A {@link ListPartsRequest} instance.
     * @return PartListing
     * @throws ClientException
     */
    public PartListing listParts(ListPartsRequest request) throws OSSException, ClientException;

    /**
     * Uploads a part to a specified multiple upload.
     * 
     * @param request
     *            A {@link UploadPartRequest} instance which specifies bucket,
     *            object key, upload id, part number, content and length, MD5
     *            digest and chunked transfer encoding flag.
     * @return UploadPartResult A {@link UploadPartResult} instance to indicate
     *         the upload result.
     * @throws ClientException
     */
    public UploadPartResult uploadPart(UploadPartRequest request) throws OSSException, ClientException;

    /**
     * Uploads Part copy from an existing source object to a target object with
     * specified upload Id and part number
     * 
     * @param request
     *            A {@link UploadPartCopyRequest} instance which specifies: 1)
     *            source file 2) source file's copy range 3) target file 4)
     *            target file's upload Id and its part number 5) constraints
     *            such as ETag match or non-match, last modified match or
     *            non-match, etc.
     * @return A {@link UploadPartCopyResult} instance which has the part number
     *         and ETag upon a successful upload.
     * @throws OSSException
     * @throws ClientException
     */
    public UploadPartCopyResult uploadPartCopy(UploadPartCopyRequest request) throws OSSException, ClientException;

    /**
     * Abort a multiple parts upload. All uploaded data will be released in OSS.
     * The executing uploads of the same upload Id will get immediate failure
     * once this method is called.
     * 
     * @param request
     *            A {@link AbortMultipartUploadRequest} instance which specifies
     *            the file name and the upload Id to abort.
     * @throws ClientException
     */
    public void abortMultipartUpload(AbortMultipartUploadRequest request) throws OSSException, ClientException;

    /**
     * Complete a multiple parts upload.
     * <p>
     * After all parts uploads finish, this API needs to be called to finalize
     * the upload. All parts' number and their ETag are required and if ETag
     * verification is not passed, the API will fail. The parts' are not
     * necessarily ordered and the final file's content is determined by the
     * order in partETags list.
     * </p>
     * 
     * <p>
     * The API will not automatically retry even if the max retry count is
     * greater than 0 because it's not idempotent.
     * </p>
     * 
     * @param request
     *            A {@link CompleteMultipartUploadRequest} instance which
     *            specifies all parameters to complete multiple part upload.
     * @return A {@link CompleteMultipartUploadResult} instance which has the
     *         key, ETag, url of the final object.
     * @throws ClientException
     */
    public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request)
            throws OSSException, ClientException;

    /**
     * Adds CORS rules to the bucket. If the same source has been specified with
     * other rules, this will overwrite (not merge) them. For example, if
     * alibaba-inc.com is a trusted source and was specified to allow GET
     * Method. Then in this request, it's specified with POST Method. In the
     * end, alibaba-inc.com will only be allowed with POST method.
     * 
     * @param request
     *            A {@link SetBucketCORSRequest} object that has defined all
     *            CORS rules.
     * @throws OSSException
     * @throws ClientException
     */
    public void setBucketCORS(SetBucketCORSRequest request) throws OSSException, ClientException;

    /**
     * Lists all CORS rules from the bucket.
     * 
     * @param bucketName
     *            Bucket name.
     * @return A list of {@link CORSRule} under the bucket.
     * @throws OSSException
     * @throws ClientException
     */
    public List<CORSRule> getBucketCORSRules(String bucketName) throws OSSException, ClientException;

    /**
     * Lists all CORS rules from the bucket.
     * 
     * @param genericRequest
     *            A {@link GenericRequest} instance that specifies the bucket
     *            name.
     * @return A list of {@link CORSRule} under the bucket.
     * @throws OSSException
     * @throws ClientException
     */
    public List<CORSRule> getBucketCORSRules(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Deletes all CORS rules under the bucket.
     * 
     * @param bucketName
     *            The bucket name.
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketCORSRules(String bucketName) throws OSSException, ClientException;

    /**
     * Deletes all CORS rules under the bucket.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance that specifies the bucket
     *            name.
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketCORSRules(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Deprecated method.
     */
    @Deprecated
    public ResponseMessage optionsObject(OptionsRequest request) throws OSSException, ClientException;

    /**
     * Enables or disables the {@link Bucket}'s logging. To enable the logging,
     * the TargetBucket attribute in SetBucketLoggingRequest object must be
     * specified. To disable the logging, the TargetBucket attribute in
     * SetBucketLoggingRequest object must be null. The logging file will be
     * hourly rolling log.
     * 
     * @param request
     *            A {@link SetBucketLoggingRequest} instance which specifies the
     *            bucket name to set the logging, the target bucket to store the
     *            logging data and the prefix of the logging file.
     */
    public void setBucketLogging(SetBucketLoggingRequest request) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s logging setting.
     * 
     * @param bucketName
     *            The bucket name.
     * @return A {@link BucketLoggingResult} instance which contains the logging
     *         settings such as target bucket for data, logging file prefix.
     * @throws OSSException
     * @throws ClientException
     */
    public BucketLoggingResult getBucketLogging(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s logging setting.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     * @return A {@link BucketLoggingResult} instance which contains the logging
     *         settings such as target bucket for data, logging file prefix.
     * @throws OSSException
     * @throws ClientException
     */
    public BucketLoggingResult getBucketLogging(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Disables the logging on {@link Bucket}.
     * 
     * @param bucketName
     *            Bucket Name
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketLogging(String bucketName) throws OSSException, ClientException;

    /**
     * Disables the logging on {@link Bucket}.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketLogging(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Sets the static website settings for the {@link Bucket}. The settings
     * includes the mandatory home page, the optional 404 page and the routing
     * rules. If home page is null, then the static website is not enabled on
     * the bucket.
     * 
     * @param setBucketWebSiteRequest
     *            A {@link SetBucketWebsiteRequest} instance to set with.
     * @throws OSSException
     * @throws ClientException
     */
    public void setBucketWebsite(SetBucketWebsiteRequest setBucketWebSiteRequest) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s static website settings.
     * 
     * @param bucketName
     *            The bucket name.
     * @return A {@link BucketWebsiteResult} instance
     * @throws OSSException
     * @throws ClientException
     */
    public BucketWebsiteResult getBucketWebsite(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s static webite settings.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     * @return A {@link BucketWebsiteResult} instance.
     * @throws OSSException
     * @throws ClientException
     */
    public BucketWebsiteResult getBucketWebsite(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Deletes the {@link Bucket}'s static website configuration, which means
     * disabling the static website on the bucket.
     * 
     * @param bucketName
     *            Bucket name
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketWebsite(String bucketName) throws OSSException, ClientException;

    /**
     * Deletes the {@link Bucket}'s static website configuration, which means
     * disabling the static website on the bucket.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     * @throws OSSException
     * @throws ClientException
     */
    public void deleteBucketWebsite(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Generates the post policy form field in JSON format.
     * 
     * @param expiration
     *            Policy expiration time.
     * @param conds
     *            Policy condition lists.
     * @return Policy string in JSON format.
     */
    public String generatePostPolicy(Date expiration, PolicyConditions conds) throws ClientException;

    /**
     * Calculates the signature based on the policy and access key secret.
     * 
     * @param postPolicy
     *            Post policy string in JSON which is generated from
     *            {@link #generatePostPolicy(Date, PolicyConditions)}.
     * @return Post signature in bas464 string.
     */
    public String calculatePostSignature(String postPolicy);

    /**
     * Sets the {@link Bucket}'s lifecycle rule.
     * 
     * @param setBucketLifecycleRequest
     *            A {@link SetBucketWebsiteRequest} instance which specifies the
     *            lifecycle rules
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void setBucketLifecycle(SetBucketLifecycleRequest setBucketLifecycleRequest)
            throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s lifecycle rules.
     * 
     * @param bucketName
     *            Bucket name.
     * @return A list of {@link LifecycleRule}.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<LifecycleRule> getBucketLifecycle(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s Lifecycle rules.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     * @return A List of {@link LifecycleRule} instances.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<LifecycleRule> getBucketLifecycle(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Deletes all the {@link Bucket}'s Lifecycle rules.
     * 
     * @param bucketName
     *            The bucket name to operate on.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteBucketLifecycle(String bucketName) throws OSSException, ClientException;

    /**
     * Deletes all the {@link Bucket}'s Lifecycle rules.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteBucketLifecycle(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Adds a {@link Bucket}'s cross-region replication rule.
     * 
     * @param addBucketReplicationRequest
     *            A {@link AddBucketReplicationRequest} instance which specifies
     *            a replication rule.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void addBucketReplication(AddBucketReplicationRequest addBucketReplicationRequest)
            throws OSSException, ClientException;

    /**
     * Gets all the {@link Bucket}'s cross region replication rules.
     * 
     * @param bucketName
     *            Bucket name.
     * @return A list of {@link ReplicationRule} under the bucket.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<ReplicationRule> getBucketReplication(String bucketName) throws OSSException, ClientException;

    /**
     * Gets all the {@link Bucket}'s cross region replication rules.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     * @return A list of {@link ReplicationRule} under the bucket.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<ReplicationRule> getBucketReplication(GenericRequest genericRequest)
            throws OSSException, ClientException;

    /**
     * Deletes the specified {@link Bucket}'s cross region replication rule.
     * 
     * @param bucketName
     *            Bucket name.
     * @param replicationRuleID
     *            Replication Id to delete.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteBucketReplication(String bucketName, String replicationRuleID)
            throws OSSException, ClientException;

    /**
     * Deletes the specified {@link Bucket}'s cross region replication rule.
     * 
     * @param deleteBucketReplicationRequest
     *            The {@link DeleteBucketReplicationRequest} instance which
     *            specifies the replication rule Id to delete.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteBucketReplication(DeleteBucketReplicationRequest deleteBucketReplicationRequest)
            throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s progress of the specified cross region
     * replication rule.
     * 
     * @param bucketName
     *            Bucket name.
     * @param replicationRuleID
     *            Replication Rule Id.
     * @return The new data's and historical data's replication progress in
     *         float.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public BucketReplicationProgress getBucketReplicationProgress(String bucketName, String replicationRuleID)
            throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s progress of the specified cross region
     * replication rule.
     * 
     * @param getBucketReplicationProgressRequest
     *            The {@link GetBucketReplicationProgressRequest} instance which
     *            specifies the replication rule Id and bucket name.
     * @return The new data's and historical data's replication progress in
     *         float.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public BucketReplicationProgress getBucketReplicationProgress(
            GetBucketReplicationProgressRequest getBucketReplicationProgressRequest)
            throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s replication reachable data centers.
     * 
     * @param bucketName
     *            Bucket name.
     * @return Replication reachable data center list.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<String> getBucketReplicationLocation(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s replication reachable data centers.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance that specifies the bucket
     *            name.
     * @return Replication reachable data center list.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<String> getBucketReplicationLocation(GenericRequest genericRequest)
            throws OSSException, ClientException;

    /**
     * Adds a Cname for the {@link Bucket} instance.
     * 
     * @param addBucketCnameRequest
     *            The request specifies the bucket name and the Cname
     *            information.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void addBucketCname(AddBucketCnameRequest addBucketCnameRequest) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s Cnames.
     * 
     * @param bucketName
     *            Bucket name.
     * @return The list of Cnames under the bucket.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<CnameConfiguration> getBucketCname(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s Cnames.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     * @return The list of Cnames under the bucket.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<CnameConfiguration> getBucketCname(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Deletes one {@link Bucket}'s Cname specified by the parameter domain.
     * 
     * @param bucketName
     *            The bucket name。
     * @param domain
     *            cname。
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteBucketCname(String bucketName, String domain) throws OSSException, ClientException;

    /**
     * Deletes one {@link Bucket}'s specific Cname specified by the parameter
     * domain.
     * 
     * @param deleteBucketCnameRequest
     *            A {@link DeleteBucketCnameRequest} instance that specifies the
     *            bucket name and the domain name to delete
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteBucketCname(DeleteBucketCnameRequest deleteBucketCnameRequest)
            throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s basic information as well as its ACL.
     * 
     * @param bucketName
     *            The bucket name。
     * @return A {@link BucketInfo} instance.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public BucketInfo getBucketInfo(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s basic information as well as its ACL.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     * @return A {@link BucketInfo} instance.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public BucketInfo getBucketInfo(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s storage information such as object counts,
     * storage size and executing multipart uploads.
     * 
     * @param bucketName
     *            The bucket name。
     * @return A {@link BucketStat} instance.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public BucketStat getBucketStat(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s storage information such as object counts,
     * storage size and executing multipart uploads.
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     * @return A {@link BucketStat} instance.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public BucketStat getBucketStat(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Sets the capacity of the {@link Bucket}.
     * 
     * @param bucketName
     *            The bucket name。
     * @param userQos
     *            A {@link UserQos} instance which specifies the capacity in GB
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void setBucketStorageCapacity(String bucketName, UserQos userQos) throws OSSException, ClientException;

    /**
     * Sets the capacity of the {@link Bucket}.
     * 
     * @param setBucketStorageCapacityRequest
     *            A {@link SetBucketStorageCapacityRequest} instance which
     *            specifies the bucket name as well as a UserQos instance
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void setBucketStorageCapacity(SetBucketStorageCapacityRequest setBucketStorageCapacityRequest)
            throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s capacity
     * 
     * @param bucketName
     *            The bucket name.
     * @return A {@link UserQos} instance which has the capacity information.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public UserQos getBucketStorageCapacity(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the {@link Bucket}'s capacity
     * 
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     * @return A {@link UserQos} instance which has the capacity information.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public UserQos getBucketStorageCapacity(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Creates a new server-side encryption configuration (or replaces an existing one, if present).
     *
     * @param setBucketEncryptionRequest The request object for setting the bucket encryption configuration.
     *
     * @return A {@link SetBucketEncryptionRequest}.
     * 
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void setBucketEncryption(SetBucketEncryptionRequest setBucketEncryptionRequest) 
    		throws OSSException, ClientException;
    
    /**
     * Returns the server-side encryption configuration of a bucket.
     *
     * @param bucketName Name of the bucket to retrieve encryption configuration for.
     * 
     * @return A {@link ServerSideEncryptionConfiguration}.
     * 
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public ServerSideEncryptionConfiguration getBucketEncryption(String bucketName) throws OSSException, ClientException;

    /**
     * Returns the server-side encryption configuration of a bucket.
     *
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     *            
     * @return A {@link ServerSideEncryptionConfiguration}.
     * 
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public ServerSideEncryptionConfiguration getBucketEncryption(GenericRequest genericRequest) 
    		throws OSSException, ClientException;
    
    /**
     * Deletes the server-side encryption configuration from the bucket.
     *
     * @param bucketName
     *            The bucket name.
     * 
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteBucketEncryption(String bucketName) throws OSSException, ClientException;

    /**
     * Deletes the server-side encryption configuration from the bucket.
     *
     * @param genericRequest
     *            The {@link GenericRequest} instance which specifies the bucket
     *            name.
     *            
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteBucketEncryption(GenericRequest genericRequest) throws OSSException, ClientException;
    
    /**
     * Sets the policy on the {@link Bucket} instance.
     * 
     * @param bucketName
     *            Bucket name.
     * @param policyText
     *            Policy JSON text, please refer to the policy writing rules of Aliyun
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.           
     */
    public void setBucketPolicy(String bucketName,  String policyText) throws OSSException, ClientException;

    /**
     * Sets the policy on the {@link Bucket} instance.
     * 
     * @param SetBucketPolicyRequest
     *            {@link SetBucketPolicyRequest} instance that has bucket
     *            information as well as policy information.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.        
     */
    public void setBucketPolicy(SetBucketPolicyRequest setBucketPolicyRequest) throws OSSException, ClientException;
    
    /**
     * Gets policy text of the {@link Bucket} instance.
     * 
     * @param genericRequest
     *            {@link GenericRequest} instance that has the bucket name.
     * @return The policy's content in {@link InputStream}. 
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public GetBucketPolicyResult getBucketPolicy(GenericRequest genericRequest) throws OSSException, ClientException;
    
    /**
     * Gets policy text of the {@link Bucket} instance.
     * 
     * @param bucketName
     *            Bucket name
     * @return The policy's content in {@link InputStream}. 
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public GetBucketPolicyResult getBucketPolicy(String bucketName) throws OSSException, ClientException;
    
    /**
     * Delete policy of the {@link Bucket} instance.
     * 
     * @param genericRequest
     *            {@link GenericRequest} instance that has the bucket name.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteBucketPolicy(GenericRequest genericRequest) throws OSSException, ClientException;
    
    /**
     * Delete policy of the {@link Bucket} instance.
     * 
     * @param bucketName
     *            Bucket name
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.           
     */
    public void deleteBucketPolicy(String bucketName) throws OSSException, ClientException;

    /**
     * File upload
     * 
     * This method will automatically split files into parts and upload them in
     * parallel by a thread pool, though by default the thread pool only has one
     * thread. After all parts are uploaded, then it will merge them into one
     * file. But if any one part fails to be uploaded, the whole upload fails.
     * Optionally a checkpoint file could be used to track the progress of the
     * upload and resume the upload later upon failure. Once the upload
     * completes, the checkpoint file would be deleted. By default checkpoint
     * file is disabled.
     *
     * @param uploadFileRequest
     *            A {@link UploadFileRequest} instance that specifies the bucket
     *            name, object key, file path ,part size (&gt; 100K) and thread
     *            count (from 1 to 1000) and checkpoint file.
     * @return A {@link UploadFileRequest} instance which has the new uploaded
     *         file's key, ETag, location.
     * @throws Throwable
     */
    public UploadFileResult uploadFile(UploadFileRequest uploadFileRequest) throws Throwable;

    /**
     * File download
     * 
     * Very similar with file upload, this method will split the OSS object into
     * parts and download them in parallel by a thread pool, though by default
     * the thread pool only has one thread. After all parts are downloaded, then
     * the method will merge them into one file. But if any one part fails to be
     * downloaded, the whole download fails. Optionally a checkpoint file could
     * be used to track the progress of the download and resume the download
     * later upon failure. Once the download completes, the checkpoint file
     * would be deleted. By default checkpoint file is disabled.
     * 
     * @param downloadFileRequest
     *            A {@link DownloadFileRequest} instance that specifies the
     *            bucket name, object key, file path, part size (&gt; 100K) and
     *            thread count (from 1 to 1000) and checkpoint file. Also it
     *            could have the ETag and ModifiedSince constraints.
     * @return A {@link DownloadFileResult} instance that has the
     *         {@link ObjectMetadata} information.
     * @throws Throwable
     */
    public DownloadFileResult downloadFile(DownloadFileRequest downloadFileRequest) throws Throwable;

    /**
     * Creates a live streaming channel. OSS could manage the RTMP inbound
     * stream by the "Live Channel". To store the RTMP stream into OSS, this
     * method needs to be called first to create a "Live Channel".
     * 
     * @param createLiveChannelRequest
     *            A {@link CreateLiveChannelRequest} instance that specifies the
     *            target bucket name, channel name, channel status (Enabled or
     *            Disabled), streaming storage status such as media file name,
     *            its .ts file time duration, etc.
     * @return A {@link CreateLiveChannelResult} instance that specifies the
     *         publish url and playback url.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public CreateLiveChannelResult createLiveChannel(CreateLiveChannelRequest createLiveChannelRequest)
            throws OSSException, ClientException;

    /**
     * Sets the Live Channel status.
     * 
     * A Live Channel could be disabled or enabled by setting its status.
     * 
     * @param bucketName
     *            Bucket name.
     * @param liveChannel
     *            Live Channel name.
     * @param status
     *            Live Channel status: "Enabled" or "Disabled".
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void setLiveChannelStatus(String bucketName, String liveChannel, LiveChannelStatus status)
            throws OSSException, ClientException;

    /**
     * Sets the Live Channel status.
     *
     * A Live Channel could be disabled or enabled by setting its status.
     * 
     * @param setLiveChannelRequest
     *            A {@link SetLiveChannelRequest} instance that specifies the
     *            bucket name, the channel name and the Live Channel status.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void setLiveChannelStatus(SetLiveChannelRequest setLiveChannelRequest) throws OSSException, ClientException;

    /**
     * Gets the Live Channel's configuration.
     * 
     * @param bucketName
     *            Bucket name.
     * @param liveChannel
     *            Live Channel name.
     * @return A {@link LiveChannelInfo} instance that contains the Live
     *         Channel's name, description, bucket name and its streaming
     *         storage information.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public LiveChannelInfo getLiveChannelInfo(String bucketName, String liveChannel)
            throws OSSException, ClientException;

    /**
     * Gets the Live Channel's configuration.
     * 
     * @param liveChannelGenericRequest
     *            A {@link LiveChannelGenericRequest} instance that specifies
     *            the bucket name and Live Channel name.
     * @return A {@link LiveChannelInfo} instance that contains the Live
     *         Channel's name, description, bucket name and its streaming
     *         storage information.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public LiveChannelInfo getLiveChannelInfo(LiveChannelGenericRequest liveChannelGenericRequest)
            throws OSSException, ClientException;

    /**
     * Gets Live Channel's streaming information.
     * 
     * @param bucketName
     *            Bucket name.
     * @param liveChannel
     *            Live Channel name.
     * @return A {@link LiveChannelStat} instance that contains the media's
     *         resolution, frame rate and bandwidth.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public LiveChannelStat getLiveChannelStat(String bucketName, String liveChannel)
            throws OSSException, ClientException;

    /**
     * Gets Live Channel's streaming information.
     * 
     * @param liveChannelGenericRequest
     *            A {@link LiveChannelGenericRequest} instance that specifies
     *            the bucket name and channel name.
     * @return A {@link LiveChannelStat} instance that contains the media's
     *         resolution, frame rate and bandwidth.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public LiveChannelStat getLiveChannelStat(LiveChannelGenericRequest liveChannelGenericRequest)
            throws OSSException, ClientException;

    /**
     * Deletes the Live Channel.
     * 
     * @param bucketName
     *            Bucket name.
     * @param liveChannel
     *            Live Channel name.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteLiveChannel(String bucketName, String liveChannel) throws OSSException, ClientException;

    /**
     * Deletes the Live Channel。
     * 
     * After the deletion, the media files are still kept. But the streaming
     * will not work on these files.
     * 
     * @param liveChannelGenericRequest
     *            A {@link LiveChannelGenericRequest} instance that specifies
     *            the
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteLiveChannel(LiveChannelGenericRequest liveChannelGenericRequest)
            throws OSSException, ClientException;

    /**
     * Lists all Live Channels under a bucket.
     * 
     * @param bucketName
     *            Bucket name.
     * @return A list of all {@link LiveChannel} instances under the bucket.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<LiveChannel> listLiveChannels(String bucketName) throws OSSException, ClientException;

    /**
     * Lists all Live Channels under a bucket that meets the requirement
     * specified by the parameter listLiveChannelRequest.
     * 
     * @param listLiveChannelRequest
     *            A {@link ListLiveChannelsRequest} that specifies the bucket
     *            name and its requirement on Live Channel instances to return,
     *            such as prefix, marker, max entries to return.
     * @return A list of {@link LiveChannel} instances that meet the
     *         requirements.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public LiveChannelListing listLiveChannels(ListLiveChannelsRequest listLiveChannelRequest)
            throws OSSException, ClientException;

    /**
     * Gets recent {@link LiveRecord} entries from the specified Live Channel.
     * OSS saves recent 10 LiveRecord (pushing streaming record) for every Live
     * Channel.
     *
     * @param bucketName
     *            Bucket name.
     * @param liveChannel
     *            Live Channel name.
     * @return Recent (up to 10) {@link LiveRecord} for the live channel.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<LiveRecord> getLiveChannelHistory(String bucketName, String liveChannel)
            throws OSSException, ClientException;

    /**
     * Gets recent {@link LiveRecord} entries from the specified Live Channel.
     * OSS saves recent 10 LiveRecord (pushing streaming record) for every Live
     * Channel.
     * 
     * @param liveChannelGenericRequest
     *            A {@link LiveChannelGenericRequest} instance that specifies
     *            the bucket name and Live Channel name.
     * @return Recent (up to 10) {@link LiveRecord} for the live channel.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public List<LiveRecord> getLiveChannelHistory(LiveChannelGenericRequest liveChannelGenericRequest)
            throws OSSException, ClientException;

    /**
     * Generates a VOD playlist (*.m3u8 file) for the *.ts files with specified
     * time range under the Live Channel.
     * 
     * @param bucketName
     *            Bucket name.
     * @param liveChannelName
     *            Live Channel name.
     * @param PlaylistName
     *            The playlist file name, such as (playlist.m3u8).
     * @param startTime
     *            The start time of the playlist in epoch time (means *.ts files
     *            time is same or later than it)
     * @param endTime
     *            The end time of the playlist in epoch time(means *.ts files
     *            time is no later than it).
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void generateVodPlaylist(String bucketName, String liveChannelName, String PlaylistName, long startTime,
            long endTime) throws OSSException, ClientException;

    /**
     * Generates a VOD playlist (*.m3u8 file) for the *.ts files with specified
     * time range under the Live Channel.
     * 
     * @param generateVodPlaylistRequest
     *            A {@link GenerateVodPlaylistRequest} instance the specifies
     *            the bucket name and the Live Channel name.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void generateVodPlaylist(GenerateVodPlaylistRequest generateVodPlaylistRequest)
            throws OSSException, ClientException;

    /**
     * Generates and returns a VOD playlist (m3u8 format) for the *.ts files with specified
     * time range under the Live Channel, but this VOD playlist would not be stored in OSS Server.
     *
     * @param bucketName
     *            Bucket name.
     * @param liveChannelName
     *            Live Channel name.
     * @param startTime
     *            The start time of the playlist in epoch time (means *.ts files
     *            time is same or later than it)
     * @param endTime
     *            The end time of the playlist in epoch time(means *.ts files
     *            time is no later than it).
     * @return A {@link OSSObject} instance.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public OSSObject getVodPlaylist(String bucketName, String liveChannelName, long startTime,
                                    long endTime) throws OSSException, ClientException;

    /**
     * Generates and returns a VOD playlist (m3u8 format) for the *.ts files with specified
     * time range under the Live Channel, but this VOD playlist would not be stored in OSS Server.
     *
     * @param getVodPlaylistRequest
     *            A {@link GetVodPlaylistRequest} instance the specifies
     *            the bucket name and the Live Channel name.
     * @return A {@link OSSObject} instance.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public OSSObject getVodPlaylist(GetVodPlaylistRequest getVodPlaylistRequest)
            throws OSSException, ClientException;

    /**
     * Generates a RTMP pushing streaming address in the Live Channel.
     * 
     * @param bucketName
     *            Bucket name.
     * @param liveChannelName
     *            Live Channel name.
     * @param PlaylistName
     *            The playlist file name such as playlist.m3u8.
     * @param expires
     *            Expiration time in epoch time, such as 1459922563.
     * @return Live Channel's RTMP pushing streaming address.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public String generateRtmpUri(String bucketName, String liveChannelName, String PlaylistName, long expires)
            throws OSSException, ClientException;

    /**
     * Generates a RTMP pushing streaming address in the Live Channel.
     * 
     * @param generatePushflowUrlRequest
     *            A {@link GenerateRtmpUriRequest} instance that specifies the
     *            bucket name and the Live Channel name.
     * @return Live Channel's RTMP pushing streaming address.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public String generateRtmpUri(GenerateRtmpUriRequest generatePushflowUrlRequest)
            throws OSSException, ClientException;

    /**
     * Creates a symlink link to a target file under the bucket---this is not
     * supported for archive class bucket.
     * 
     * @param bucketName
     *            Bucket name.
     * @param symlink
     *            symlink name.
     * @param target
     *            target file key.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void createSymlink(String bucketName, String symlink, String target) throws OSSException, ClientException;

    /**
     * Creates a symbol link to a target file under the bucket---this is not
     * supported for archive class bucket.
     * 
     * @param createSymlinkRequest
     *            A {@link CreateSymlinkRequest} instance that specifies the
     *            bucket name, symlink name.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void createSymlink(CreateSymlinkRequest createSymlinkRequest) throws OSSException, ClientException;

    /**
     * Gets the symlink information for the given symlink name.
     * 
     * @param bucketName
     *            Bucket name.
     * @param symlink
     *            The symlink name.
     * @return The symlink information, including the target file name and its
     *         metadata.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public OSSSymlink getSymlink(String bucketName, String symlink) throws OSSException, ClientException;

    /**
     * Gets the symlink information for the given symlink name.
     * 
     * @param genericRequest
     *            A {@link GenericRequest} instance which specifies the bucket
     *            name and symlink name.
     * @return The symlink information, including the target file name and its
     *         metadata.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public OSSSymlink getSymlink(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Apply process on the specified image file.
     * <p>
     * The supported process includes resize, rotate, crop, watermark, format,
     * udf, customized style, etc. The {@link GenericResult} instance returned
     * must be closed by the calller to release connection via calling
     * getResponse().getContent().close().
     * </p>
     * 
     * @param processObjectRequest
     *            A {@link ProcessObjectRequest} instance that specifies the
     *            bucket name, the object key and the process (such as
     *            image/resize,w_500)
     * @return A {@link GenericResult} instance which must be closed after the
     *         usage by the caller.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public GenericResult processObject(ProcessObjectRequest processObjectRequest) throws OSSException, ClientException;

    /**
     * Sets the request payment of the {@link Bucket}.
     * 
     * @param bucketName
     *             The bucket name.
     * @param payer
     *             The request payer setting
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void setBucketRequestPayment(String bucketName, Payer payer) throws OSSException, ClientException;

    /**
     * Sets the request payment of the {@link Bucket}.
     * 
     * @param setBucketRequestPaymentRequest
     *             A {@link SetBucketRequestPaymentRequest} instance  that has 
     *             the bucket name and payer setting.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void setBucketRequestPayment(SetBucketRequestPaymentRequest setBucketRequestPaymentRequest) throws OSSException, ClientException;

    /**
     * Gets the request payment of the {@link Bucket}.
     * 
     * @param bucketName
     *             The bucket name.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public GetBucketRequestPaymentResult getBucketRequestPayment(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the request payment of the {@link Bucket}.
     * 
     * @param genericRequest
     *             {@link GenericRequest} instance that has the bucket name.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public GetBucketRequestPaymentResult getBucketRequestPayment(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * sets the qos info for the {@link Bucket}.
     * 
     * @param bucketName
     *             The bucket name.
     * @param bucketQosInfo
     *             The bucket qos info setting
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void setBucketQosInfo(String bucketName, BucketQosInfo bucketQosInfo) throws OSSException, ClientException;

    /**
     * sets the qos info for the {@link Bucket}.
     * 
     * @param setBucketQosInfoRequest
     *             {@link SetBucketQosInfoRequest} instance that has the bucket name and bucket qos info.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void setBucketQosInfo(SetBucketQosInfoRequest setBucketQosInfoRequest) throws OSSException, ClientException;

    /**
     * Gets the bucket qos info of the {@link Bucket}.
     * 
     * @param bucketName
     *             The bucket name.
     * @return  A {@link BucketQosInfo} instance.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public BucketQosInfo getBucketQosInfo(String bucketName) throws OSSException, ClientException;

    /**
     * Gets the bucket qos info of the {@link Bucket}.
     * 
     * @param genericRequest
     *             {@link GenericRequest} instance that has the bucket name.
     * @return  A {@link BucketQosInfo} instance.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public BucketQosInfo getBucketQosInfo(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Deletes the bucket qos info
     * @param bucketName
     *            The bucket name
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteBucketQosInfo(String bucketName) throws OSSException, ClientException;
 
    /**
     * Deletes the bucket qos info
     * @param genericRequest
     *            A {@link GenericRequest} instance that has the bucket name
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void deleteBucketQosInfo(GenericRequest genericRequest) throws OSSException, ClientException;

    /**
     * Gets the User qos info 
     * 
     * @return  A {@link UserQosInfo} instance.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public UserQosInfo getUserQosInfo() throws OSSException, ClientException;

    /**
     * Creates UDF
     * 
     * @param createUdfRequest
     *            A {@link CreateUdfRequest} instance.
     * @throws OSSException
     *             OSS Server side exception.
     * @throws ClientException
     *             OSS Client side exception.
     */
    public void createUdf(CreateUdfRequest createUdfRequest) throws OSSException, ClientException;

    public UdfInfo getUdfInfo(UdfGenericRequest genericRequest) throws OSSException, ClientException;

    public List<UdfInfo> listUdfs() throws OSSException, ClientException;

    public void deleteUdf(UdfGenericRequest genericRequest) throws OSSException, ClientException;

    public void uploadUdfImage(UploadUdfImageRequest uploadUdfImageRequest) throws OSSException, ClientException;

    public List<UdfImageInfo> getUdfImageInfo(UdfGenericRequest genericRequest) throws OSSException, ClientException;

    public void deleteUdfImage(UdfGenericRequest genericRequest) throws OSSException, ClientException;

    public void createUdfApplication(CreateUdfApplicationRequest createUdfApplicationRequest)
            throws OSSException, ClientException;

    public UdfApplicationInfo getUdfApplicationInfo(UdfGenericRequest genericRequest)
            throws OSSException, ClientException;

    public List<UdfApplicationInfo> listUdfApplications() throws OSSException, ClientException;

    public void deleteUdfApplication(UdfGenericRequest genericRequest) throws OSSException, ClientException;

    public void upgradeUdfApplication(UpgradeUdfApplicationRequest upgradeUdfApplicationRequest)
            throws OSSException, ClientException;

    public void resizeUdfApplication(ResizeUdfApplicationRequest resizeUdfApplicationRequest)
            throws OSSException, ClientException;

    public UdfApplicationLog getUdfApplicationLog(GetUdfApplicationLogRequest getUdfApplicationLogRequest)
            throws OSSException, ClientException;

}
