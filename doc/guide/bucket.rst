**********
Bucket
**********

Bucket是OSS上的命名空间，也是计费、权限控制、日志记录等高级功能的管理实体；Bucket名称在整个OSS服务中具有全局唯一性，且不能修改；存储在OSS上的每个Object必须都包含在某个Bucket中。一个应用，例如图片分享网站，可以对应一个或多个Bucket。一个用户最多可创建10个Bucket，但每个Bucket中存放的Object的数量和大小总和没有限制，用户不需要考虑数据的可扩展性。

.. _bucket-name-rule:

命名规范
===============

Bucket的命名有以下规范：

* 只能包括小写字母，数字，短横线（-）
* 必须以小写字母或者数字开头
* 长度必须在3-63字节之间

新建Bucket
================

如下代码可以新建一个Bucket：

.. code-block:: java

    String bucketName = "my-bucket-name";

    // 初始化OSSClient
    OSSClient client = ...;

    // 新建一个Bucket
    client.createBucket(bucketName);

由于Bucket的名字是全局唯一的，所以尽量保证你的 ``bucketName`` 不与别人重复。

列出用户所有的Bucket
==========================

下面代码可以列出用户所有的Bucket：

.. code-block:: java

    // 获取用户的Bucket列表
    List<Bucket> buckets = client.listBuckets();
    
    // 遍历Bucket
    for (Bucket bucket : buckets) {
        System.out.println(bucket.getName());
    }

判断Bucket是否存在
========================

有时候，我们的需求只是判断Bucket是否存在。则下面代码可以做到：

.. code-block:: java

    String bucketName = "your-bucket-name";

    // 获取Bucket的存在信息
    boolean exists = client.doesBucketExist(bucketName);

    // 输出结果
    if (exists) {
        System.out.println("Bucket exists");
    } else {
        System.out.println("Bucket not exists");           
    }

删除Bucket
================

下面代码删除了一个Bucket：

.. code-block:: java

    String bucketName = "your-bucket-name";

    // 删除Bucket
    client.deleteBucket(bucketName)

需要注意的是，如果Bucket不为空（Bucket中有Object），则Bucket无法删除，必须清空Bucket后才能成功删除。


Bucket权限控制
=======================

Bucket的访问权限
------------------------

OSS提供Bucket级别的权限访问控制，Bucket目前有三种访问权限：public-read-write，public-read和private。它们的含义如下：


* **public-read-write:** 任何人（包括匿名访问）都可以对该bucket中的object进行上传、下载和删除操作；所有这些操作产生的费用由该bucket的创建者承担，请慎用该权限。

* **public-read:** 只有该bucket的创建者可以对该bucket内的Object进行写操作（包括上传和删除）；任何人（包括匿名访问）可以对该bucket中的object进行读操作。

* **private:** 只有该bucket的创建者才可以访问此Bukcet。其他人禁止对此Bucket做任何操作。

用户新创建一个新Bucket时，如果不指定Bucket权限，OSS会自动为该Bucket设置private权限。对于一个已经存在的Bucket，只有它的创建者可以通过OSS的所提供的接口修改其访问权限。

修改Bucket的访问权限
----------------------------

下面代码将Bucket的权限设置为了private。

.. code-block:: java

        String bucketName = "your-bucket-name";
        client.setBucketAcl(bucketName, CannedAccessControlList.Private);

CannedAccessControlList是枚举类型，包含三个值： ``Private`` 、 ``PublicRead`` 、 ``PublicReadWrite`` ，它们分别对应相关权限。


















