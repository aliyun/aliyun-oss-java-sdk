.. _ossclient-top:

*********
OSSClient
*********

OSSClient是OSS服务的Java客户端，它为调用者提供了一系列的方法，用于和OSS服务进行交互。

新建OSSClient
================

新建一个OSSClient很简单，如下面代码所示：

.. code-block:: java

    String key = "<key>";
    String secret = "<secret>";
    OSSClient client = new OSSClient(key, secret);

上面的方式使用默认域名作为OSS的服务地址，如果你想自己指定域名，可以传入endpoint参数来指定。

.. code-block:: java

    String key = "<key>";
    String secret = "<secret>";
    String endpoint = "http://oss.aliyuncs.com";
    OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);

配置OSSClient
==============

如果你想配置OSSClient的一些细节的参数，可以在构造OSSClient的时候传入ClientConfiguration对象。
ClientConfiguration是OSS服务的配置类，可以为客户端配置代理，最大连接数等参数。

使用代理
----------

下面一段代码可以使客户端使用代理访问OSS服务：

.. code-block:: java
        
        // 创建ClientConfiguration实例
        ClientConfiguration conf = new ClientConfiguration();

        // 配置代理为本地8080端口
        conf.setProxyHost("127.0.0.1");
        conf.setProxyPort(8080);

        // 创建OSS客户端
        client = new OSSClient(endpoint, accessKeySecret, accessKeySecret, conf);

上面代码使得客户端的所有操作都会使用127.0.0.1地址的8080端口做代理执行。

对于有用户验证的代理，可以配置用户名和密码：

.. code-block:: java

    // 创建ClientConfiguration实例
    ClientConfiguration conf = new ClientConfiguration();

    // 配置代理为本地8080端口
    conf.setProxyHost("127.0.0.1");
    conf.setProxyPort(8080);

    //设置用户名和密码
    conf.setProxyUsername("username");
    conf.setProxyPassword("password");

设置网络参数
------------

我们可以用ClientConfiguration设置一些网络参数：

.. code-block:: java
    
    ClientConfiguration conf = new ClientConfiguration();

    // 设置HTTP最大连接数为10
    conf.setMaxConnections(10);

    // 设置TCP连接超时为5000毫秒
    conf.setConnectionTimeout(5000);

    // 设置最大的重试次数为3
    conf.setMaxErrorRetry(3);

    // 设置Socket传输数据超时的时间为2000毫秒
    conf.setSocketTimeout(2000);

ClientConfiguration所有参数
------------------------------

通过ClientConfiguration能指定的所有参数如下表所示：

================== =====================================================
参数                说明
================== =====================================================
UserAgent          用户代理，指HTTP的User-Agent头。默认为"aliyun-sdk-java"
ProxyHost          代理服务器主机地址
ProxyPort          代理服务器端口
ProxyUsername      代理服务器验证的用户名
ProxyPassword      代理服务器验证的密码
ProxyDomain        访问NTLM验证的代理服务器的Windows域名
ProxyWorkstation   NTLM代理服务器的Windows工作站名称
MaxConnections     允许打开的最大HTTP连接数。默认为50
SocketTimeout      通过打开的连接传输数据的超时时间（单位：毫秒）。默认为50000毫秒
ConnectionTimeout  建立连接的超时时间（单位：毫秒）。默认为50000毫秒
MaxErrorRetry      可重试的请求失败后最大的重试次数。默认为3次
================== =====================================================









