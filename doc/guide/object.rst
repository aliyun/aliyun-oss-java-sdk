*******
Object
*******

在OSS中，用户操作的基本数据单元是Object。单个Object最大允许存储5TB的数据。Object包含key、meta和data。其中，key是Object的名字；meta是用户对该object的描述，由一系列name-value对组成；data是Object的数据。


.. _object-name-rule:

命名规范
==================

Object的命名规范如下：

* 使用UTF-8编码
* 长度必须在1-1023字节之间
* 不能以“/”或者“\\”字符开头
* 不能含有“\\r”或者“\\n”的换行符

.. _put-object:

上传Object
============

最简单的上传
---------------

如下代码：

.. code-block:: java
    
    public void putObject(String bucketName, String key, String filePath) throws FileNotFoundException {

        // 初始化OSSClient
        OSSClient client = ...;

        // 获取指定文件的输入流
        File file = new File(filePath);
        InputStream content = new FileInputStream(file);

        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        meta.setContentLength(file.length());

        // 上传Object.
        PutObjectResult result = client.putObject(bucketName, key, content, meta);

        // 打印ETag
        System.out.println(result.getETag());    
    }

Object通过InputStream的形式上传到OSS中。在上面的例子里我们可以看出，每上传一个Object，都需要指定和Object关联的ObjectMetadata。ObjectMetaData是用户对该object的描述，由一系列name-value对组成；其中ContentLength是必须设置的，以便SDK可以正确识别上传Object的大小。

Put Object请求处理成功后，OSS会将收到文件的MD5值放在返回结果的ETag中。用户可以根据ETag检验上传的文件与本地的是否一致。

设定Object的Http Header
----------------------------

OSS Java SDK本质上是调用后台的HTTP接口，因此OSS服务允许用户自定义Object的Http Header。下面代码为Object设置了过期时间：

.. code-block:: java
    
    // 初始化OSSClient
    OSSClient client = ...;

    // 初始化上传输入流
    InputStream content = ...;

    // 创建上传Object的Metadata
    ObjectMetadata meta = new ObjectMetadata();

    // 设置ContentLength为1000
    meta.setContentLength(1000);

    // 设置1小时后过期
    Date expire = new Date(new Date().getTime() + 3600 * 1000);
    meta.setExpirationTime(expire);
    client.putObject(bucketName, key, content, meta);

Java SDK支持的Http Header有四种，分别为：*Cache-Control* 、 *Content-Disposition* 、*Content-Encoding* 、 *Expires* 。它们的相关介绍见 RFC2616_ 。


.. _RFC2616: http://www.ietf.org/rfc/rfc2616.txt


服务器端加密编码
-----------------------------

OSS 支持在服务器端对用户上传的数据进行加密编码（ Server-Side Encryption）：用户上传数据时，OSS 对收到的用户数据进行加密编码，然后再将编码得到的数据永久保存下来；用户下载数据时，OSS 自动对保存的编码数据进行解码并把原始数据返回给用户，并在返回的 HTTP 请求 Header 中声明该数据进行了服务器端加密编码。换句话说，下载一个进行服务器端加密编码的 Object和下载一个普通的 Object 没有多少区别，因为 OSS 会为用户管理整个编解码过程。 

可以在ObjectMeta中开启服务器端加密编码。（目前仅支持AES256）：

.. code-block:: java
    
        // 开启服务器端加密编码
        meta.setServerSideEncryption(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);


用户自定义元数据
--------------------------

OSS支持用户自定义元数据来对Object进行描述。比如：

.. code-block:: java

    // 设置自定义元数据name的值为my-data
    meta.addUserMetadata("name", "my-data");

    // 上传object
    client.putObject(bucketName, key, content, meta);

在上面代码中，用户自定义了一个名字为"name"，值为"my-data"的元数据。当用户下载此Object的时候，此元数据也可以一并得到。一个Object可以有多个类似的参数，但所有的user meta总大小不能超过2k。

分块上传
---------------------------

OSS允许用户将一个Object分成多个请求上传到后台服务器中，关于分块上传的内容，我们将在  :ref:`object-multipart` 这一章中做介绍。


.. _list-object:

列出Bucket中的Object
==========================

列出Object
-----------------
.. code-block:: java

    public void listObjects(String bucketName) {

        // 初始化OSSClient
        OSSClient client = ...;
        
        // 获取指定bucket下的所有Object信息
        ObjectListing listing = client.listObjects(bucketName);
        
        // 遍历所有Object
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            System.out.println(objectSummary.getKey());
        }
    }

listObjects方法会返回 ``ObjectListing`` 对象，``ObjectListing`` 对象包含了此次listObject请求的返回结果。其中我们可以通过 ``ObjetListing`` 中的 ``getObjectSummaries`` 方法获取所有Object的描述信息（List<OSSObjectSummary>）。

.. note::
    默认情况下，如果Bucket中的Object数量大于100，则只会返回100个Object， 且返回结果中 ``IsTruncated`` 为 false，并返回 ``NextMarker`` 作为下此读取的起点。若想增大返回Object数目，可以修改 ``MaxKeys`` 参数，或者使用 ``Marker`` 参数分次读取。另外如果使用iterator（迭代器）的方式，也可以将Object全部读取出来。

通过以下代码可以遍历一个Bucket下的所有Object：

.. code-block:: java

    public static void listObjectsWithIter(String bucketName, OSS client) {
        ObjectListing objectListing = client.listObjects(bucketName);
        Iterator<OSSObjectSummary> iterator = OSSObjects.withObjectListing(client, objectListing).iterator();
        while (iterator.hasNext()) {
            OSSObjectSummary objectSummary = (OSSObjectSummary)iterator.next();
            System.out.println(objectSummary.getKey());
        }
    }

或者：

.. code-block:: java

    public void listObjectsWithIter2(String bucketName, OSS client) {
        ObjectListing objectListing = client.listObjects(bucketName);
        for (OSSObjectSummary summary : OSSObjects.withObjectListing(client, objectListing)) {
            System.out.println(summary.getKey());
        }
    }


扩展参数
-------------

通常，我们可以通过设置ListObjectsRequest的参数来完成更强大的功能。比如：

.. code-block:: java

    // 构造ListObjectsRequest请求
    ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
    
    // 设置参数
    listObjectsRequest.setDelimiter("/");
    listObjectsRequest.setMarker("123");
    ...
    
    ObjectListing listing = client.listObjects(listObjectsRequest);

上面代码中我们调用了 ``listObjects`` 的一个重载方法，通过传入 ``ListObjectsRequest`` 来完成请求。通过 ``ListObjectsRequest`` 中的参数设置我们可以完成很多扩展的功能。下表列出了 ``ListObjectsRequest`` 中可以设置的参数名称和作用：

=============    ==============================================================================================================================
名称                  作用
=============    ==============================================================================================================================
Delimiter         是一个用于对Object名字进行分组的字符。所有名字包含指定的前缀且第一次出现Delimiter字符之间的object作为一组元素: CommonPrefixes。

Marker            设定结果从Marker之后按字母排序的第一个开始返回。

MaxKeys           限定此次返回object的最大数，如果不设定，默认为100，MaxKeys取值不能大于1000。

Prefix            限定返回的object key必须以Prefix作为前缀。注意使用prefix查询时，返回的key中仍会包含Prefix。
=============    ==============================================================================================================================

文件夹功能模拟
------------------

我们可以通过 ``Delimiter`` 和 ``Prefix`` 参数的配合模拟出文件夹功能。

``Delimiter`` 和 ``Prefix`` 的组合效果是这样的：如果把 ``Prefix`` 设为某个文件夹名，就可以罗列以此 ``Prefix`` 开头的文件，即该文件夹下递归的所有的文件和子文件夹。如果再把 ``Delimiter`` 设置为 "/" 时，返回值就只罗列该文件夹下的文件，该文件夹下的子文件名返回在 ``CommonPrefixes`` 部分，子文件夹下递归的文件和文件夹不被显示.

假设Bucket中有4个文件： ``oss.jpg`` ， ``fun/test.jpg`` ， ``fun/movie/001.avi`` ， ``fun/movie/007.avi`` ，我们把 "/" 符号作为文件夹的分隔符。

列出Bucket内所有文件
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

当我们需要获取Bucket下的所有文件时，可以这样写：

.. code-block:: java

    // 构造ListObjectsRequest请求
    ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
    
    // List Objects
    ObjectListing listing = client.listObjects(listObjectsRequest);

    // 遍历所有Object
    System.out.println("Objects:");
    for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
        System.out.println(objectSummary.getKey());
    }
    
    // 遍历所有CommonPrefix
    System.out.println("CommonPrefixs:");
    for (String commonPrefix : listing.getCommonPrefixes()) {
        System.out.println(commonPrefix);
    }

输出::
    
    Objects:
    fun/movie/001.avi
    fun/movie/007.avi
    fun/test.jpg
    oss.jpg

    CommonPrefixs:

递归列出目录下所有文件
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

我们可以通过设置 ``Prefix`` 参数来获取某个目录下所有的文件：

.. code-block:: java

    // 构造ListObjectsRequest请求
    ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
    
    // 递归列出fun目录下的所有文件    
    listObjectsRequest.setPrefix("fun/");
    
    ObjectListing listing = client.listObjects(listObjectsRequest);
    
    // 遍历所有Object
    System.out.println("Objects:");
    for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
        System.out.println(objectSummary.getKey());
    }
    
    // 遍历所有CommonPrefix
    System.out.println("\nCommonPrefixs:");
    for (String commonPrefix : listing.getCommonPrefixes()) {
        System.out.println(commonPrefix);
    }

输出::

    Objects:
    fun/movie/001.avi
    fun/movie/007.avi
    fun/test.jpg

    CommonPrefixs:

列出目录下的文件和子目录
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

在 ``Prefix`` 和 ``Delimiter`` 结合的情况下，可以列出目录下的文件和子目录：

.. code-block:: java

    // 构造ListObjectsRequest请求
    ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
    
    // "/" 为文件夹的分隔符
    listObjectsRequest.setDelimiter("/");

    // 列出fun目录下的所有文件和文件夹   
    listObjectsRequest.setPrefix("fun/");
    
    ObjectListing listing = client.listObjects(listObjectsRequest);
    
    // 遍历所有Object
    System.out.println("Objects:");
    for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
        System.out.println(objectSummary.getKey());
    }
    
    // 遍历所有CommonPrefix
    System.out.println("\nCommonPrefixs:");
    for (String commonPrefix : listing.getCommonPrefixes()) {
        System.out.println(commonPrefix);
    }

输出::

    Objects:
    fun/test.jpg

    CommonPrefixs:
    fun/movie/

返回的结果中， ``ObjectSummaries`` 的列表中给出的是fun目录下的文件。而 ``CommonPrefixs`` 的列表中给出的是fun目录下的所有子文件夹。可以看出 ``fun/movie/001.avi`` ， ``fun/movie/007.avi`` 两个文件并没有被列出来，因为它们属于 ``fun`` 文件夹下的 ``movie`` 目录。


获取Object
=============

简单的读取Object
-------------------

我们可以通过以下代码将Object读取到一个流中：

.. code-block:: java

    public void getObject(String bucketName, String key) throws IOException {

        // 初始化OSSClient
        OSSClient client = ...;

        // 获取Object，返回结果为OSSObject对象
        OSSObject object = client.getObject(bucketName, key);

        // 获取ObjectMeta
        ObjectMetadata meta = object.getObjectMetadata();

        // 获取Object的输入流
        InputStream objectContent = object.getObjectContent();

        // 处理Object
        ...

        // 关闭流
        objectContent.close();
    }

```OSSObject`` 包含了Object的各种信息，包含Object所在的Bucket、Object的名称、Metadata以及一个输入流。我们可以通过操作输入流将Object的内容读取到文件或者内存中。而ObjectMetadata包含了Object上传时定义的，ETag，Http Header以及自定义的元数据。

通过GetObjectRequest获取Object
--------------------------------------

为了实现更多的功能，我们可以通过使用 ``GetObjectRequest`` 来获取Object。

.. code-block:: java

    // 初始化OSSClient
    OSSClient client = ...;

    // 新建GetObjectRequest
    GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        
    // 获取0~100字节范围内的数据
    getObjectRequest.setRange(0, 100);

    // 获取Object，返回结果为OSSObject对象
    OSSObject object = client.getObject(getObjectRequest);


我们通过 ``getObjectRequest`` 的 ``setRange`` 方法设置了返回的Object的范围。我们可以用此功能实现文件的分段下载和断点续传。



.. _get-object-params:

GetObjectRequest可以设置以下参数：

===========================  =====================================================================================================================
参数                          说明
===========================  =====================================================================================================================
Range                        指定文件传输的范围。
ModifiedSinceConstraint      如果指定的时间早于实际修改时间，则正常传送文件。否则抛出304 Not Modified异常。
UnmodifiedSinceConstraint    如果传入参数中的时间等于或者晚于文件实际修改时间，则正常传输文件。否则抛出412 precondition failed异常
MatchingETagConstraints      传入一组ETag，如果传入期望的ETag和object的 ETag匹配，则正常传输文件。否则抛出412 precondition failed异常
NonmatchingEtagConstraints   传入一组ETag，如果传入的ETag值和Object的ETag不匹配，则正常传输文件。否则抛出304 Not Modified异常。
ResponseHeaderOverrides      自定义OSS返回请求中的一些Header。
===========================  =====================================================================================================================

修改 ``ResponseHeaderOverrides`` ， 它提供了一系列的可修改参数，可以自定义OSS的返回Header，如下表所示：

====================  ======================================================================
参数                   说明
====================  ======================================================================
ContentType           OSS返回请求的content-type头
ContentLanguage       OSS返回请求的content-language头
Expires               OSS返回请求的expires头
CacheControl          OSS返回请求的cache-control头
ContentDisposition    OSS返回请求的content-disposition头
ContentEncoding       OSS返回请求的content-encoding头
====================  ======================================================================

直接下载Object到文件
-------------------------------

我们可以通过下面的代码直接将Object下载到指定文件：

.. code-block:: java

    // 新建GetObjectRequest
    GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
    
    // 下载Object到文件
    ObjectMetadata objectMetadata = client.getObject(getObjectRequest, new File("/path/to/file"));

当使用上面方法将Object直接下载到文件时，方法返回ObjectMetadata对象。

只获取ObjectMetadata
---------------------------------

通过 ``getObjectMetadata`` 方法可以只获取ObjectMetadata而不获取Object的实体。如下代码所示：

.. code-block:: java

        ObjectMetadata objectMetadata = client.getObjectMetadata(bucketName, key);


删除Object
=============================

下面代码删除了一个Object:

.. code-block:: java

    public void deleteObject(String bucketName, String key) {
        // 初始化OSSClient
        OSSClient client = ...;
                
        // 删除Object
        client.deleteObject(bucketName, key);           
    }

拷贝Object
==============================


拷贝一个Object
------------------------

通过 ``copyObject`` 方法我们可以拷贝一个Object，如下面代码：

.. code-block:: java

    public void copyObject(String srcBucketName, String srcKey, String destBucketName, String destKey) {
        // 初始化OSSClient
        OSSClient client = ...;
                
        // 拷贝Object
        CopyObjectResult result = client.copyObject(srcBucketName, srcKey, destBucketName, destKey);
        
        // 打印结果
        System.out.println("ETag: " + result.getETag() + " LastModified: " + result.getLastModified());
    }

``copyObject`` 方法返回一个 ``CopyObjectResult`` 对象，对象中包含了新Object的ETag和修改时间。


通过CopyObjectRequest拷贝Object
---------------------------------------
        
也可以通过 ``CopyObjectRequest`` 实现Object的拷贝：


.. code-block:: java

    // 初始化OSSClient
    OSSClient client = ...;
    
    // 创建CopyObjectRequest对象
    CopyObjectRequest copyObjectRequest = new CopyObjectRequest(srcBucketName, srcKey, destBucketName, destKey);
    
    // 设置新的Metadata
    ObjectMetadata meta = new ObjectMetadata();
    meta.setContentType("text/html");
    copyObjectRequest.setNewObjectMetadata(meta);
    
    // 复制Object
    CopyObjectResult result = client.copyObject(copyObjectRequest);
    
    System.out.println("ETag: " + result.getETag() + " LastModified: " + result.getLastModified());

``CopyObjectRequest`` 允许用户修改目的Object的ObjectMeta，同时也提供 ``ModifiedSinceConstraint`` ， 
``UnmodifiedSinceConstraint`` ， ``MatchingETagConstraints`` ， ``NonmatchingEtagConstraints`` 四个参数的设定， 用法与 ``GetObjectRequest`` 的参数相似，参见 :ref:`GetObjectRequest的可设置参数 <get-object-params>`。











    