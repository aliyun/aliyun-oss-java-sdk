*******************
生成预签名URL
*******************

如果你想把自己的资源发放给第三方用户访问，但是又不想开放Bucket的读权限，可以通过生成预签名URL的形式提供给用户一个临时的访问URL。在生成URL时，你可以指定URL过期的时间，从而限制用户长时间访问。

生成一个预签名的URL
============================

如下代码：

.. code-block:: java

    String bucketName = "your-bucket-name";
    String key = "your-object-key";
    
    // 设置URL过期时间为1小时
    Date expiration = new Date(new Date().getTime() + 3600 * 1000);

    // 生成URL
    URL url = client.generatePresignedUrl(bucketName, key, expiration);

生成的URL默认以GET方式访问，这样，用户可以直接通过浏览器访问相关内容。

生成其他Http方法的URL
==============================

如果你想允许用户临时进行其他操作（比如上传，删除Object），可能需要签名其他方法的URL，如下：

.. code-block:: java

    // 生成PUT方法的URL
    URL url = client.generatePresignedUrl(bucketName, key, expiration, HttpMethod.PUT);

通过传入 ``HttpMethod.PUT`` 参数，用户可以使用生成的URL上传Object。

添加用户自定义参数（UserMetadata）
============================================

如果你想使用签名的URL上传Object，并指定UserMetadata等参数，可以这样做：

.. code-block:: java
    
    // 创建请求
    GeneratePresignedUrlRequest generatePresignedUrlRequest = 
                                    new GeneratePresignedUrlRequest(bucketName, key);

    // HttpMethod为PUT
    generatePresignedUrlRequest.setMethod(HttpMethod.PUT);

    // 添加UserMetadata
    generatePresignedUrlRequest.addUserMetadata("key", "value");

    // 生成预签名的URL
    URL url = client.generatePresignedUrl(bucketName, key, expiration);

需要注意的是，上述过程只是生成了签名的URL，你仍需要在request header中添加UserMetadata的信息。

关于如何在Http请求中设置UserMetadata等参数，可以参考  `OSS REST API 文档`_ 中的相关内容。

.. _OSS REST API 文档: http://bbs.aliyun.com/job.php?spm=5176.383663.0.64.CvvLVx&action=download&aid=36111





