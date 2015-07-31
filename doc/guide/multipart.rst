.. _object-multipart:

********************
Object的分块上传
********************

除了通过putObject接口上传文件到OSS以外，OSS还提供了另外一种上传模式 —— Multipart Upload。用户可以在如下的应用场景内（但不仅限于此），使用Multipart Upload上传模式，如：

* 需要支持断点上传。
* 上传超过100MB大小的文件。
* 网络条件较差，和OSS的服务器之间的链接经常断开。
* 需要流式地上传文件。
* 上传文件之前，无法确定上传文件的大小。

下面我们将一步步介绍怎样实现Multipart Upload。

分步完成Multipart Upload
=================================

假设我们有一个文件，本地路径为 ``/path/to/file.zip`` 由于文件比较大，我们将其分块传输到OSS中。 

1. 初始化Multipart Upload
--------------------------

我们使用 ``initiateMultipartUpload`` 方法来初始化一个分块上传事件：

.. code-block:: java

    String bucketName = "your-bucket-name";
    String key = "your-key";
    
    // 初始化OSSClient
    OSSClient client = ...;
    
    // 开始Multipart Upload
    InitiateMultipartUploadRequest initiateMultipartUploadRequest = 
                                        new InitiateMultipartUploadRequest(bucketName, key);
    InitiateMultipartUploadResult initiateMultipartUploadResult =  
                                        client.initiateMultipartUpload(initiateMultipartUploadRequest);
    
    // 打印UploadId
    System.out.println("UploadId: " + initiateMultipartUploadResult.getUploadId());

我们用 ``InitiateMultipartUploadRequest`` 来指定上传Object的名字和所属Bucket。在 ``InitiateMultipartUploadRequest`` 中，你也可以设置 ``ObjectMetadata`` ，但是不必指定其中的 ``ContentLength`` （指定了也无效）。

``initiateMultipartUpload`` 的返回结果中含有 ``UploadId`` ，它是区分分块上传事件的唯一标识，在后面的操作中，我们将用到它。

2. 上传分块
-----------------------

接着，我们把文件分块上传。

.. code-block:: java

    // 设置每块为 5M
    final int partSize = 1024 * 1024 * 5; 

    File partFile = new File("/path/to/file.zip");
    
    // 计算分块数目
    int partCount = (int) (partFile.length() / partSize);
    if (partFile.length() % partSize != 0){
        partCount++;
    }
    
    // 新建一个List保存每个分块上传后的ETag和PartNumber
    List<PartETag> partETags = new ArrayList<PartETag>();

    for(int i = 0; i < partCount; i++){
        // 获取文件流
        FileInputStream fis = new FileInputStream(partFile);
        
        // 跳到每个分块的开头
        long skipBytes = partSize * i;
        fis.skip(skipBytes);
        
        // 计算每个分块的大小
        long size = partSize < partFile.length() - skipBytes ?
                partSize : partFile.length() - skipBytes;

        // 创建UploadPartRequest，上传分块
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(bucketName);
        uploadPartRequest.setKey(key);
        uploadPartRequest.setUploadId(initiateMultipartUploadResult.getUploadId());
        uploadPartRequest.setInputStream(fis);
        uploadPartRequest.setPartSize(size);
        uploadPartRequest.setPartNumber(i + 1);
        UploadPartResult uploadPartResult = client.uploadPart(uploadPartRequest);
        
        // 将返回的PartETag保存到List中。
        partETags.add(uploadPartResult.getPartETag());
        
        // 关闭文件
        fis.close();
    }

上面程序的核心是调用 ``uploadPart`` 方法来上传每一个分块，但是要注意以下几点：

* ``uploadPart`` 方法要求除最后一个Part以外，其他的Part大小都要大于5MB。但是Upload Part接口并不会立即校验上传Part的大小（因为不知道是否为最后一块）；只有当Complete Multipart Upload的时候才会校验。
* OSS会将服务器端收到Part数据的MD5值放在ETag头内返回给用户。为了保证数据在网络传输过程中不出现错误，强烈推荐用户在收到OSS的返回请求后，用该MD5值验证上传数据的正确性。
* Part号码的范围是1~10000。如果超出这个范围，OSS将返回InvalidArgument的错误码。
* 每次上传part时都要把流定位到此次上传块开头所对应的位置。
* 每次上传part之后，OSS的返回结果会包含一个 ``PartETag`` 对象，他是上传块的ETag与块编号（PartNumber）的组合，在后续完成分块上传的步骤中会用到它，因此我们需要将其保存起来。一般来讲我们将这些 ``PartETag`` 对象保存到List中。


3. 完成分块上传
--------------------

完成分块上传很简单，如下：

.. code-block:: java

    CompleteMultipartUploadRequest completeMultipartUploadRequest =
            new CompleteMultipartUploadRequest(bucketName, key, initiateMultipartUploadResult.getUploadId(), partETags);

    // 完成分块上传
    CompleteMultipartUploadResult completeMultipartUploadResult =
            client.completeMultipartUpload(completeMultipartUploadRequest);

    // 打印Object的ETag
    System.out.println(completeMultipartUploadResult.getETag());

上面代码中的 ``partETags`` 就是第二部中保存的partETag的列表，OSS收到用户提交的Part列表后，会逐一验证每个数据Part的有效性。当所有的数据Part验证通过后，OSS将把这些数据part组合成一个完整的Object。

``completeMultipartUpload`` 方法的返回结果中会包含拼装后Object的ETag，用户可以和本地文件的MD5值进行校验以保证数据的有效性。

取消分块上传事件
======================
 
我们可以用 ``abortMultipartUpload`` 方法取消分块上传。

.. code-block:: java
    
    AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest(bucketName, key, uploadId);
    
    // 取消分块上传
    client.abortMultipartUpload(abortMultipartUploadRequest);

获取Bucket内所有分块上传事件
==============================

我们可以用 ``listMultipartUploads`` 方法获取Bucket内所有上传事件。

.. code-block:: java

        // 获取Bucket内所有上传事件
        MultipartUploadListing listing = client.listMultipartUploads(listMultipartUploadsRequest);
        
        // 遍历所有上传事件
        for (MultipartUpload multipartUpload : listing.getMultipartUploads()) {
            System.out.println("Key: " + multipartUpload.getKey() + " UploadId: " + multipartUpload.getUploadId());
        }

.. note::
    默认情况下，如果Bucket中的分块上传事件的数量大于1000，则只会返回1000个Object， 且返回结果中 ``IsTruncated`` 为 false，并返回 ``NextKeyMarker`` 和 ``NextUploadMarker`` 作为下此读取的起点。若想增大返回分块上传事件数目，可以修改 ``MaxUploads`` 参数，或者使用 ``KeyMarker`` 以及 ``UploadIdMarker`` 参数分次读取。

获取所有已上传的块信息
==============================

我们可以用 ``listParts`` 方法获取某个上传事件所有已上传的块。

.. code-block:: java

    ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, key, uploadId);
    
    // 获取上传的所有Part信息
    PartListing partListing = client.listParts(listPartsRequest);
    
    // 遍历所有Part
    for (PartSummary part : partListing.getParts()) {
        System.out.println("PartNumber: " + part.getPartNumber() + " ETag: " + part.getETag());
    }

.. note::
    默认情况下，如果Bucket中的分块上传事件的数量大于1000，则只会返回1000个Object， 且返回结果中 ``IsTruncated`` 为 false，并返回 ``NextPartNumberMarker`` 作为下此读取的起点。若想增大返回分块上传事件数目，可以修改 ``MaxParts`` 参数，或者使用 ``PartNumberMarker`` 参数分次读取。






